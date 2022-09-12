package com.nle.shared.service.ftp;

import com.nle.config.prop.AppProperties;
import com.nle.constant.enums.AccountStatus;
import com.nle.constant.enums.GateMoveSource;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.entity.FtpFile;
import com.nle.io.entity.GateMove;
import com.nle.io.repository.FtpFileRepository;
import com.nle.io.repository.GateMoveRepository;
import com.nle.shared.service.depoOwner.DepoOwnerAccountService;
import com.nle.shared.dto.ftp.FtpMoveDTOError;
import com.nle.shared.dto.ftp.MoveDTO;
import com.nle.shared.service.inventory.InventoryService;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.nle.constant.AppConstant.MEMBER_FIELDS_TO_BIND_TO;
import static com.nle.util.NleUtil.GATE_IN;
import static com.nle.util.NleUtil.convertToGateMoveEntity;

@Service
@RequiredArgsConstructor
public class FTPService {
    private final Logger log = LoggerFactory.getLogger(FTPService.class);
    private final DepoOwnerAccountService depoOwnerAccountService;
    private final AppProperties appProperties;
    private final FtpFileRepository ftpFileRepository;
    private final GateMoveRepository gateMoveRepository;
    private final Validator validator;
    private final InventoryService inventoryService;

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
                        // emailService.sendFTPSynErrorEmail(depoOwnerAccount, errors);
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
                    //TODO INFO FILE NAME IS ALREADY USED
                    log.info("Ignore processed file {}", ftpFile.getName());
                } else {
                    try (OutputStream os = new FileOutputStream(ftpFile.getName())) {
                        // Download file from FTP server.
                        boolean status = client.retrieveFile(ftpFile.getName(), os);
                        log.info("Status of retrieveFile() {}", status);
                        log.info("Processing for file {}", ftpFile.getName());
                        Path localFilePath = Paths.get(ftpFile.getName());
                        Reader reader = Files.newBufferedReader(localFilePath);
                        ColumnPositionMappingStrategy<MoveDTO> strategy = new ColumnPositionMappingStrategy<MoveDTO>();
                        strategy.setType(MoveDTO.class);
                        strategy.setColumnMapping(MEMBER_FIELDS_TO_BIND_TO);

                        CsvToBean csvToBean = new CsvToBeanBuilder(reader)
                            .withMappingStrategy(strategy)
                            .withSeparator(';')
                            .withSkipLines(1)
                            .withIgnoreLeadingWhiteSpace(true)
                            .build();
                        try {
                            Iterator<MoveDTO> ftpMoveDTOIterator = csvToBean.iterator();
                            while (ftpMoveDTOIterator.hasNext()) {
                                MoveDTO moveDTO = ftpMoveDTOIterator.next();
                                // validate the record
                                Set<ConstraintViolation<MoveDTO>> constraintViolations = validator.validate(moveDTO);
                                if (!constraintViolations.isEmpty()) {
                                    String errorMessage = constraintViolations.stream()
                                        .map(ConstraintViolation::getMessage)
                                        .collect(Collectors.joining(", "));
                                    errors.add(new FtpMoveDTOError(moveDTO, errorMessage));
                                    continue;
                                }
                                try {
                                    GateMove entity = convertToGateMoveEntity(moveDTO, GateMoveSource.FTP_SERVER);
                                    entity.setDepoOwnerAccount(depoOwnerAccount);
                                    gateMoveRepository.save(entity);

                                    //trigger inventory
                                    if (entity.getGateMoveType().equalsIgnoreCase(GATE_IN)) {
                                        inventoryService.triggerCreateInventory(entity);
                                    }

                                } catch (Exception e) {
                                    log.error("Error while importing gate move data {} {}", moveDTO, e);
                                }

                            }
                            FtpFile newFile = new FtpFile();
                            newFile.setFileName(depoOwnerAccount.getCompanyEmail() + "_" + ftpFile.getName());
                            newFile.setFileSize(ftpFile.getSize());
                            newFile.setImportDate(LocalDateTime.now());
                            newFile.setDepoOwnerAccount(depoOwnerAccount);
                            ftpFileRepository.save(newFile);
                            //delete in local
                            Files.deleteIfExists(localFilePath);
                        } catch (Exception exception) {
                            log.error("Error while sync data from ftp server FTP server", exception);
                        }
                    } catch (IOException e) {
                        log.error("Error while sync data from ftp server FTP server", e);
                    }

                    //delete in ftp server
                    try {
                        client.deleteFile(ftpFile.getName());
                    } catch (IOException e) {
//                        throw new RuntimeException(e);
                        log.error("Error while delete file in ftp server", e);
                    }
                }
            }
        }
    }

    private boolean validation () {
        //TODO validation for check ftp file is true
        return true;
    }

}
