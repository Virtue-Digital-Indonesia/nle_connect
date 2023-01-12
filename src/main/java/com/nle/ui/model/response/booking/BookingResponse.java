package com.nle.ui.model.response.booking;

import com.nle.constant.enums.BookingStatusEnum;
import com.nle.constant.enums.ItemTypeEnum;
import com.nle.constant.enums.PaymentMethodEnum;
import com.nle.ui.model.response.ApplicantResponse;
import com.nle.ui.model.response.BonResponse;
import com.nle.ui.model.response.BonResponseList;
import com.nle.ui.model.response.ItemResponse;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
public class BookingResponse {

    private Long id;
    private String tx_date;
    private ItemTypeEnum booking_type;
    private String full_name;
    private String phone_number;
    private String email;
    private String bill_landing;
    private String consignee;
    private String npwp;
    private String npwp_address;
    private PaymentMethodEnum payment_method;
    private BookingStatusEnum booking_status;
    private LocalDateTime txDateFormatted;
    private ApplicantResponse depo;
    private List<ItemResponse> items;
    private String invoice_no;
    private List<BonResponse> bon_no;
    private List<BankCodeResponse> bank_code;
}
