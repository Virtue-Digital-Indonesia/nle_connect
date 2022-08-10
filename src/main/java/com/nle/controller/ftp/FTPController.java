package com.nle.controller.ftp;

import com.nle.controller.depo.InventoryController;
import com.nle.service.ftp.FTPService;
import com.nle.service.tax.TaxMinistryService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
public class FTPController {
    private static final Logger LOGGER = LoggerFactory.getLogger(InventoryController.class);
    private final FTPService ftpService;
    private final TaxMinistryService taxMinistryService;

    @GetMapping(value = "/api/ftp/sync-data-from-ftp-server")
    public void syncDataFromFtpServer() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        ftpService.syncDataFromFtpServer();
        stopWatch.stop();
        LOGGER.info("Finished sync data from FTP Server in {} seconds", stopWatch.getTime(TimeUnit.SECONDS));
    }

    @GetMapping(value = "/api/ftp/sync-data-to-tax-ministry")
    public void syncDataToTaxMinistry() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        taxMinistryService.syncDataToTaxMinistry();
        stopWatch.stop();
        LOGGER.info("Finished sync data to tax ministry Server in {} seconds", stopWatch.getTime(TimeUnit.SECONDS));

    }

}
