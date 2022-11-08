package com.nle.ui.model.response.order;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class OrderHeaderResponse {

    private Long id;
    private String tx_date;
    private String full_name;
    private String phone_number;
    private String email;
    private String bill_landing;
    private String consignee;
    private String npwp;
    private String npwp_address;
    private String payment_method;
    private String order_status;
    private List<OrderDetailResponse> items;
}
