package com.nle.ui.controller.depo;

import com.nle.shared.service.inventory.InventoryService;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.response.GateMoveResponseDTO;
import com.nle.shared.service.gatemove.GateMoveService;
import com.nle.ui.model.response.InventoryResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/inventories")
@RequiredArgsConstructor
public class InventoryController {
    private final Logger log = LoggerFactory.getLogger(InventoryController.class);

    private final GateMoveService gateMoveService;
    private final InventoryService inventoryService;

    @Operation(description = "Get List inventory of depo owner with paging", operationId = "getAllInventory", summary = "Get List inventory of depo owner with paging")
    @SecurityRequirement(name = "nleapi")
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 0"),
            @Parameter (in = ParameterIn.QUERY, name = "size", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 10"),
            @Parameter (in = ParameterIn.QUERY, name = "sort", schema = @Schema(type = "string"), allowEmptyValue = true, description = "default value id, cannot have null data")
    })
    @GetMapping
    public ResponseEntity<PagingResponseModel<InventoryResponse>> getAllInventory(
            @PageableDefault(page = 0, size = 10) @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            })
            @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(inventoryService.getAllInventory(pageable));
    }

    @Operation(description = "Get list of inventory move with paging", operationId = "findAllInventories", summary = "Get list of inventory move with paging")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "/{from}/{to}")
    public ResponseEntity<PagingResponseModel<GateMoveResponseDTO>> findAllInventories(
        @PathVariable(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
        @PathVariable(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
        Pageable pageable) {
        return ResponseEntity.ok(gateMoveService.findAll(pageable, from, to));
    }

}
