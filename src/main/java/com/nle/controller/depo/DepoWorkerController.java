package com.nle.controller.depo;

import com.nle.controller.dto.DepoWorkerActivationDTO;
import com.nle.exception.ApiResponse;
import com.nle.service.depoWorker.DepoWorkerAccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DepoWorkerController {

    private final Logger log = LoggerFactory.getLogger(DepoWorkerController.class);

    private final DepoWorkerAccountService depoWorkerAccountService;

    @Operation(description = "Depo worker join request", operationId = "depoWorkerJoinRequest", summary = "Depo worker join request")
    @PostMapping(value = "/depo-worker-accounts/join")
    @SecurityRequirement(name = "nleapi")
    public ResponseEntity<ApiResponse> depoWorkerJoinRequest(@RequestBody DepoWorkerActivationDTO depoWorkerActivationDTO) {
        log.info("Process joining request for depo worker: " + depoWorkerActivationDTO.getFullName());
        depoWorkerAccountService.depoWorkerJoinRequest(depoWorkerActivationDTO);
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK, "Your joining request is sent to depo owner", ""));
    }


}
