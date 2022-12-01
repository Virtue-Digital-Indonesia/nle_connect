package com.nle.ui.model.request.search;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BookingSearchRequest {

    private String bill_landing;
    private String full_name;
    private String consignee;
    private String booking_type;
    private String booking_status;
    private String tx_date;
    private String phone_number;
    private String from;
    private String to;
    private String email;
    private String npwp;
    private String npwp_address;
    private String payment_method;
    private String globalSearch;
}
