package com.nle.controller.ftp;

import com.nle.service.ftp.FTPService;
import com.nle.service.tax.TaxMinistryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FTPController {
    private final FTPService ftpService;
    private final TaxMinistryService taxMinistryService;

    @GetMapping(value = "/api/ftp/download")
    public void syncDataFromFtpServer() {
        ftpService.syncDataFromFtpServer();
    }

    @GetMapping(value = "/api/ftp/sync")
    public void syncDataToTaxMinistry() {
        taxMinistryService.syncDataToTaxMinistry();
    }

}
