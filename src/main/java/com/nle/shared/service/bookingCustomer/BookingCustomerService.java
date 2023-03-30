package com.nle.shared.service.bookingCustomer;

import com.nle.shared.dto.verihubs.VerihubsResponseDTO;
import com.nle.ui.model.JWTToken;
import com.nle.ui.model.request.ChangeAdminPasswordRequest;
import com.nle.ui.model.request.ForgotPasswordRequest;
import com.nle.ui.model.response.booking.BookingCustomerResponse;

import java.util.Map;

public interface BookingCustomerService {

    VerihubsResponseDTO sendOtpMobile (String phoneNumber);
    BookingCustomerResponse verifOTP(String otp, String phone_number);
    BookingCustomerResponse registerEmail (String email);
    JWTToken resetPasswordToken(String email);
    String changeForgotPassword(ForgotPasswordRequest request, Map<String, String> token);
    String changePassword(ChangeAdminPasswordRequest request);
}
