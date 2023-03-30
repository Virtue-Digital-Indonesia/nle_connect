package com.nle.shared.service.bookingCustomer;

import com.nle.io.entity.BookingCustomer;
import com.nle.io.entity.OtpLog;
import com.nle.io.repository.BookingCustomerRepository;
import com.nle.io.repository.OtpLogRepository;
import com.nle.security.AuthoritiesConstants;
import com.nle.security.jwt.TokenProvider;
import com.nle.shared.dto.verihubs.VerihubsResponseDTO;
import com.nle.shared.service.OTPService;
import com.nle.ui.model.JWTToken;
import com.nle.ui.model.response.booking.BookingCustomerResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class BookingCustomerServiceImpl implements BookingCustomerService{

    private final OTPService otpService;
    private final TokenProvider tokenProvider;
    private final OtpLogRepository otpLogRepository;
    private final BookingCustomerRepository customerRepository;

    @Override
    public VerihubsResponseDTO sendOtpMobile(String phoneNumber) {
        VerihubsResponseDTO response = otpService.sendOTP(phoneNumber);
        return response;
    }

    @Override
    public BookingCustomerResponse verifOTP(String otp, String phone_number) {
        ResponseEntity<String> verify = otpService.verifOTP(otp, phone_number);

        if (verify.getStatusCodeValue() != 200)
            return null;

        String token = tokenProvider.generateManualToken(phone_number, AuthoritiesConstants.BOOKING_CUSTOMER);

        saveOtpLog(otp, phone_number);
        BookingCustomer customer = createCustomer(phone_number);
        return convertToResponse(customer, token);
    }

    private void saveOtpLog(String otp, String phone_number) {
        OtpLog otpLog = new OtpLog();
        otpLog.setOtp(otp);
        otpLog.setPhoneNumber(phone_number);
        otpLogRepository.save(otpLog);
    }

    private BookingCustomer createCustomer (String phone_number) {

        Optional<BookingCustomer> optional = customerRepository.findByPhoneNumber(phone_number);
        if (!optional.isEmpty())
            return optional.get();

        BookingCustomer bookingCustomer = new BookingCustomer();
        bookingCustomer.setPhone_number(phone_number);
        BookingCustomer saved = customerRepository.save(bookingCustomer);
        return saved;
    }

    private BookingCustomerResponse convertToResponse (BookingCustomer entity, String token) {
        BookingCustomerResponse response = new BookingCustomerResponse(token);
        BeanUtils.copyProperties(entity, response);
        return response;
    }
}
