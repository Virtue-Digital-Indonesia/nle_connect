package com.nle.ui.controller.booking;

import com.nle.shared.dto.verihubs.VerihubsResponseDTO;
import com.nle.shared.service.bookingCustomer.BookingCustomerService;
import com.nle.ui.model.JWTToken;
import com.nle.ui.model.request.ChangeAdminPasswordRequest;
import com.nle.ui.model.request.ForgotPasswordRequest;
import com.nle.ui.model.response.booking.BookingCustomerResponse;
import com.nle.util.DecodeUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/customer")
@RequiredArgsConstructor
public class BookingCustomerController {

    private final BookingCustomerService customerService;

    @Operation(description = "send OTP via mobile", operationId = "sendOtpMobile", summary = "send OTP via mobile")
    @SecurityRequirement(name = "nleapi")
    @PostMapping(value = "/otp/send")
    public ResponseEntity<VerihubsResponseDTO> sendOtpMobile (@RequestParam String phoneNumber) {
        return ResponseEntity.ok(customerService.sendOtpMobile(phoneNumber));
    }

    @Operation(description = "verif OTP", operationId = "verifOTP", summary = "verif OTP")
    @SecurityRequirement(name = "nleapi")
    @PostMapping(value = "/otp/verif")
    public ResponseEntity<BookingCustomerResponse> verifOTP (@RequestParam("otp") String otp,
                                                             @RequestParam("phone_number") String phone_number) {
        return ResponseEntity.ok(customerService.verifOTP(otp, phone_number));
    }

    @Operation(description = "register email customer", operationId = "registerEmail", summary = "register emil customer, need token")
    @SecurityRequirement(name = "nleapi")
    @PostMapping(value = "/email")
    public ResponseEntity<BookingCustomerResponse> registerEmail (@RequestParam("email") String email) {
        return ResponseEntity.ok(customerService.registerEmail(email));
    }

    @Operation(description = "forgot password, send token to email", operationId = "forgotPassword", summary = "forgot password, send token to email")
    @SecurityRequirement(name = "nleapi")
    @PostMapping(value = "/forgot-password")
    public ResponseEntity<JWTToken> generateResetToken(@RequestParam String email) {
        return ResponseEntity.ok(customerService.resetPasswordToken(email));
    }
    @Operation(description = "reset password for forgot password", operationId = "resetPassword", summary = "reset password for forgot password")
    @SecurityRequirement(name = "nleapi")
    @PostMapping(value = "/reset-password")
    public ResponseEntity<String> forgotPassword(@RequestBody ForgotPasswordRequest request) {
        Map<String, String> authBody = DecodeUtil.decodeToken(request.getToken());
        return ResponseEntity.ok(customerService.changeForgotPassword(request, authBody));
    }

    @Operation(description = "Change password booking customer", operationId = "changePassword", summary = "Change password booking customer")
    @SecurityRequirement(name = "nleapi")
    @PutMapping(value = "/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangeAdminPasswordRequest request) {
        return ResponseEntity.ok(customerService.changePassword(request));
    }

}
