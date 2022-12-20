package com.nle.ui.model.request.xendit;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class XenditRequest {
    private String back_code;
    private String name;
    private String phone_number;
    private Boolean is_closed = true;
    private Number expected_amount;
    private String expiration_date;
    private String description;
}
