package com.nle.constant;

import lombok.Getter;

@Getter
public enum EmailType {
    ACTIVE_DEPO_OWNER,
    INVITE_DEPO_WORKER,
    APPROVE_DEPO_WORKER,
    FTP_SYNC_ERROR
}
