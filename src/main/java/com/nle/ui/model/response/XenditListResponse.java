package com.nle.ui.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class XenditListResponse {
    private Long bookingId;
    private String bookingType;
    private String bankCode;
    private String va;
    private String expiryDate;
    private Long amount;
    private String invoiceUrl;
    private String status;
}
