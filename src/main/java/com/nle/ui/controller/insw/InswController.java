package com.nle.ui.controller.insw;

import com.nle.shared.service.insw.InswService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/insw")
@RequiredArgsConstructor
public class InswController {
    private final InswService inswService;

    @Operation(description = "Find Shipping Line with code", operationId = "findByCode", summary = "Find Shipping Line with code")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "/{nobl}")
    public ResponseEntity<String> getInsw(@PathVariable String nobl) {
        return inswService.getBolData(nobl);
    }
}
