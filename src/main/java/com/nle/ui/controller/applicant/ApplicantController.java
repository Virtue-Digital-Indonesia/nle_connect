package com.nle.ui.controller.applicant;

import com.nle.constant.enums.AccountStatus;
import com.nle.constant.enums.ApprovalStatus;
import com.nle.io.repository.dto.ShippingLineStatistic;
import com.nle.ui.model.ApplicantListReqDTO;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.search.ApplicantSearchRequest;
import com.nle.ui.model.response.ApplicantResponse;
import com.nle.shared.service.applicant.ApplicantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApplicantController {

    private final ApplicantService applicantService;

    @Operation(description = "Get list of applicant with paging", operationId = "getApplicantsList", summary = "Get list of applicant with paging")
    @PostMapping(value = "/applicants")
    @SecurityRequirement(name = "nleapi")
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 0"),
            @Parameter(in = ParameterIn.QUERY, name = "size", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 10"),
            @Parameter(in = ParameterIn.QUERY, name = "sort", schema = @Schema(type = "string"), allowEmptyValue = true, description = "default value id, cannot have null data")
    })
    public ResponseEntity<PagingResponseModel<ApplicantResponse>> getApplicantsList(
            @RequestBody ApplicantListReqDTO applicantListReqDTO,
            @PageableDefault(page = 0, size = 10) @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(applicantService.findAll(applicantListReqDTO, pageable));
    }

    @Operation(description = "Update Applicant approval status", operationId = "updateApprovalStatus", summary = "Update Applicant approval status")
    @PutMapping(value = "/applicants/update-approval-status/{applicantId}/{status}")
    @SecurityRequirement(name = "nleapi")
    public ResponseEntity<ApplicantResponse> updateApprovalStatus(@PathVariable Long applicantId,
            @PathVariable String status) {
        return ResponseEntity.ok(applicantService.updateApprovalStatus(applicantId, ApprovalStatus.valueOf(status)));
    }

    @Operation(description = "Update Applicant account status", operationId = "updateAccountStatus", summary = "Update Applicant account status")
    @PutMapping(value = "/applicants/update-account-status/{applicantId}/{status}")
    @SecurityRequirement(name = "nleapi")
    public ResponseEntity<ApplicantResponse> updateAccountStatus(@PathVariable Long applicantId,
            @PathVariable String status) {
        return ResponseEntity.ok(applicantService.updateAccountStatus(applicantId, AccountStatus.valueOf(status)));
    }

    @Operation(description = "global search for applicant", operationId = "searchByCondition", summary = "global search for applicant")
    @SecurityRequirement(name = "nleapi")
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 0"),
            @Parameter(in = ParameterIn.QUERY, name = "size", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 10"),
            @Parameter(in = ParameterIn.QUERY, name = "sort", schema = @Schema(type = "string"), allowEmptyValue = true, description = "default value id, cannot have null data")
    })
    @PostMapping("/applicants/search")
    public ResponseEntity<PagingResponseModel<ApplicantResponse>> searchByCondition(
            @RequestBody ApplicantSearchRequest request,
            @PageableDefault(page = 0, size = 10) @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            }) @Parameter(hidden = true) Pageable pageable) {
        return ResponseEntity.ok(applicantService.searchByCondition(request, pageable));
    }

    @Operation(description = "Count all fleet manager", operationId = "countFleetManager", summary = "Count all fleet manager")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "/applicants/count-fleet-manager")
    public ResponseEntity<List<ShippingLineStatistic>> countFleetManager() {
        return ResponseEntity.ok(applicantService.countFleetManager());
    }
}
