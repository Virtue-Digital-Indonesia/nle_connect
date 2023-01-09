package com.nle.ui.model.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FormInvoiceDTO {
    private String noInvoice;
    private String bookingId;
    private String createdDate;
    private String paymentStatus;
    private String paymentId;
    private String fullName;
    private String phone;
    private String email;
    private String amount;
    private String bank;
    private String va;
}
