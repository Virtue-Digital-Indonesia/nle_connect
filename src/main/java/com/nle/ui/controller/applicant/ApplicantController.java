package com.nle.ui.controller.applicant;

import com.nle.constant.enums.AccountStatus;
import com.nle.constant.enums.ApprovalStatus;
import com.nle.io.repository.dto.LocationStatistic;
import com.nle.io.repository.dto.ShippingLineStatistic;
import com.nle.ui.model.ApplicantListReqDTO;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.search.ApplicantSearchRequest;
import com.nle.ui.model.response.ApplicantResponse;
import com.nle.ui.model.response.count.TotalMoves;
import com.nle.ui.model.response.count.CountMovesByDepotResponse;
import com.nle.shared.service.applicant.ApplicantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @Operation(description = "Count total location", operationId = "countLocation", summary = "Count total location")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "/applicants/count-location")
    public ResponseEntity<List<LocationStatistic>> countLocation() {
        return ResponseEntity.ok(applicantService.countLocation());
    }

    @Operation(description = "Count all fleet manager", operationId = "countFleetManager", summary = "Count all fleet manager")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "/applicants/count-fleet-manager")
    public ResponseEntity<List<ShippingLineStatistic>> countFleetManager() {
        return ResponseEntity.ok(applicantService.countFleetManager());
    }

    @Operation(description = "Count total moves per day", operationId = "totalMovesPerDay", summary = "Count total moves per day")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "/applicants/count-total-moves")
    public ResponseEntity<List<TotalMoves>> totalMovesPerDay(@RequestParam int duration, @RequestParam String location) {
        return ResponseEntity.ok(applicantService.totalMovesPerDay(duration, location));
    }

    @Operation(description = "Count gate moves by depot", operationId = "countGateMovesByDepotPerDay", summary = "Count gate moves by depot")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "/applicants/count-gate-moves-by-depot")
    public ResponseEntity<List<CountMovesByDepotResponse>> countGateMovesByDepotPerDay(@RequestParam int duration, @RequestParam String location) {
        return ResponseEntity.ok(applicantService.countGateMovesByDepotPerDay(duration, location));
    }

    @Operation(description = "Download excel count gate moves by depot", operationId = "downloadGateMovesByDepotPerDay", summary = "Download excel count gate moves by depot")
    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "/applicants/count-gate-moves-by-depot/download")
    public ResponseEntity<Resource> getFile(@RequestParam int duration, @RequestParam String location) {
        String fileName = "gatemove.xlsx";
        InputStreamResource file = new InputStreamResource(applicantService.downloadCountGateMovesByDepot(duration, location));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(file);
    }
}
