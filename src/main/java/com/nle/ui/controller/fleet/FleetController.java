package com.nle.ui.controller.fleet;

import com.nle.shared.service.fleet.FleetService;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.FleetRequest;
import com.nle.ui.model.response.FleetResponse;
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
@RequestMapping("/api/fleets")
@RequiredArgsConstructor
public class FleetController {

    private final FleetService fleetService;

    @Operation(description = "get list of fleets with paging", operationId = "getAllFleets", summary = "Get List Fleets with paging")
    @SecurityRequirement(name = "nleapi")
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 0"),
            @Parameter (in = ParameterIn.QUERY, name = "size", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 10"),
            @Parameter (in = ParameterIn.QUERY, name = "sort", schema = @Schema(type = "string"), allowEmptyValue = true, description = "default value id, cannot have null data")
    })
    @GetMapping
    public ResponseEntity<PagingResponseModel<FleetResponse>> getAllFleets(
            @PageableDefault(page = 0, size = 10) @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            })
            @Parameter(hidden = true) Pageable pageable
    ) {
        return ResponseEntity.ok(fleetService.getAllFleets(pageable));
    }

    @Operation(description = "create fleet", operationId = "createFleet", summary = "create fleet")
    @SecurityRequirement(name = "nleapi")
    @PostMapping(value = "/addFleet")
    public ResponseEntity<FleetResponse> createFleet(@RequestBody FleetRequest request) {
        return ResponseEntity.ok(fleetService.createFleet(request));
    }

}
