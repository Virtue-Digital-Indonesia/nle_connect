package com.nle.ui.controller.fleet;

import com.nle.shared.service.fleet.InswShippingService;
import com.nle.ui.model.request.InswShippingRequest;
import com.nle.ui.model.response.InswShippingResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
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
    @GetMapping()
    public ResponseEntity<List<InswShippingResponse>> getInswShipping() {
        return ResponseEntity.ok(inswShippingService.getAllInswShipping());
    }

    @Operation(description = "insert insw shipping line", operationId = "insertInswShipping", summary = "insert insw shipping line")
    @SecurityRequirement(name = "nleapi")
    @PostMapping(value = "/add-Insw-Shipping")
    public ResponseEntity<InswShippingResponse> insertInswShipping(@RequestBody InswShippingRequest request) {
        return ResponseEntity.ok(inswShippingService.insertInswShipping(request));
    }

}
