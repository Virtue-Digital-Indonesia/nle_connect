package com.nle.ui.model.request.xendit;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class XenditRequest {
    private String user_xendit_id;
    private String back_code;
    private String name;
    private String phone_number;
    private Number expected_amount;
}
