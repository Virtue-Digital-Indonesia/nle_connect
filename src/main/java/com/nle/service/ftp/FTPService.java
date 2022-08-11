package com.nle.service.ftp;

import com.nle.config.prop.AppProperties;
import com.nle.constant.AccountStatus;
import com.nle.constant.AppConstant;
import com.nle.constant.GateMoveSource;
import com.nle.entity.DepoOwnerAccount;
import com.nle.entity.FtpFile;
import com.nle.entity.GateMove;
import com.nle.repository.FtpFileRepository;
import com.nle.repository.GateMoveRepository;
import com.nle.service.depoOwner.DepoOwnerAccountService;
import com.nle.service.dto.ftp.FtpMoveDTO;
import com.nle.service.dto.ftp.FtpMoveDTOError;
import com.nle.service.email.EmailService;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.nle.constant.AppConstant.MEMBER_FIELDS_TO_BIND_TO;

@Service
@RequiredArgsConstructor
public class FTPService {
    private final Logger log = LoggerFactory.getLogger(FTPService.class);

    public static final String GATE_IN = "gate_in";
    public static final String GATE_IN_EMPTY = "gate_in_empty";
    public static final String GATE_OUT = "gate_out";
    public static final String GATE_OUT_EMPTY = "gate_out_empty";

    private final DepoOwnerAccountService depoOwnerAccountService;
    private final AppProperties appProperties;
    private final FtpFileRepository ftpFileRepository;
    private final GateMoveRepository gateMoveRepository;
    private final Validator validator;
    private final EmailService emailService;

    @Scheduled(cron = "${app.scheduler.ftp-sync-cron}")
    public void syncDataFromFtpServer() {
        // find all active depo owner account
        List<DepoOwnerAccount> activeDepoOwner = depoOwnerAccountService.findAllByStatus(AccountStatus.ACTIVE);
        if (activeDepoOwner.isEmpty()) {
            log.info("There is no active depo owner");
            return;
        }
        FTPClient ftpClient = new FTPClient();
        for (DepoOwnerAccount depoOwnerAccount : activeDepoOwner) {
            try {
                ftpClient.connect(appProperties.getSecurity().getFtp().getServer());
                ftpClient.enterLocalPassiveMode();
                byte[] decodedBytes = Base64.getDecoder().decode(depoOwnerAccount.getFtpPassword());
                String rawPassword = new String(decodedBytes);
                log.info("Authenticating for account {} with FTP folder {}", depoOwnerAccount.getCompanyEmail(), depoOwnerAccount.getFtpFolder());
                boolean login = ftpClient.login(depoOwnerAccount.getCompanyEmail(), rawPassword);
                if (login) {
                    log.info("Login success...");
                    String path = appProperties.getSecurity().getFtp().getPath();
                    log.info("FTP file path {}", path);
                    int cwd = ftpClient.cwd(path);
                    log.info("CWD {}", cwd);
                    FTPFile[] ftpFiles = ftpClient.listFiles(".");
                    log.info("Working directory {}", ftpClient.printWorkingDirectory());
                    log.info("Total file in folder {} from FTP server {}", path, ftpFiles.length);
                    // Download file from FTP server.
                    List<FtpMoveDTOError> errors = new ArrayList<>();
                    processFiles(ftpFiles, ftpClient, depoOwnerAccount, errors);
                    if (!errors.isEmpty()) {
                        // send email to depo owner
                        emailService.sendFTPSynErrorEmail(depoOwnerAccount, errors);
                    }
                } else {
                    log.error("Can not login to FTP server");
                }
            } catch (IOException e) {
                log.error("Error while sync data from ftp server FTP server", e);
            } finally {
                try {
                    ftpClient.disconnect();
                } catch (IOException e) {
                    log.error("Error while sync data from ftp server FTP server", e);
                }
            }
        }
    }

    private void processFiles(FTPFile[] ftpFiles, FTPClient client, DepoOwnerAccount depoOwnerAccount, List<FtpMoveDTOError> errors) {
        for (FTPFile ftpFile : ftpFiles) {
            if (ftpFile.getName().endsWith(".csv")) {
                List<FtpFile> allByFileName = ftpFileRepository.findAllByFileName(depoOwnerAccount.getCompanyEmail() + "_" + ftpFile.getName());
                if (!allByFileName.isEmpty()) {
                    log.info("Ignore processed file {}", ftpFile.getName());
                } else {
                    try (OutputStream os = new FileOutputStream(ftpFile.getName())) {
                        // Download file from FTP server.
                        boolean status = client.retrieveFile(ftpFile.getName(), os);
                        log.info("Status of retrieveFile() {}", status);
                        log.info("Processing for file {}", ftpFile.getName());
                        Reader reader = Files.newBufferedReader(Paths.get(ftpFile.getName()));
                        ColumnPositionMappingStrategy<FtpMoveDTO> strategy = new ColumnPositionMappingStrategy<FtpMoveDTO>();
                        strategy.setType(FtpMoveDTO.class);
                        strategy.setColumnMapping(MEMBER_FIELDS_TO_BIND_TO);

                        CsvToBean csvToBean = new CsvToBeanBuilder(reader)
                            .withMappingStrategy(strategy)
                            .withSeparator(';')
                            .withSkipLines(1)
                            .withIgnoreLeadingWhiteSpace(true)
                            .build();
                        try {
                            Iterator<FtpMoveDTO> ftpMoveDTOIterator = csvToBean.iterator();
                            while (ftpMoveDTOIterator.hasNext()) {
                                FtpMoveDTO ftpMoveDTO = ftpMoveDTOIterator.next();
                                // validate the record
                                Set<ConstraintViolation<FtpMoveDTO>> constraintViolations = validator.validate(ftpMoveDTO);
                                if(!constraintViolations.isEmpty()) {
                                    String errorMessage = constraintViolations.stream()
                                        .map(ConstraintViolation::getMessage)
                                        .collect(Collectors.joining(", "));
                                    errors.add(new FtpMoveDTOError(ftpMoveDTO, errorMessage));
                                    continue;
                                }
                                try {
                                    GateMove entity = convertToEntity(ftpMoveDTO);
                                    entity.setDepoOwnerAccount(depoOwnerAccount);
                                    gateMoveRepository.save(entity);
                                } catch (Exception e) {
                                    log.error("Error while importing gate move data {} {}", ftpMoveDTO, e);
                                }

                            }
                            FtpFile newFile = new FtpFile();
                            newFile.setFileName(depoOwnerAccount.getCompanyEmail() + "_" + ftpFile.getName());
                            newFile.setFileSize(ftpFile.getSize());
                            newFile.setImportDate(LocalDateTime.now());
                            newFile.setDepoOwnerAccount(depoOwnerAccount);
                            ftpFileRepository.save(newFile);
                        } catch (Exception exception) {
                            log.error("Error while sync data from ftp server FTP server", exception);
                        }
                    } catch (IOException e) {
                        log.error("Error while sync data from ftp server FTP server", e);
                    }
                }
            }
        }
    }

    private GateMove convertToEntity(FtpMoveDTO ftpMoveDTO) {
        GateMove gateMove = new GateMove();
        if (ftpMoveDTO.getTx_date() != null) {
            gateMove.setTxDate(ftpMoveDTO.getTx_date());
            gateMove.setTxDateFormatted(formatTxDate(ftpMoveDTO.getTx_date()));
        }
        gateMove.setProcessType(ftpMoveDTO.getProcess_type());
        gateMove.setGateMoveType(transformProcessTypeToGateMoveType(ftpMoveDTO.getProcess_type()));
        gateMove.setDepot(ftpMoveDTO.getDepot());
        gateMove.setFleetManager(ftpMoveDTO.getFleet_manager());
        gateMove.setContainerNumber(ftpMoveDTO.getContainer_number());
        gateMove.setIsoCode(ftpMoveDTO.getIso_code());
        gateMove.setCondition(ftpMoveDTO.getCondition());
        gateMove.setDateManufacturer(ftpMoveDTO.getDate_manufacturer());
        gateMove.setClean("yes".equalsIgnoreCase(ftpMoveDTO.getClean()) || "true".equalsIgnoreCase(ftpMoveDTO.getClean()));
        gateMove.setGrade(ftpMoveDTO.getGrade());
        gateMove.setOrderNumber(ftpMoveDTO.getOrder_number());
        gateMove.setCustomer(ftpMoveDTO.getCustomer());
        gateMove.setVessel(ftpMoveDTO.getVessel());
        gateMove.setVoyage(ftpMoveDTO.getVoyage());
        gateMove.setDiscargePort(ftpMoveDTO.getDiscarge_port());
        gateMove.setDeliveryPort(ftpMoveDTO.getDelivery_port());
        gateMove.setCarrier(ftpMoveDTO.getCarrier());
        gateMove.setTransportNumber(ftpMoveDTO.getTransport_number());
        gateMove.setDriverName(ftpMoveDTO.getDriver_name());
        gateMove.setTare(Double.valueOf(ftpMoveDTO.getTare()));
        gateMove.setPayload(Double.valueOf(ftpMoveDTO.getPayload()));
        gateMove.setMaxGross(Double.valueOf(ftpMoveDTO.getMax_gross()));
        gateMove.setRemarks(ftpMoveDTO.getRemark());
        gateMove.setSource(GateMoveSource.FTP_SERVER);
        gateMove.setStatus(AppConstant.Status.WAITING);
        gateMove.setNleId(UUID.randomUUID().toString());
        return gateMove;
    }

    private String transformProcessTypeToGateMoveType(String processType) {
        if (processType == null) {
            return null;
        }
        if (GATE_IN.equals(processType) || GATE_IN_EMPTY.equals(processType)) {
            return GATE_IN;
        }
        if (GATE_OUT.equals(processType) || GATE_OUT_EMPTY.equals(processType)) {
            return GATE_OUT;
        }
        return null;
    }

    private LocalDateTime formatTxDate(String txDate) {
        String replace = txDate.replace(AppConstant.TIME_ZONE, "");
        try {
            return LocalDateTime.parse(replace);
        } catch (Exception exception) {
            log.error("Can not format transaction date {}", txDate);
            return null;
        }
    }

}
