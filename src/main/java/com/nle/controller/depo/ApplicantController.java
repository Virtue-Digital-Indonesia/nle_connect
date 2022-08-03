package com.nle.controller.depo;

import com.nle.constant.AccountStatus;
import com.nle.constant.ApprovalStatus;
import com.nle.controller.dto.pageable.PagingResponseModel;
import com.nle.controller.dto.response.ApplicantDTO;
import com.nle.service.applicant.ApplicantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApplicantController {

    private final ApplicantService applicantService;

    @Operation(description = "Get list of applicant with paging", operationId = "getApplicantsList", summary = "Get list of applicant with paging")
    @GetMapping(value = "/applicants")
    @SecurityRequirement(name = "nleapi")
    public ResponseEntity<PagingResponseModel<ApplicantDTO>> getApplicantsList(Pageable pageable) {
        return ResponseEntity.ok(applicantService.findAll(pageable));
    }

    @Operation(description = "Filter list of applicant in range of date", operationId = "filterApplicantsList", summary = "Filter list of applicant in range of date")
    @GetMapping(value = "/applicants/{from}/{to}")
    @SecurityRequirement(name = "nleapi")
    public ResponseEntity<PagingResponseModel<ApplicantDTO>> filterApplicantsByDateRange(Pageable pageable,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
        @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        return ResponseEntity.ok(applicantService.filterByCreatedDate(pageable, from, to));
    }

    @Operation(description = "Filter list of applicant by approval status", operationId = "filterApprovalStatus", summary = "Filter list of applicant by approval status")
    @GetMapping(value = "/applicants/{status}")
    @SecurityRequirement(name = "nleapi")
    public ResponseEntity<PagingResponseModel<ApplicantDTO>> filterApplicantsByApprovalStatus(Pageable pageable, @PathVariable String status) {
        return ResponseEntity.ok(applicantService.filterApprovalStatus(pageable, ApprovalStatus.valueOf(status)));
    }

    @Operation(description = "Update Applicant approval status", operationId = "updateApprovalStatus", summary = "Update Applicant approval status")
    @PutMapping(value = "/applicants/update-approval-status/{applicantId}/{status}")
    @SecurityRequirement(name = "nleapi")
    public ResponseEntity<ApplicantDTO> updateApprovalStatus(@PathVariable Long applicantId, @PathVariable String status) {
        return ResponseEntity.ok(applicantService.updateApprovalStatus(applicantId, ApprovalStatus.valueOf(status)));
    }

    @Operation(description = "Update Applicant account status", operationId = "updateAccountStatus", summary = "Update Applicant account status")
    @PutMapping(value = "/applicants/update-account-status/{applicantId}/{status}")
    @SecurityRequirement(name = "nleapi")
    public ResponseEntity<ApplicantDTO> updateAccountStatus(@PathVariable Long applicantId, @PathVariable String status) {
        return ResponseEntity.ok(applicantService.updateAccountStatus(applicantId, AccountStatus.valueOf(status)));
    }
}
