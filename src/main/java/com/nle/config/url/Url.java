package com.nle.config.url;

import lombok.Data;

@Data
public class Url {
    private String activeUrl;
    private String successRedirectUrl;
    private String failedRedirectUrl;
}
