package com.nle.config.prop.url;

import lombok.Data;

@Data
public class Url {
    private String activeUrl;
    private String successRedirectUrl;
    private String failedRedirectUrl;
    private String impersonateUrl;
    private String taxMinistry;
}
