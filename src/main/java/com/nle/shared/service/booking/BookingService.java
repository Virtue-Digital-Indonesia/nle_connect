package com.nle.shared.service.booking;

import com.nle.shared.dto.verihubs.VerihubsResponseDTO;
import com.nle.ui.model.JWTToken;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.booking.CreateBookingLoading;
import com.nle.ui.model.request.booking.CreateBookingUnloading;
import com.nle.ui.model.request.search.BookingSearchRequest;
import com.nle.ui.model.response.booking.BookingResponse;
import org.springframework.data.domain.Pageable;

public interface BookingService {

    BookingResponse getBookingById(Long booking_id, String phone_number);
    PagingResponseModel<BookingResponse> SearchByPhone(String phoneNumber, Pageable pageable);
    VerihubsResponseDTO sendOtpMobile (String phoneNumber);
    JWTToken verifOTP(String otp, String phone_number);
    BookingResponse createBookingUnloading(CreateBookingUnloading request);
    BookingResponse createBookingLoading(CreateBookingLoading request);
    PagingResponseModel<BookingResponse> searchBooking(BookingSearchRequest request, Pageable pageable);
}
