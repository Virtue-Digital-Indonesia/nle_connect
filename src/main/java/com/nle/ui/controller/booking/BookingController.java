package com.nle.ui.controller.booking;

import com.nle.shared.service.applicant.ApplicantService;
import com.nle.shared.service.booking.BookingService;
import com.nle.shared.service.item.ItemService;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.booking.CreateBookingRequest;
import com.nle.ui.model.request.search.BookingSearchRequest;
import com.nle.ui.model.response.ApplicantResponse;
import com.nle.ui.model.response.ItemResponse;
import com.nle.ui.model.response.booking.BookingResponse;
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

import java.util.List;

@RestController
@RequestMapping(value = "api/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final ApplicantService applicantService;
    private final ItemService itemService;

    @Operation(description = "get booking by id", operationId = "getBookingById", summary = "get booking by Id")
    @SecurityRequirement(name = "nleapi")
    @GetMapping
    public ResponseEntity<BookingResponse> getBookingById(@RequestParam("booking_id") Long booking_id,
                                                          @RequestParam("phone_number") String phone_number){
        return ResponseEntity.ok(bookingService.getBookingById(booking_id, phone_number));
    }

    @Operation(description = "get booking by phoneNumber with paging", operationId = "searchByPhone", summary = "get booking by phoneNumber with paging")
    @SecurityRequirement(name = "nleapi")
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 0"),
            @Parameter (in = ParameterIn.QUERY, name = "size", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 10"),
            @Parameter (in = ParameterIn.QUERY, name = "sort", schema = @Schema(type = "string"), allowEmptyValue = true, description = "default value id, cannot have null data")
    })
    @GetMapping(value = "/phone")
    public ResponseEntity<PagingResponseModel<BookingResponse>> searchByPhone (
            @RequestParam("phone_number") String phoneNumber,
            @PageableDefault(page = 0, size = 10) @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            })
            @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(bookingService.SearchByPhone(phoneNumber, pageable));
    }

    @Operation(description = "create Order", operationId = "createOrder", summary = "create order with details")
    @SecurityRequirement(name = "nleapi")
    @PostMapping
    public ResponseEntity<BookingResponse> createOrder (@RequestBody CreateBookingRequest request) {
        return ResponseEntity.ok(bookingService.CreateOrder(request));
    }

    @Operation(description = "get list of depo", operationId = "listDepo", summary = "get list of depo for booking")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "depo/active")
    public ResponseEntity<List<ApplicantResponse>> listDepo(){
        return ResponseEntity.ok(applicantService.getAllApplicant());
    }

    @Operation(description = "get list item of depo", operationId = "getItemDepo", summary = "get list item of depo")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "depo/item")
    public ResponseEntity<List<ItemResponse>> getItemDepo (@RequestParam("depo_id") Long depo_id) {
        return ResponseEntity.ok(itemService.getItemDepo(depo_id));
    }

    @Operation(description = "get search booking with paging ", operationId = "searchBooking", summary = "get search booking with paging")
    @SecurityRequirement(name = "nleapi")
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 0"),
            @Parameter (in = ParameterIn.QUERY, name = "size", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 10"),
            @Parameter (in = ParameterIn.QUERY, name = "sort", schema = @Schema(type = "string"), allowEmptyValue = true, description = "default value id, cannot have null data")
    })
    @PostMapping(value = "/search")
    public ResponseEntity<PagingResponseModel<BookingResponse>> searchBooking (
            @RequestBody BookingSearchRequest request,
            @PageableDefault(page = 0, size = 10) @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            })
            @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(bookingService.searchBooking(request, pageable));
    }
}
