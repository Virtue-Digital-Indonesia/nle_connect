package com.nle.constant;

import lombok.Getter;

@Getter
public enum AccountStatus {
    ACTIVE,
    INACTIVE,
    MISSING,
    WAITING_FOR_APPROVE,
    DELETE;
}
