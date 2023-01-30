package com.nle.ui.model.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FormInvoiceDTO {
    private String noInvoice;
    private String depoName;
    private String depoAddress;
    private String consignee;
    private String npwp;
    private String bol;
    private String bookingId;
    private String createdDate;
    private String paidStatus;
    private String cancelStatus;
    private String paymentId;
    private String fullName;
    private String phone;
    private String email;
    private String amount;
    private String bank;
    private String va;
    private String lastModified;
}
