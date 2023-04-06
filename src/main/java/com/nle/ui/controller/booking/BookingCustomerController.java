package com.nle.ui.controller.booking;

import com.nle.io.entity.BookingCustomer;
import com.nle.shared.dto.verihubs.VerihubsResponseDTO;
import com.nle.shared.service.bookingCustomer.BookingCustomerService;
import com.nle.ui.model.request.BookingCustomerRegisterEmail;
import com.nle.ui.model.response.booking.BookingCustomerResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @Operation(description = "get profile customer", operationId = "getBookingCustomerProfileId", summary = "get profile customer")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "/profile")
    public ResponseEntity<BookingCustomerResponse> getProfile() {
        return ResponseEntity.ok(customerService.getProfile());
    }

}
