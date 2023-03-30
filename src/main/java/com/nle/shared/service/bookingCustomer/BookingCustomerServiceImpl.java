package com.nle.shared.service.bookingCustomer;

import com.nle.io.entity.OtpLog;
import com.nle.io.repository.OtpLogRepository;
import com.nle.security.AuthoritiesConstants;
import com.nle.security.jwt.TokenProvider;
import com.nle.shared.dto.verihubs.VerihubsResponseDTO;
import com.nle.shared.service.OTPService;
import com.nle.ui.model.JWTToken;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class BookingCustomerServiceImpl implements BookingCustomerService{

    private final OTPService otpService;
    private final TokenProvider tokenProvider;
    private final OtpLogRepository otpLogRepository;

    @Override
    public VerihubsResponseDTO sendOtpMobile(String phoneNumber) {
        VerihubsResponseDTO response = otpService.sendOTP(phoneNumber);
        return response;
    }

    @Override
    public JWTToken verifOTP(String otp, String phone_number) {
        ResponseEntity<String> verify = otpService.verifOTP(otp, phone_number);

        if (verify.getStatusCodeValue() != 200)
            return null;

        String token = tokenProvider.generateManualToken(phone_number, AuthoritiesConstants.BOOKING_CUSTOMER);

        saveOtpLog(otp, phone_number);
        return new JWTToken(token);
    }

    private void saveOtpLog(String otp, String phone_number) {
        OtpLog otpLog = new OtpLog();
        otpLog.setOtp(otp);
        otpLog.setPhoneNumber(phone_number);
        otpLogRepository.save(otpLog);
    }
}
