package com.nle.ui.model.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VerifOTPRequest {
    private String OTP;
    private String phoneNumber;
}
