package com.nle.constant;

import lombok.Getter;

@Getter
public enum VerificationType {
    ACTIVE_ACCOUNT("A"),
    RESET_PASSWORD("R");

    private String code;

    VerificationType(String code) {
        this.code = code;
    }
}
