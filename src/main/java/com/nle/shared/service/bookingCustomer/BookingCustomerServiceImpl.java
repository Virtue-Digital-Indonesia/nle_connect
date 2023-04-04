package com.nle.shared.service.bookingCustomer;

import com.nle.exception.BadRequestException;
import com.nle.exception.CommonException;
import com.nle.io.entity.BookingCustomer;
import com.nle.io.entity.OtpLog;
import com.nle.io.repository.BookingCustomerRepository;
import com.nle.io.repository.OtpLogRepository;
import com.nle.security.AuthoritiesConstants;
import com.nle.security.SecurityUtils;
import com.nle.security.jwt.TokenProvider;
import com.nle.shared.dto.verihubs.VerihubsResponseDTO;
import com.nle.shared.service.OTPService;
import com.nle.ui.model.response.booking.BookingCustomerProfileResponse;
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

    @Override
    public BookingCustomerResponse registerEmail (String email) {
        Optional<String> optional = SecurityUtils.getCurrentUserLogin();
        String phoneNumber = optional.get();

        Optional<BookingCustomer> bookingCustomer = customerRepository.findByPhoneNumber(phoneNumber);
        if (bookingCustomer.isEmpty())
            throw new CommonException("Not found booking customer with phone: " + phoneNumber);

        BookingCustomer customer = bookingCustomer.get();
        customer.setEmail(email);
        BookingCustomer saved = customerRepository.save(customer);
        Optional<String> token = SecurityUtils.getCurrentUserJWT();
        return convertToResponse(saved, token.get());
    }

    @Override
    public BookingCustomerProfileResponse getProfile() {
        Optional<String> userLogin = SecurityUtils.getCurrentUserLogin();
        if (userLogin.isEmpty())
            throw new BadRequestException("Invalid token!");
        String userName = userLogin.get();
        Optional<BookingCustomer> bookingCustomer;

        //Todo : Makesure lagi loginnya pakai no telfon atau dengan email juga
        if (userName.startsWith("+62") || userName.startsWith("62") || userName.startsWith("0")) {
           bookingCustomer = customerRepository.findByPhoneNumber(userName);
        } else {
            bookingCustomer = customerRepository.findByEmail(userName);
        }

        if (bookingCustomer.isEmpty())
            throw new BadRequestException("Profile not found!");

        BookingCustomer getBookingCustomer = bookingCustomer.get();

        return convertToProfileResponse(getBookingCustomer);
    }

    private BookingCustomerProfileResponse convertToProfileResponse(BookingCustomer bookingCustomer){
        BookingCustomerProfileResponse profileResponse = new BookingCustomerProfileResponse();
        BeanUtils.copyProperties(bookingCustomer, profileResponse);
        return profileResponse;
    }

    private BookingCustomerResponse convertToResponse (BookingCustomer entity, String token) {
        BookingCustomerResponse response = new BookingCustomerResponse(token);
        BeanUtils.copyProperties(entity, response);
        return response;
    }
}
