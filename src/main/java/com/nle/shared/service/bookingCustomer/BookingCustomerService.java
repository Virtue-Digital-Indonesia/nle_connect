package com.nle.shared.service.bookingCustomer;

import com.nle.io.entity.BookingCustomer;
import com.nle.shared.dto.verihubs.VerihubsResponseDTO;
import com.nle.ui.model.request.BookingCustomerRegisterEmail;
import com.nle.ui.model.JWTToken;
import com.nle.ui.model.request.ChangePhoneNumberRequest;
import com.nle.ui.model.request.ForgotPhoneNumberRequest;
import com.nle.ui.model.response.booking.BookingCustomerResponse;

import java.util.Map;

public interface BookingCustomerService {

    VerihubsResponseDTO sendOtpMobile (String phoneNumber);
    BookingCustomerResponse verifOTP(String otp, String phone_number);
    BookingCustomerResponse updateCustomer (BookingCustomerRegisterEmail registerEmail);
    String resetPhoneNumberToken(String email);
    String changeForgotPhoneNumber(ForgotPhoneNumberRequest request, Map<String, String> authBody);
    String changePhoneNumber(ChangePhoneNumberRequest request);
    BookingCustomerResponse getProfile();
}
