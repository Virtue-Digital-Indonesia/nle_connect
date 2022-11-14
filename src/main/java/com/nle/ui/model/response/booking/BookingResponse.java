package com.nle.ui.model.response.booking;

import com.nle.constant.enums.BookingStatusEnum;
import com.nle.constant.enums.PaymentMethodEnum;
import com.nle.ui.model.response.ItemResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class BookingResponse {

    private Long id;
    private String tx_date;
    private String full_name;
    private String phone_number;
    private String email;
    private String bill_landing;
    private String consignee;
    private String npwp;
    private String npwp_address;
    private PaymentMethodEnum payment_method;
    private BookingStatusEnum order_status;
    private List<ItemResponse> items;
}
