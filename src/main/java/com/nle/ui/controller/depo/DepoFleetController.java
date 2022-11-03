package com.nle.ui.controller.depo;

import com.nle.shared.service.fleet.DepoFleetService;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.DepoFleetRegisterRequest;
import com.nle.ui.model.request.DepoFleetUpdateRequest;
import com.nle.ui.model.request.search.DepoFleetSearchRequest;
import com.nle.ui.model.response.DepoFleetResponse;
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
@RequestMapping("/api/depo-fleet")
@RequiredArgsConstructor
public class DepoFleetController {

    private final DepoFleetService depoFleetService;

    @Operation(description = "register fleet to depo fleet list", operationId = "registerFleet", summary = "register fleet to depo fleet list")
    @SecurityRequirement(name = "nleapi")
    @PostMapping(value = "/register")
    public ResponseEntity<DepoFleetResponse> registerFleet (@RequestBody DepoFleetRegisterRequest request) {
        return ResponseEntity.ok(depoFleetService.registerFleet(request));
    }

    @Operation(description = "Get List fleet of depo owner with paging", operationId = "getListDepoFleet", summary = "Get List fleet of depo owner with paging")
    @SecurityRequirement(name = "nleapi")
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 0"),
            @Parameter (in = ParameterIn.QUERY, name = "size", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 10"),
            @Parameter (in = ParameterIn.QUERY, name = "sort", schema = @Schema(type = "string"), allowEmptyValue = true, description = "default value id, cannot have null data")
    })
    @GetMapping()
    public ResponseEntity<PagingResponseModel<DepoFleetResponse>> getListDepoFleet(
            @PageableDefault(page = 0, size = 10) @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            })
            @Parameter(hidden = true) Pageable pageable
    ) {
        return ResponseEntity.ok(depoFleetService.getAllFleetsDepo(pageable));
    }

    @Operation(description = "update or edit depo fleet", operationId = "editDepoFleet", summary = "edit depo fleet")
    @SecurityRequirement(name = "nleapi")
    @PutMapping()
    public ResponseEntity<DepoFleetResponse> editDepoFleet(@RequestBody DepoFleetUpdateRequest request) {
        return ResponseEntity.ok(depoFleetService.updateRegisterFleet(request));
    }

    @Operation(description = "delete registered depo fleet", operationId = "deleteDepoFleet", summary = "delete registered depo fleet in depo")
    @SecurityRequirement(name = "nleapi")
    @DeleteMapping()
    public ResponseEntity<DepoFleetResponse> deleteDepoFleet (@RequestParam("fleet_code") String fleet_code) {
        return ResponseEntity.ok(depoFleetService.deleteDepoFleet(fleet_code));
    }

    @Operation(description = "Get List fleet of depo owner by filter and paging", operationId = "search", summary = "Get List fleet of depo owner by filter and paging")
    @SecurityRequirement(name = "nleapi")
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 0"),
            @Parameter (in = ParameterIn.QUERY, name = "size", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 10"),
            @Parameter (in = ParameterIn.QUERY, name = "sort", schema = @Schema(type = "string"), allowEmptyValue = true, description = "default value id, cannot have null data")
    })
    @PostMapping("search")
    public ResponseEntity<PagingResponseModel<DepoFleetResponse>> searchDepoFleet(
            @PageableDefault(page = 0, size = 10) @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            })
            @Parameter(hidden = true) Pageable pageable,
            @RequestBody DepoFleetSearchRequest searchRequest
            ) {
        return ResponseEntity.ok(depoFleetService.searchDepoFleet(searchRequest,pageable));
    }


}
