package com.nle.controller.depo;

import com.nle.service.dto.ShippingLineDTO;
import com.nle.service.shippingline.ShippingLineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/shipping-lines")
@RequiredArgsConstructor
public class ShippingLineController {


    private final Logger log = LoggerFactory.getLogger(ShippingLineController.class);

    private final ShippingLineService shippingLineService;

    @Operation(description = "Find all Shipping Line", operationId = "findAll", summary = "Find all Shipping Line")
    @SecurityRequirement(name = "nleapi")
    @GetMapping
    public ResponseEntity<List<ShippingLineDTO>> findAll() {
        return ResponseEntity.ok(shippingLineService.findAll());
    }

    @Operation(description = "Find Shipping Line with code", operationId = "findByCode", summary = "Find Shipping Line with code")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "/{code}")
    public ResponseEntity<ShippingLineDTO> findByCode(@PathVariable String code) {
        return ResponseEntity.ok(shippingLineService.findByCode(code));
    }
}
