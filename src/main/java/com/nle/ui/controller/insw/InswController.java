package com.nle.ui.controller.insw;

import com.nle.shared.service.insw.InswService;
import com.nle.ui.model.request.insw.GetInswRequest;
import com.nle.ui.model.response.insw.InswResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/insw")
@RequiredArgsConstructor
public class InswController {
    private final InswService inswService;

    @Operation(description = "Find Shipping Line with code", operationId = "findByCode", summary = "Find Shipping Line with code")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "/{nobl}")
    public InswResponse getInsw(@PathVariable String nobl, @RequestBody GetInswRequest getInswRequest) {
        return inswService.getBolData(nobl, getInswRequest);
    }

}
