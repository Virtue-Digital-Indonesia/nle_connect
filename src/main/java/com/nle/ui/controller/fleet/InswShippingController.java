package com.nle.ui.controller.fleet;

import com.nle.shared.service.fleet.InswShippingService;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.InswShippingRequest;
import com.nle.ui.model.response.InswShippingResponse;
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
@RequestMapping(value = "/api/insw-shipping")
@RequiredArgsConstructor
public class InswShippingController {

    private final InswShippingService inswShippingService;

    @Operation(description = "get all insw shipping line", operationId = "getInswShipping", summary = "get all insw shipping line")
    @SecurityRequirement(name = "nleapi")
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 0"),
            @Parameter (in = ParameterIn.QUERY, name = "size", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 10"),
            @Parameter (in = ParameterIn.QUERY, name = "sort", schema = @Schema(type = "string"), allowEmptyValue = true, description = "default value id, cannot have null data")
    })
    @GetMapping()
    public ResponseEntity<PagingResponseModel<InswShippingResponse>> getInswShipping(
            @PageableDefault(page = 0, size = 10) @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            })
            @Parameter(hidden = true) Pageable pageable
    ) {
        return ResponseEntity.ok(inswShippingService.getAllInswShipping(pageable));
    }

    @Operation(description = "insert insw shipping line", operationId = "insertInswShipping", summary = "insert insw shipping line")
    @SecurityRequirement(name = "nleapi")
    @PostMapping(value = "/add-Insw-Shipping")
    public ResponseEntity<InswShippingResponse> insertInswShipping(@RequestBody InswShippingRequest request) {
        return ResponseEntity.ok(inswShippingService.insertInswShipping(request));
    }

}
