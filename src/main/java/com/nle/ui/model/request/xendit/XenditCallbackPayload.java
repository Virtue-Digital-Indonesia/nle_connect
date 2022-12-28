package com.nle.ui.model.request.xendit;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class XenditCallbackPayload {

    private String id;
    private String payment_id;
    private String callback_virtual_account_id;
    private String owner_id;
    private String external_id;
    private String account_number;
    private String bank_code;
    private String transaction_timestamp;
    private int amount;
    private String merchant_code;
    private String currency;
    private String sender_name;
    private XenditDetailPayload payment_detail;
}
