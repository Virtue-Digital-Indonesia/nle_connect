package com.nle.controller.depo;

import com.nle.constant.AccountStatus;
import com.nle.controller.dto.ActiveDto;
import com.nle.controller.dto.DepoWorkerActivationDTO;
import com.nle.controller.dto.DepoWorkerLoginDto;
import com.nle.controller.dto.DepoWorkerUpdateGateNameReqDto;
import com.nle.controller.dto.JWTToken;
import com.nle.controller.dto.pageable.PagingResponseModel;
import com.nle.controller.dto.response.DepoWorkerListDTO;
import com.nle.exception.ApiResponse;
import com.nle.service.depoWorker.DepoWorkerAccountService;
import com.nle.service.dto.DepoWorkerAccountDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DepoWorkerController {

    private final Logger log = LoggerFactory.getLogger(DepoWorkerController.class);

    private final DepoWorkerAccountService depoWorkerAccountService;

    @Operation(description = "Depo worker join request", operationId = "depoWorkerJoinRequest", summary = "Depo worker join request")
    @PostMapping(value = "/depo-worker-accounts/join")
    public ResponseEntity<ApiResponse> depoWorkerJoinRequest(@RequestBody @Valid DepoWorkerActivationDTO depoWorkerActivationDTO) {
        log.info("Process joining request for depo worker: " + depoWorkerActivationDTO.getFullName());
        depoWorkerAccountService.depoWorkerJoinRequest(depoWorkerActivationDTO);
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK, "Your joining request is sent to depo owner", ""));
    }

    @Operation(description = "Complete depo worker registration process", operationId = "completeDepoWorkerRegistration", summary = "Complete depo worker registration process")
    @PutMapping(value = "/depo-worker-accounts/complete")
    public ResponseEntity<DepoWorkerAccountDTO> completeDepoWorkerRegistration(@RequestBody @Valid DepoWorkerUpdateGateNameReqDto depoWorkerUpdateGateNameReqDto) {
        DepoWorkerAccountDTO depoWorkerAccountDTO = depoWorkerAccountService.completeDepoWorkerRegistration(depoWorkerUpdateGateNameReqDto);
        return ResponseEntity.ok(depoWorkerAccountDTO);
    }

    @Operation(description = "Check depo worker join request status", operationId = "checkDepoWorkerRegistrationStatus", summary = "Check depo worker join request status")
    @GetMapping(value = "/depo-worker-accounts/status/{androidId}")
    public ResponseEntity<ActiveDto> checkDepoWorkerRegistrationStatus(@PathVariable String androidId) {
        AccountStatus accountStatus = depoWorkerAccountService.checkDepoWorkerRegistrationStatus(androidId);
        return ResponseEntity.ok(new ActiveDto(accountStatus.name()));
    }


    @Operation(description = "Get list of depo worker account with paging", operationId = "getDepoWorkerList", summary = "Get list of depo worker account with paging")
    @GetMapping(value = "/depo-worker-accounts")
    @SecurityRequirement(name = "nleapi")
    public ResponseEntity<PagingResponseModel<DepoWorkerListDTO>> getDepoWorkerList(Pageable pageable) {
        return ResponseEntity.ok(depoWorkerAccountService.findAll(pageable));
    }

    @Operation(description = "Authenticate depo worker account against android id", operationId = "getDepoWorkerList", summary = "Authenticate depo worker account against android id")
    @PostMapping(value = "/depo-worker-accounts/authenticate")
    public ResponseEntity<JWTToken> authenticateDepoWorker(@RequestBody @Valid DepoWorkerLoginDto depoWorkerLoginDto) {
        return ResponseEntity.ok(depoWorkerAccountService.authenticateDepoWorker(depoWorkerLoginDto));
    }

    @Operation(description = "Get depo worker account details", operationId = "getDepoWorkerAccountDetails", summary = "Get depo worker account details")
    @GetMapping(value = "/depo-worker-accounts/details")
    @SecurityRequirement(name = "nleapi")
    public ResponseEntity<DepoWorkerAccountDTO> getDepoWorkerAccountDetails() {
        return ResponseEntity.ok(depoWorkerAccountService.getDepoWorkerAccountDetails());
    }

}
