package com.nle.controller.depo;

import com.nle.controller.dto.pageable.PagingResponseModel;
import com.nle.service.dto.GateMoveDTO;
import com.nle.service.gatemove.GateMoveService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
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

    @Operation(description = "Get list of inventory move with paging", operationId = "findAllInventories", summary = "Get list of inventory move with paging")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "/{from}/{to}")
    public ResponseEntity<PagingResponseModel<GateMoveDTO>> findAllInventories(
        @PathVariable(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
        @PathVariable(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
        Pageable pageable) {
        return ResponseEntity.ok(gateMoveService.findAll(pageable, from, to));
    }

}
