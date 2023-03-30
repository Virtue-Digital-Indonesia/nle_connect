package com.nle.shared.service.bookingCustomer;

import com.nle.shared.dto.verihubs.VerihubsResponseDTO;
import com.nle.ui.model.JWTToken;
import com.nle.ui.model.response.booking.BookingCustomerResponse;

public interface BookingCustomerService {

    VerihubsResponseDTO sendOtpMobile (String phoneNumber);
    BookingCustomerResponse verifOTP(String otp, String phone_number);
}
