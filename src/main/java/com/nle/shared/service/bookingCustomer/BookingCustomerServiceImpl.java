package com.nle.shared.service.bookingCustomer;

import com.nle.exception.BadRequestException;
import com.nle.exception.CommonException;
import com.nle.io.entity.BookingCustomer;
import com.nle.io.entity.DepoOwnerAccount;
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
import com.nle.ui.model.request.ChangeAdminPasswordRequest;
import com.nle.ui.model.request.ForgotPasswordRequest;
import com.nle.ui.model.response.booking.BookingCustomerResponse;
import com.nle.util.DecodeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

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
    public JWTToken resetPasswordToken(String email) {
        Optional<BookingCustomer> bookingCustomer = customerRepository.findByEmail(email);
        String token = null;
        if (!bookingCustomer.isEmpty()) {
            token = tokenProvider.generateManualToken(bookingCustomer.get().getEmail(),
                    AuthoritiesConstants.RESET_PASSWORD);
            emailService.sendResetPasswordCustomer(bookingCustomer.get(), token);
        } else
            throw new BadRequestException("Email is not register!");
        return new JWTToken(token);
    }

    @Override
    public String changeForgotPassword(ForgotPasswordRequest request, Map<String, String> token) {

        if (!token.get("auth").equals("RESET_PASSWORD"))
            throw new BadRequestException("This is not token for reset password!");

        if (request.getPassword() == null)
            throw new BadRequestException("Password cannot be null!");

        if (request.getPassword().isEmpty() || request.getConfirm_password().isEmpty())
            throw new BadRequestException("Password cannot be empty!");

        if (!request.getPassword().equals(request.getConfirm_password()))
            throw new BadRequestException("Invalid confirm password!");

        String email = token.get("sub");
        Optional<BookingCustomer> foundEntity = customerRepository.findByEmail(email);
        if (foundEntity.isEmpty())
            throw new BadRequestException("No customer with this email!");

        BookingCustomer entity = foundEntity.get();
        entity.setPassword(passwordEncoder.encode(request.getPassword()));
        customerRepository.save(entity);
        return "Success to reset password with user email : " + email + "!";
    }
    @Override
    public String changePassword(ChangeAdminPasswordRequest request) {
        Optional<String> getCurrentJwt = SecurityUtils.getCurrentUserJWT();
        if (getCurrentJwt.isEmpty())
            throw new BadRequestException("Token not found");

        Map<String, String> token = DecodeUtil.decodeToken(getCurrentJwt.get());
        if (!token.get("auth").equalsIgnoreCase(AuthoritiesConstants.BOOKING_CUSTOMER))
            throw new BadRequestException("Different authorization");

        BookingCustomer bookingCustomer = customerRepository.findByEmail(
                token.get("sub")).orElseThrow(() -> new BadRequestException("Invalid account"));

        if (!passwordEncoder.matches(request.getOldPassword(), bookingCustomer.getPassword()))
            throw new BadRequestException("Invalid old_password");

        if (!request.getNewPassword().equals(request.getConfirmNewPassword()))
            throw new BadRequestException("Invalid confirm_new_password");

        bookingCustomer.setPassword(request.getNewPassword());
        customerRepository.save(bookingCustomer);

        return "success password changed";

    }

    private BookingCustomerResponse convertToResponse (BookingCustomer entity, String token) {
        BookingCustomerResponse response = new BookingCustomerResponse(token);
        BeanUtils.copyProperties(entity, response);
        return response;
    }
}
