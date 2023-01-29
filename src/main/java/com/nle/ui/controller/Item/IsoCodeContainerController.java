package com.nle.ui.controller.Item;

import com.nle.shared.service.item.IsoCodeContainerService;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.IsoCodeContainerRequest;
import com.nle.ui.model.response.IsoCodeContainerResponse;
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
@RequestMapping(value = "/api/item-type/iso")
@RequiredArgsConstructor
public class IsoCodeContainerController {

    private final IsoCodeContainerService isoCodeContainerService;

    @Operation(description = "get list iso code container with paging", operationId = "getAllIsoCode", summary = "get list iso code container with paging")
    @SecurityRequirement(name = "nleapi")
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 0"),
            @Parameter (in = ParameterIn.QUERY, name = "size", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 10"),
            @Parameter (in = ParameterIn.QUERY, name = "sort", schema = @Schema(type = "string"), allowEmptyValue = true, description = "default value id, cannot have null data")
    })
    @GetMapping
    public ResponseEntity<PagingResponseModel<IsoCodeContainerResponse>> getAllIsoCode (
            @PageableDefault(page = 0, size = 10) @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            })
            @Parameter(hidden = true) Pageable pageable
    ) {
        return ResponseEntity.ok(isoCodeContainerService.getAllIsoCode(pageable));
    }

    @Operation(description = "insert iso code container", operationId = "insertIsoCode", summary = "insert iso code container")
    @SecurityRequirement(name = "nleapi")
    @PostMapping(value = "/addIso")
    public ResponseEntity<IsoCodeContainerResponse> insertIsoCode(@RequestBody IsoCodeContainerRequest request) {
        return ResponseEntity.ok(isoCodeContainerService.insertIsoCode(request));
    }

}
