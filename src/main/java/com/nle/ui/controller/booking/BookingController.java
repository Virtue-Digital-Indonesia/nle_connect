package com.nle.ui.controller.booking;

import com.nle.constant.enums.ItemTypeEnum;
import com.nle.exception.BadRequestException;
import com.nle.security.SecurityUtils;
import com.nle.shared.dto.verihubs.VerihubsResponseDTO;
import com.nle.shared.service.applicant.ApplicantService;
import com.nle.shared.service.booking.BookingService;
import com.nle.shared.service.item.ItemService;
import com.nle.shared.service.xendit.XenditService;
import com.nle.ui.model.JWTToken;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.booking.CreateBookingLoading;
import com.nle.ui.model.request.booking.CreateBookingUnloading;
import com.nle.ui.model.request.search.BookingSearchRequest;
import com.nle.ui.model.request.xendit.XenditCallbackPayload;
import com.nle.ui.model.request.xendit.XenditRequest;
import com.nle.ui.model.response.ApplicantResponse;
import com.nle.ui.model.response.ItemResponse;
import com.nle.ui.model.response.XenditResponse;
import com.nle.ui.model.response.booking.BookingResponse;
import com.xendit.model.Invoice;
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
import java.util.Optional;

@RestController
@RequestMapping(value = "/api/booking")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final ApplicantService applicantService;
    private final ItemService itemService;
    private final XenditService xenditService;

    @Operation(description = "get booking by id", operationId = "getBookingById", summary = "get booking by Id")
    @SecurityRequirement(name = "nleapi")
    @GetMapping
    public ResponseEntity<BookingResponse> getBookingById(@RequestParam("booking_id") Long booking_id) {
        return ResponseEntity.ok(bookingService.getBookingById(booking_id));
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
            @PageableDefault(page = 0, size = 10)
            @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            })
            @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(bookingService.SearchByPhone(pageable));
    }

    @Operation(description = "send OTP via mobile", operationId = "sendOtpMobile", summary = "send OTP via mobile")
    @SecurityRequirement(name = "nleapi")
    @PostMapping(value = "/otp/send")
    public ResponseEntity<VerihubsResponseDTO> sendOtpMobile (@RequestParam String phoneNumber) {
        return ResponseEntity.ok(bookingService.sendOtpMobile(phoneNumber));
    }

    @Operation(description = "verif OTP", operationId = "verifOTP", summary = "verif OTP")
    @SecurityRequirement(name = "nleapi")
    @PostMapping(value = "/otp/verif")
    public ResponseEntity<JWTToken> verifOTP (@RequestParam("otp") String otp,
                                              @RequestParam("phone_number") String phone_number) {
        return ResponseEntity.ok(bookingService.verifOTP(otp, phone_number));
    }

    @Operation(description = "create Unloading Booking", operationId = "createBookingUnloading", summary = "create unloading booking with details")
    @SecurityRequirement(name = "nleapi")
    @PostMapping(value = "unloading")
    public ResponseEntity<BookingResponse> createBookingUnloading (@RequestBody CreateBookingUnloading request) {
        Optional<String> currentPhone = SecurityUtils.getCurrentUserLogin();
        if (currentPhone.isEmpty())
            throw new BadRequestException("you need to verify otp");
        if (!currentPhone.get().equalsIgnoreCase(request.getPhone_number()))
            throw new BadRequestException("this token(" + currentPhone.get() + ") is not used " + request.getPhone_number() + " phone");

        return ResponseEntity.ok(bookingService.createBookingUnloading(request));
    }

    @Operation(description = "Create Loading Booking", operationId = "createBookingLoading", summary = "Create Loading Booking")
    @SecurityRequirement(name = "nleapi")
    @PostMapping(value = "/loading")
    public ResponseEntity<BookingResponse> createBookingLoading(@RequestBody CreateBookingLoading request) {
        Optional<String> currentPhone = SecurityUtils.getCurrentUserLogin();
        if (currentPhone.isEmpty())
            throw new BadRequestException("you need to verify otp");
        if (!currentPhone.get().equalsIgnoreCase(request.getPhone_number()))
            throw new BadRequestException("this token(" + currentPhone.get() + ") is not used " + request.getPhone_number() + "phone");

        return ResponseEntity.ok(bookingService.createBookingLoading(request));
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
    public ResponseEntity<List<ItemResponse>> getItemDepo (@RequestParam("depo_id") Long depo_id,@RequestParam("type") ItemTypeEnum type) {
        return ResponseEntity.ok(itemService.getItemDepo(depo_id,type));
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

    @Operation(description = "create virtual account for payment", operationId = "paymentBooking", summary = "create virtual account for payment")
    @SecurityRequirement(name = "nleapi")
    @PostMapping(value = "/payment")
    public ResponseEntity<XenditResponse> paymentBooking(@RequestBody XenditRequest request) {
        return ResponseEntity.ok(xenditService.CreateVirtualAccount(request));
    }

    @Operation(hidden = true)
    @PutMapping(value = "/payment/callback")
    public ResponseEntity<Invoice> callbackBooking (@RequestBody XenditCallbackPayload payload) {
        Invoice invoice = xenditService.VirtualAccountPayment(payload);
//        return ResponseEntity.ok("Success paid");
        return ResponseEntity.ok(invoice);
    }
}
