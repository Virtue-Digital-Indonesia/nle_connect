package com.nle.ui.controller.insw;

import com.nle.shared.dto.insw.InswSyncDataDTO;
import com.nle.shared.service.insw.InswService;
import com.nle.ui.model.response.insw.InswResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/insw")
@RequiredArgsConstructor
@Slf4j
public class InswController {
    private final InswService inswService;

    @Operation(description = "Find Insw record from nobl")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "/{nobl}")
    public InswResponse getInsw(@PathVariable String nobl, @Param(value = "depoId") Long depoId) {
        return inswService.getBolData(nobl, depoId);
    }

    @Operation(description = "sync data with insw", operationId = "syncInsw", summary = "sync data with insw")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "/sync-data-to-insw")
    public List<InswSyncDataDTO> syncInsw() {
        return inswService.syncInsw();
    }

}
