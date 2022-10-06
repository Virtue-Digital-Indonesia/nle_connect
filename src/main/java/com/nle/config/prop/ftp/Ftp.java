package com.nle.config.prop.ftp;

import lombok.Data;

@Data
public class Ftp {
    private String server;
    private String path;
    private String triggerUrl;
    private String triggerToken;
    private String ftpUsername;
    private String ftpPassword;
}
