package com.nle.ui.model.request.xendit;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
public class XenditDisCallbackPayload {

    private String id;
    private int amount;
    private String status;
    private String created;
    private String updated;
    private String user_id;
    private String bank_code;
    private boolean is_instant;
    private String external_id;
    private String account_holder_name;
    private String disbursement_description;

}