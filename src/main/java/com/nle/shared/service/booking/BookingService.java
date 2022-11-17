package com.nle.shared.service.booking;

import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.booking.CreateBookingRequest;
import com.nle.ui.model.request.search.BookingSearchRequest;
import com.nle.ui.model.response.booking.BookingResponse;
import org.springframework.data.domain.Pageable;

public interface BookingService {

    BookingResponse getBookingById(Long booking_id, String phone_number);
    PagingResponseModel<BookingResponse> SearchByPhone(String phoneNumber, Pageable pageable);
    BookingResponse CreateOrder(CreateBookingRequest request);
    PagingResponseModel<BookingResponse> searchBooking(BookingSearchRequest request, Pageable pageable);
}
