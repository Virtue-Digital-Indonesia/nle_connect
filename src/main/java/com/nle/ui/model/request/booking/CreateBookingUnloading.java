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
public class CreateBookingUnloading extends BookingHeaderRequest {
    private List<DetailUnloadingRequest> detailRequests;
}
