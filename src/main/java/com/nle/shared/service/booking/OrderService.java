package com.nle.shared.service.booking;

import com.nle.io.entity.DepoOwnerAccount;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.search.BookingSearchRequest;
import com.nle.ui.model.response.booking.BookingResponse;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OrderService {
    PagingResponseModel<BookingResponse> getOrderDepo(Pageable pageable);
    PagingResponseModel<BookingResponse> searchOrderDepo(BookingSearchRequest request,Pageable pageable);
    DepoOwnerAccount orderValidate(Optional<String> username);
}
