package com.nle.ui.controller.report;

import com.nle.io.entity.GateMove;
import com.nle.io.entity.report.ReportParameter;
import com.nle.shared.service.gatemove.GateMoveService;
import com.nle.shared.service.report.ReportParameterService;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.report.CreateReportGateMoveRequest;
import com.nle.ui.model.request.report.CreateReportPaymentRequest;
import com.nle.ui.model.response.GateMoveResponseDTO;
import com.nle.ui.model.response.report.CreateReportGateMoveDTO;
import com.nle.ui.model.response.report.CreateReportPaymentDTO;
import com.nle.ui.model.response.report.ReportParameterResponse;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "api/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportParameterService reportParameterService;
    private final GateMoveService gateMoveService;


    @SecurityRequirement(name = "nleapi")
    @Parameters({
            @Parameter(in = ParameterIn.QUERY, name = "page", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 0"),
            @Parameter (in = ParameterIn.QUERY, name = "size", schema = @Schema(type = "int"), allowEmptyValue = true, description = "default value 10"),
            @Parameter (in = ParameterIn.QUERY, name = "sort", schema = @Schema(type = "string"), allowEmptyValue = true, description = "default value id, cannot have null data")
    })
    @GetMapping()
    public ResponseEntity<PagingResponseModel<ReportParameterResponse>> getAllReport(
            @PageableDefault(page = 0, size = 10) @SortDefault.SortDefaults({
                    @SortDefault(sort = "id", direction = Sort.Direction.DESC)
            })
            @Parameter(hidden = true) Pageable pageable
    ){
        return ResponseEntity.ok(reportParameterService.getAllReport(pageable));
    }

    @PostMapping(value = "/gatemove")
    @SecurityRequirement(name = "nleapi")
    public ResponseEntity<CreateReportGateMoveDTO> createReportGateMove(@RequestBody CreateReportGateMoveRequest createReportGateMoveRequest){
        return ResponseEntity.ok(reportParameterService.createReportGateMove(createReportGateMoveRequest));
    }

    @PostMapping(value = "/payment")
    @SecurityRequirement(name = "nleapi")
    public ResponseEntity<CreateReportPaymentDTO> createReportPayment(@RequestBody CreateReportPaymentRequest createReportPaymentRequest){
        return ResponseEntity.ok(reportParameterService.createReportPayment(createReportPaymentRequest));
    }

    @SecurityRequirement(name = "nleapi")
    @GetMapping(value = "/gatemove")
    public ResponseEntity<List<GateMoveResponseDTO>> getReportGateMove(@Param("reportId") Long reportId){
        return ResponseEntity.ok(gateMoveService.buildReportFromParameter(reportId));
    }
}
