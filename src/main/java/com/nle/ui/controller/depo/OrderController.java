package com.nle.ui.controller.depo;

import com.nle.exception.BadRequestException;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.repository.DepoOwnerAccountRepository;
import com.nle.security.SecurityUtils;
import com.nle.shared.service.booking.BookingService;
import com.nle.shared.service.booking.OrderService;
import com.nle.shared.service.form.FormService;
import com.nle.shared.service.xendit.XenditService;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.booking.CreateBookingLoading;
import com.nle.ui.model.request.booking.CreateBookingUnloading;
import com.nle.ui.model.request.search.BookingSearchRequest;
import com.nle.ui.model.request.xendit.XenditCallbackPayload;
import com.nle.ui.model.request.xendit.XenditRequest;
import com.nle.ui.model.response.XenditResponse;
import com.nle.ui.model.response.booking.BookingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/depo/order")
public class OrderController {

    private final OrderService orderService;
    private final BookingService bookingService;
    private final DepoOwnerAccountRepository depoOwnerAccountRepository;
    private final XenditService xenditService;
    private final FormService formService;

    @Operation(description = "get list order booking for depo with paging", operationId = "getOrderDepo", summary = "get list order booking for depo with paging")
    @SecurityRequirement(name = "nleapi")
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 0"),
            @Parameter (in = ParameterIn.QUERY, name = "size", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 10"),
            @Parameter (in = ParameterIn.QUERY, name = "sort", schema = @Schema(type = "string"), allowEmptyValue = true, description = "default value id, cannot have null data")
    })
    @GetMapping
    public ResponseEntity<PagingResponseModel<BookingResponse>> getOrderDepo(
            @PageableDefault(page = 0, size = 10) @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            })
            @Parameter(hidden = true) Pageable pageable
    ){
        return ResponseEntity.ok(orderService.getOrderDepo(pageable));
    }

    @Operation(description = "create booking unloading from depo", operationId = "createOrderUnloading", summary = "create booking unloading from depo")
    @SecurityRequirement(name = "nleapi")
    @PostMapping(value = "/unloading")
    public ResponseEntity<BookingResponse> createOrderUnloading(@RequestBody CreateBookingUnloading request) {
            Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
            if (currentUserLogin.isEmpty())
                throw new BadRequestException("you need to log in");

            Optional<DepoOwnerAccount> entity = depoOwnerAccountRepository.findByCompanyEmail(currentUserLogin.get());
            if (entity.isEmpty())
                throw new BadRequestException("this depo is not registered");

        request.setDepo_id(entity.get().getId());
        return ResponseEntity.ok(bookingService.createBookingUnloading(request));
    }

    @Operation(description = "create booking loading from depo", operationId = "createOrderLoading", summary = "create booking loading from depo")
    @SecurityRequirement(name = "nleapi")
    @PostMapping(value = "/loading")
    public ResponseEntity<BookingResponse> createOrderLoading(@RequestBody CreateBookingLoading request) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isEmpty())
            throw new BadRequestException("you need to log in");

        Optional<DepoOwnerAccount> entity = depoOwnerAccountRepository.findByCompanyEmail(currentUserLogin.get());
        if (entity.isEmpty())
            throw new BadRequestException("this depo is not registered");

        request.setDepo_id(entity.get().getId());
        return ResponseEntity.ok(bookingService.createBookingLoading(request));
    }

    @Operation(description = "search order booking for depo with paging", operationId = "search", summary = "search order booking for depo with paging")
    @SecurityRequirement(name = "nleapi")
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 0"),
            @Parameter (in = ParameterIn.QUERY, name = "size", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 10"),
            @Parameter (in = ParameterIn.QUERY, name = "sort", schema = @Schema(type = "string"), allowEmptyValue = true, description = "default value id, cannot have null data")
    })
    @PostMapping("/search")
    public ResponseEntity<PagingResponseModel<BookingResponse>> search(
            @RequestBody BookingSearchRequest request,
            @PageableDefault(page = 0, size = 10) @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            })
            @Parameter(hidden = true) Pageable pageable
    ){
        return ResponseEntity.ok(orderService.searchOrderDepo(request,pageable));
    }

    @Operation(description = "create virtual account for payment order", operationId = "paymentOrder", summary = "create virtual account for payment order")
    @SecurityRequirement(name = "nleapi")
    @PostMapping(value = "/payment")
    public ResponseEntity<XenditResponse> paymentOrder(@RequestBody XenditRequest request) {
        return ResponseEntity.ok(xenditService.CreatePaymentOrder(request));
    }

    @Operation(description = "Export invoice by booking id", operationId = "exportInvoiceByBookingId", summary = "Export invoice by booking id")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "/invoice/{id}")
    public ResponseEntity<ByteArrayResource> exportInvoiceByBookingId(@PathVariable Long id) {
        ByteArrayOutputStream reportByte = formService.exportInvoice(id);
        String filename = "Invoice_" + String.valueOf(id) + ".pdf";
        ByteArrayResource resource = new ByteArrayResource(reportByte.toByteArray());
        HttpHeaders header = new HttpHeaders();
        header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" +
                filename);
        header.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE);
        header.add("Cache-Control", "no-cache, no-store, must-revalidate");
        header.add("Pragma", "no-cache");
        header.add("Expires", "0");

        return ResponseEntity.status(HttpStatus.OK)
                .headers(header)
                .contentLength(reportByte.size())
                .body(resource);
    }

    @Operation(description = "Cancel order xendit VA", operationId = "cancelOrder", summary = "Cancel order for xendit VA")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "/cancel")
    public ResponseEntity<XenditResponse> cancelOrder(@RequestParam("booking_id") Long booking_id){
        return ResponseEntity.ok(xenditService.cancelOrderXendit(booking_id));
    }

}
