package com.nle.ui.model.request.xendit;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class XenditCallbackPayload {

    private String id;
    private String user_id;
    private String external_id;
    private Boolean is_high;
    private String status;
    private String merchant_name;
    private int amount;
    private String created;
    private String updated;
    private String description;
    private int paid_amount;
    private String payment_method;
    private String bank_code;
    private String currency;
    private int initial_amount;
    private String paid_at;
    private String payment_channel;
    private String payment_destination;
}
