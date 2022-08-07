package com.nle.controller.applicant;

import com.nle.constant.AccountStatus;
import com.nle.constant.ApprovalStatus;
import com.nle.controller.dto.ApplicantListReqDTO;
import com.nle.controller.dto.pageable.PagingResponseModel;
import com.nle.controller.dto.response.ApplicantDTO;
import com.nle.service.applicant.ApplicantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<PagingResponseModel<ApplicantDTO>> getApplicantsList(Pageable pageable, @RequestBody ApplicantListReqDTO applicantListReqDTO) {
        return ResponseEntity.ok(applicantService.findAll(applicantListReqDTO, pageable));
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