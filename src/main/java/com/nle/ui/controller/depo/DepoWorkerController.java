package com.nle.ui.controller.depo;

import com.nle.constant.enums.AccountStatus;
import com.nle.ui.model.ActiveDto;
import com.nle.ui.model.DepoWorkerActivationDTO;
import com.nle.ui.model.DepoWorkerLoginDto;
import com.nle.ui.model.JWTToken;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.DepoWorkerUpdateGateNameReqDto;
import com.nle.ui.model.request.search.DepoWorkerSearchRequest;
import com.nle.ui.model.response.DepoWorkerListDTO;
import com.nle.exception.ApiResponse;
import com.nle.shared.service.depoWorker.DepoWorkerAccountService;
import com.nle.shared.dto.DepoWorkerAccountDTO;
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
@RequestMapping("/api/depo-worker-accounts")
@RequiredArgsConstructor
public class DepoWorkerController {

    private final Logger log = LoggerFactory.getLogger(DepoWorkerController.class);

    private final DepoWorkerAccountService depoWorkerAccountService;

    @Operation(description = "Depo worker join request", operationId = "depoWorkerJoinRequest", summary = "Depo worker join request")
    @PostMapping(value = "/join")
    public ResponseEntity<ApiResponse> depoWorkerJoinRequest(@RequestBody @Valid DepoWorkerActivationDTO depoWorkerActivationDTO) {
        log.info("Process joining request for depo worker: " + depoWorkerActivationDTO.getFullName());
        depoWorkerAccountService.depoWorkerJoinRequest(depoWorkerActivationDTO);
        return ResponseEntity.ok(new ApiResponse(HttpStatus.OK, "Your joining request is sent to depo owner", ""));
    }

    @Operation(description = "Complete depo worker registration process", operationId = "completeDepoWorkerRegistration", summary = "Complete depo worker registration process")
    @PutMapping(value = "/complete")
    public ResponseEntity<DepoWorkerAccountDTO> completeDepoWorkerRegistration(@RequestBody @Valid DepoWorkerUpdateGateNameReqDto depoWorkerUpdateGateNameReqDto) {
        DepoWorkerAccountDTO depoWorkerAccountDTO = depoWorkerAccountService.completeDepoWorkerRegistration(depoWorkerUpdateGateNameReqDto);
        return ResponseEntity.ok(depoWorkerAccountDTO);
    }

    @Operation(description = "Check depo worker join request status", operationId = "checkDepoWorkerRegistrationStatus", summary = "Check depo worker join request status")
    @GetMapping(value = "/status/{androidId}")
    public ResponseEntity<ActiveDto> checkDepoWorkerRegistrationStatus(@PathVariable String androidId) {
        AccountStatus accountStatus = depoWorkerAccountService.checkDepoWorkerRegistrationStatus(androidId);
        return ResponseEntity.ok(new ActiveDto(accountStatus.name(), ""));
    }

    @Operation(description = "Get list of depo worker account with paging", operationId = "getDepoWorkerList", summary = "Get list of depo worker account with paging")
    @GetMapping
    @SecurityRequirement(name = "nleapi")
    @Parameters({
            @Parameter (in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 0"),
            @Parameter (in = ParameterIn.QUERY, name = "size", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 10"),
            @Parameter (in = ParameterIn.QUERY, name = "sort", schema = @Schema(type = "string"), allowEmptyValue = true, description = "default value id, cannot have null data")
    })
    public ResponseEntity<PagingResponseModel<DepoWorkerListDTO>> getDepoWorkerList(
            @PageableDefault(page = 0, size = 10) @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(depoWorkerAccountService.findAll(pageable));
    }

    @Operation(description = "Authenticate depo worker account against android id", operationId = "getDepoWorkerList", summary = "Authenticate depo worker account against android id")
    @PostMapping(value = "/authenticate")
    public ResponseEntity<JWTToken> authenticateDepoWorker(@RequestBody @Valid DepoWorkerLoginDto depoWorkerLoginDto) {
        return ResponseEntity.ok(depoWorkerAccountService.authenticateDepoWorker(depoWorkerLoginDto));
    }

    @Operation(description = "Get depo worker account details", operationId = "getDepoWorkerAccountDetails", summary = "Get depo worker account details")
    @GetMapping(value = "/details")
    @SecurityRequirement(name = "nleapi")
    public ResponseEntity<DepoWorkerAccountDTO> getDepoWorkerAccountDetails() {
        return ResponseEntity.ok(depoWorkerAccountService.getDepoWorkerAccountDetails());
    }

    @Operation(description = "global search depo worker account with paging", operationId = "searchByCondition", summary = "global search depo worker account with paging")
    @PostMapping(value = "/search")
    @SecurityRequirement(name = "nleapi")
    @Parameters({
            @Parameter (in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 0"),
            @Parameter (in = ParameterIn.QUERY, name = "size", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 10"),
            @Parameter (in = ParameterIn.QUERY, name = "sort", schema = @Schema(type = "string"), allowEmptyValue = true, description = "default value id, cannot have null data")
    })
    public ResponseEntity<PagingResponseModel<DepoWorkerListDTO>> searchByCondition(
            @RequestBody DepoWorkerSearchRequest request,
            @PageableDefault(page = 0, size = 10) @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(depoWorkerAccountService.searchByCondition(request, pageable));
    }

}
