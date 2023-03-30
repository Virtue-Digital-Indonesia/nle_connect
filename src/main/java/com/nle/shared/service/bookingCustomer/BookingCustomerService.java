package com.nle.shared.service.bookingCustomer;

import com.nle.shared.dto.verihubs.VerihubsResponseDTO;
import com.nle.ui.model.JWTToken;

public interface BookingCustomerService {

    VerihubsResponseDTO sendOtpMobile (String phoneNumber);
    JWTToken verifOTP(String otp, String phone_number);
}
