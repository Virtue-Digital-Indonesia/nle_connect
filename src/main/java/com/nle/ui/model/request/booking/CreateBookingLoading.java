package com.nle.ui.model.request.booking;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
@ToString
public class CreateBookingLoading extends BookingHeaderRequest{
    private List<DetailLoadingRequest> detailRequests;
}
