package com.nle.ui.controller.booking;

import com.nle.shared.service.order.OrderService;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.order.CreateOrderHeaderRequest;
import com.nle.ui.model.response.order.OrderHeaderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "api/booking")
@RequiredArgsConstructor
public class BookingController {

    private final OrderService orderService;

    @Operation(description = "get booking by phoneNumber with paging", operationId = "searchByPhone", summary = "get booking by phoneNumber with paging")
    @SecurityRequirement(name = "nleapi")
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 0"),
            @Parameter (in = ParameterIn.QUERY, name = "size", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 10"),
            @Parameter (in = ParameterIn.QUERY, name = "sort", schema = @Schema(type = "string"), allowEmptyValue = true, description = "default value id, cannot have null data")
    })
    @GetMapping(value = "/phone")
    public ResponseEntity<PagingResponseModel<OrderHeaderResponse>> searchByPhone (
            @RequestParam("phone_number") String phoneNumber,
            @PageableDefault(page = 0, size = 10) @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            })
            @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(orderService.SearchByPhone(phoneNumber, pageable));
    }

    @Operation(description = "create Order", operationId = "createOrder", summary = "create order with details")
    @SecurityRequirement(name = "nleapi")
    @PostMapping
    public ResponseEntity<OrderHeaderResponse> createOrder (@RequestBody CreateOrderHeaderRequest request) {
        return ResponseEntity.ok(orderService.CreateOrder(request));
    }
}
