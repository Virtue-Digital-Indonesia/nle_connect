package com.nle.ui.controller.insw;

import com.nle.shared.dto.insw.InswSyncDataDTO;
import com.nle.shared.service.insw.InswService;
import com.nle.shared.service.insw.SyncToInsw;
import com.nle.ui.model.response.insw.InswResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/insw")
@RequiredArgsConstructor
@Slf4j
public class InswController {
    private final InswService inswService;
    private final SyncToInsw syncToInsw;

    @Operation(description = "Find Insw record from nobl")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "/{nobl}")
    public InswResponse getInsw(@PathVariable String nobl, @Param(value = "depoId") Long depoId) {
        return inswService.getBolData(nobl, depoId);
    }

    @Operation(description = "sync data with insw", operationId = "syncInsw", summary = "sync data with insw")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "/sync-data-to-insw")
    public void syncInsw() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        syncToInsw.SyncDataToInsw();
        stopWatch.stop();
        log.info("Finished sync data to INSW Server in {} seconds", stopWatch.getTime(TimeUnit.SECONDS));
    }

}
