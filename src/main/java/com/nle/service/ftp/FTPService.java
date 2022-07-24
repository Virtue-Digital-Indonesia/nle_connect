package com.nle.service.ftp;

import com.nle.config.prop.AppProperties;
import com.nle.entity.FtpFile;
import com.nle.repository.FtpFileRepository;
import com.nle.service.dto.ftp.FtpMoveDTO;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FTPService {
    public static final String[] MEMBER_FIELDS_TO_BIND_TO = new String[]{
        "tx_date", "process_type", "depot", "fleet_manager", "container_number"
        , "iso_code", "condition", "date_manufacturer", "clean", "grade"
        , "order_number", "customer", "vessel", "voyage", "discarge_port"
        , "delivery_port", "carrier", "transport_number", "driver_name"
        , "tare", "payload", "max_gross", "remark"};
    private final Logger log = LoggerFactory.getLogger(FTPService.class);
    private final AppProperties appProperties;
    private final FtpFileRepository ftpFileRepository;

    public void syncDataFromFtpServer() {
        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(appProperties.getSecurity().getFtp().getServer());
            boolean login = ftpClient.login(appProperties.getSecurity().getFtp().getUsername()
                , appProperties.getSecurity().getFtp().getPassword());
            if (login) {
                log.info("Login success...");
                FTPFile[] ftpFiles = ftpClient.listFiles(appProperties.getSecurity().getFtp().getPath());
                log.info("Total file in folder {} from FTP server {}", appProperties.getSecurity().getFtp().getPath(), ftpFiles.length);
                // Download file from FTP server.
                ftpClient.changeWorkingDirectory(appProperties.getSecurity().getFtp().getPath());
                processFiles(ftpFiles, ftpClient);
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

    private void processFiles(FTPFile[] ftpFiles, FTPClient client) {
        for (FTPFile ftpFile : ftpFiles) {
            if (ftpFile.getName().endsWith(".csv")) {
                Optional<FtpFile> optionalFtpFile = ftpFileRepository.findByFileName(ftpFile.getName());
                if (optionalFtpFile.isPresent()) {
                    log.info("Ignore processed file {}", ftpFile.getName());
                } else {
                    try (OutputStream os = new FileOutputStream(ftpFile.getName())) {
                        // Download file from FTP server.
                        boolean status = client.retrieveFile(ftpFile.getName(), os);
                        log.info("Status of retrieveFile() {}", status);
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
                        Iterator<FtpMoveDTO> ftpMoveDTOIterator = csvToBean.iterator();
                        while (ftpMoveDTOIterator.hasNext()) {
                            FtpMoveDTO ftpMoveDTO = ftpMoveDTOIterator.next();
                            System.out.println(ftpMoveDTO);
                        }
                        System.out.println(csvToBean);
                    } catch (IOException e) {
                        log.error("Error while sync data from ftp server FTP server", e);
                    }
                }
            }
        }
    }
}
