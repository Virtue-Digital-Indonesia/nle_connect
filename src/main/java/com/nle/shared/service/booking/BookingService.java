package com.nle.shared.service.booking;

import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.order.CreateOrderHeaderRequest;
import com.nle.ui.model.request.search.BookingSearchRequest;
import com.nle.ui.model.response.order.OrderHeaderResponse;
import org.springframework.data.domain.Pageable;

public interface BookingService {

    PagingResponseModel<OrderHeaderResponse> SearchByPhone(String phoneNumber, Pageable pageable);
    OrderHeaderResponse CreateOrder(CreateOrderHeaderRequest request);
    PagingResponseModel<OrderHeaderResponse> searchBooking(BookingSearchRequest request, Pageable pageable);
}
