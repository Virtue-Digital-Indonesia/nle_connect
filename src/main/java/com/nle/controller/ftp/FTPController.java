package com.nle.controller.ftp;

import com.nle.service.ftp.FTPService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FTPController {
    private final FTPService ftpService;

    @GetMapping(value = "/api/ftp/download")
    public String test() {
        ftpService.syncDataFromFtpServer();
        return "";
    }

}
