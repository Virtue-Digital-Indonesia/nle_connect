package com.nle.ui.model.request.search;

import com.nle.constant.enums.BookingStatusEnum;
import com.nle.constant.enums.ItemTypeEnum;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BookingSearchRequest {

    private ItemTypeEnum booking_type;
    private BookingStatusEnum booking_status;
    private String tx_date;
    private String phone_number;
    private String globalSearch;
}
