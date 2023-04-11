package com.nle.ui.controller.booking;

import com.nle.io.entity.BookingCustomer;
import com.nle.shared.dto.verihubs.VerihubsResponseDTO;
import com.nle.shared.service.bookingCustomer.BookingCustomerService;
import com.nle.ui.model.request.BookingCustomerRegisterEmail;
import com.nle.ui.model.JWTToken;
import com.nle.ui.model.request.ChangePhoneNumberRequest;
import com.nle.ui.model.request.ForgotPhoneNumberRequest;
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

    @Operation(description = "update profile customer", operationId = "updateProfile", summary = "update profile customer, need token")
    @SecurityRequirement(name = "nleapi")
    @PutMapping(value = "/update")
    public ResponseEntity<BookingCustomerResponse> updateCustomer (@RequestBody BookingCustomerRegisterEmail registerEmail) {
        return ResponseEntity.ok(customerService.updateCustomer(registerEmail));
    }
    
    @Operation(description = "forgot phone number, send token to email", operationId = "forgotPhoneNumber", summary = "forgot phone number, send token to email")
    @SecurityRequirement(name = "nleapi")
    @PostMapping(value = "/reset/forget-phone-number")
    public ResponseEntity<JWTToken> generateResetToken(@RequestParam String email) {
        return ResponseEntity.ok(customerService.resetPhoneNumberToken(email));
    }

    @Operation(description = "reset phone number for forgot phone number", operationId = "resetPhoneNumber", summary = "reset phone number for forgot phone number")
    @SecurityRequirement(name = "nleapi")
    @PostMapping(value = "/reset/reset-phone-number")
    public ResponseEntity<String> forgotPhoneNumber(@RequestBody ForgotPhoneNumberRequest request) {
        Map<String, String> authBody = DecodeUtil.decodeToken(request.getToken());
        return ResponseEntity.ok(customerService.changeForgotPhoneNumber(request, authBody));
    }

    @Operation(description = "Change phone number booking customer", operationId = "changePhoneNumber", summary = "Change phone number booking customer")
    @SecurityRequirement(name = "nleapi")
    @PutMapping(value = "/reset/change-phone-number")
    public ResponseEntity<String> changePhoneNumber(@RequestBody ChangePhoneNumberRequest request) {
        return ResponseEntity.ok(customerService.changePhoneNumber(request));
    }
    
    @Operation(description = "get profile customer", operationId = "getBookingCustomerProfileId", summary = "get profile customer")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "/profile")
    public ResponseEntity<BookingCustomerResponse> getProfile() {
        return ResponseEntity.ok(customerService.getProfile());
    }
}
