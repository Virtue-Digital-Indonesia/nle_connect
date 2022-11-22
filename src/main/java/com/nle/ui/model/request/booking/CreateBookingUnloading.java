package com.nle.ui.model.request.booking;

import com.nle.constant.enums.BookingStatusEnum;
import com.nle.constant.enums.ItemTypeEnum;
import com.nle.constant.enums.PaymentMethodEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
public class CreateBookingUnloading {
    private Long depo_id;
    private String tx_date;
    private String full_name;
    private String phone_number;
    private String email;
    private String bill_landing;
    private String consignee;
    private String npwp;
    private String npwp_address;
    private List<DetailUnloadingRequest> detailRequests;
}
