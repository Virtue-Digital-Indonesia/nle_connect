package com.nle.ui.controller.booking;

import com.nle.shared.service.xendit.XenditService;
import com.nle.ui.model.response.XenditListResponse;
import com.nle.ui.model.response.XenditResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final XenditService xenditService;

    @Operation(description = "get Xendit VA by booking Header id", operationId = "getXenditByBooking", summary = "get Xendit VA by booking Header id")
    @SecurityRequirement(name = "nleapi")
    @GetMapping()
    public ResponseEntity<XenditResponse> getXenditByBooking(@RequestParam("booking_id") Long booking_id) {
        return ResponseEntity.ok(xenditService.getXenditByBookingId(booking_id));
    }

    @Operation(description = "get list Xendit VA by phone", operationId = "getListXenditByPhone", summary = "get list Xendit VA by phone")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "/bookinglist")
    public ResponseEntity<List<XenditListResponse>> getListXenditByPhone() {
        return ResponseEntity.ok(xenditService.getMultipleXenditByPhone());
    }
}
