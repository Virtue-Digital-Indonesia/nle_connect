package com.nle.ui.model.request.xendit;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class XenditRequest {
    private Long depo_id;
    private Long booking_header_id;
    private String back_code;
    private String name;
    private String phone_number;
    private Number expected_amount;
}
