package com.nle.ui.controller.order;

import com.nle.shared.service.order.OrderService;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.response.order.OrderHeaderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @Operation(description = "get order with phone number", operationId = "s")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "/phone")
    public ResponseEntity<PagingResponseModel<OrderHeaderResponse>> searchByPhone (@RequestParam("phone_number") String phoneNumber,
                                                                                   Pageable pageable) {
        return ResponseEntity.ok(orderService.SearchByPhone(phoneNumber, pageable));
    }
}
