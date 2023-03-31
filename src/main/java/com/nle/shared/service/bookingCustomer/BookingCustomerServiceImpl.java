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
import com.nle.shared.service.email.EmailService;
import com.nle.ui.model.JWTToken;
import com.nle.ui.model.request.ChangePhoneNumberRequest;
import com.nle.ui.model.request.ForgotPhoneNumberRequest;
import com.nle.ui.model.response.booking.BookingCustomerResponse;
import com.nle.util.DecodeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class BookingCustomerServiceImpl implements BookingCustomerService{

    private final OTPService otpService;
    private final TokenProvider tokenProvider;
    private final OtpLogRepository otpLogRepository;
    private final BookingCustomerRepository customerRepository;
    private final EmailService emailService;

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
    public JWTToken resetPhoneNumberToken(String email) {
        Optional<BookingCustomer> bookingCustomer = customerRepository.findByEmail(email);
        String token = null;
        if (!bookingCustomer.isEmpty()) {
            token = tokenProvider.generateManualToken(bookingCustomer.get().getEmail(),
                    AuthoritiesConstants.RESET_PHONE_NUMBER);
            emailService.sendResetPhoneNumber(bookingCustomer.get(), token);
        } else
            throw new BadRequestException("Email is not register!");
        return new JWTToken(token);
    }

    @Override
    public String changeForgotPhoneNumber(ForgotPhoneNumberRequest request, Map<String, String> authBody) {
        if (!authBody.get("auth").equals("RESET_PHONE_NUMBER"))
            throw new BadRequestException("This is not token for reset phone number!");

        if (request.getPhone_number().isEmpty() || request.getConfirm_phone_number().isEmpty())
            throw new BadRequestException("Phone number cannot be empty!");

        if (!request.getPhone_number().equals(request.getConfirm_phone_number()))
            throw new BadRequestException("Invalid confirm email!");

        String email = authBody.get("sub");
        Optional<BookingCustomer> foundEntity = customerRepository.findByEmail(email);
        if (foundEntity.isEmpty())
            throw new BadRequestException("Not found customer with this email!");

        BookingCustomer entity = foundEntity.get();
        entity.setPhone_number(request.getPhone_number());
        customerRepository.save(entity);
        return "Success to reset phone number with user email : " + email + "!";
    }

    @Override
    public String changePhoneNumber(ChangePhoneNumberRequest request) {
        Optional<String> getCurrentJwt = SecurityUtils.getCurrentUserJWT();
        if (getCurrentJwt.isEmpty())
            throw new BadRequestException("Token not found");

        Map<String, String> token = DecodeUtil.decodeToken(getCurrentJwt.get());
        if (!token.get("auth").equalsIgnoreCase(AuthoritiesConstants.BOOKING_CUSTOMER))
            throw new BadRequestException("Different authorization");

        BookingCustomer bookingCustomer = customerRepository.findByEmail(
            token.get("sub")).orElseThrow(() -> new BadRequestException("Invalid account"));

        if (!bookingCustomer.getPhone_number().equalsIgnoreCase(request.getOldPhoneNumber()))
            throw new BadRequestException("Invalid old_phone_number");

        if (!request.getNewPhoneNumber().equals(request.getConfirmPhoneNumber()))
            throw new BadRequestException("Invalid confirm_new_phone_number");

        bookingCustomer.setPhone_number(request.getNewPhoneNumber());
        customerRepository.save(bookingCustomer);

        return "success phone number changed";
    }

    private BookingCustomerResponse convertToResponse (BookingCustomer entity, String token) {
        BookingCustomerResponse response = new BookingCustomerResponse(token);
        BeanUtils.copyProperties(entity, response);
        return response;
    }
}
