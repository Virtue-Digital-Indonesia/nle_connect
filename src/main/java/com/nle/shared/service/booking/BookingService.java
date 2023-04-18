package com.nle.shared.service.booking;

import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.booking.CreateBookingLoading;
import com.nle.ui.model.request.booking.CreateBookingUnloading;
import com.nle.ui.model.request.search.BookingSearchRequest;
import com.nle.ui.model.response.booking.BookingResponse;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface BookingService {

    BookingResponse getBookingById(Long booking_id);
    PagingResponseModel<BookingResponse> SearchByPhone(Pageable pageable);
    BookingResponse createBookingUnloading(CreateBookingUnloading request);
    BookingResponse createBookingLoading(CreateBookingLoading request);
    PagingResponseModel<BookingResponse> searchBooking(BookingSearchRequest request, Pageable pageable);
    void bookingValidate(Optional<String> phone, Long booking_id);
}
