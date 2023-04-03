package com.nle.ui.controller.booking;

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

    @Operation(description = "register email customer", operationId = "registerEmail", summary = "register email customer, need token")
    @SecurityRequirement(name = "nleapi")
    @PostMapping(value = "/email")
    public ResponseEntity<BookingCustomerResponse> registerEmail (@RequestBody BookingCustomerRegisterEmail registerEmail) {
        return ResponseEntity.ok(customerService.registerEmail(registerEmail));
    }

}
