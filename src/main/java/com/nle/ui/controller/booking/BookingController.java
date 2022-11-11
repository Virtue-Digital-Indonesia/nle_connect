package com.nle.ui.controller.booking;

import com.nle.shared.service.order.OrderService;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.order.CreateOrderHeaderRequest;
import com.nle.ui.model.response.order.OrderHeaderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/booking")
@RequiredArgsConstructor
public class BookingController {

    private final OrderService orderService;

    @Operation(description = "get order with phone number", operationId = "s")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "/phone")
    public ResponseEntity<PagingResponseModel<OrderHeaderResponse>> searchByPhone (@RequestParam("phone_number") String phoneNumber,
                                                                                   Pageable pageable) {
        return ResponseEntity.ok(orderService.SearchByPhone(phoneNumber, pageable));
    }

    @Operation(description = "create Order", operationId = "createOrder", summary = "create order with details")
    @SecurityRequirement(name = "nleapi")
    @PostMapping
    public ResponseEntity<OrderHeaderResponse> createOrder (@RequestBody CreateOrderHeaderRequest request) {
        return ResponseEntity.ok(orderService.CreateOrder(request));
    }
}
