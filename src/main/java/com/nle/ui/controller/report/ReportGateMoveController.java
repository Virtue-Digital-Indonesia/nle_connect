package com.nle.ui.controller.report;

import com.nle.ui.model.response.report.CreateReportGateMoveDTO;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "api/report/gatemove")
public class ReportGateMoveController {
    @PostMapping
    @SecurityRequirement(name = "nleapi")
    public ResponseEntity<CreateReportGateMoveDTO> createReportGateMove(){
        return null;
    }
}
