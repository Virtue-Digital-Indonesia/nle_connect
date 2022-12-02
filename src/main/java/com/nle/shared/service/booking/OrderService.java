package com.nle.shared.service.booking;

import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.search.BookingSearchRequest;
import com.nle.ui.model.response.booking.BookingResponse;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    PagingResponseModel<BookingResponse> getOrderDepo(Pageable pageable);
    PagingResponseModel<BookingResponse> searchOrderDepo(BookingSearchRequest request,Pageable pageable);
}
