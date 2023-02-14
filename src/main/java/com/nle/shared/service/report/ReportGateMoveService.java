package com.nle.shared.service.report;

import com.nle.ui.model.request.report.CreateReportGateMoveRequest;
import com.nle.ui.model.response.report.CreateReportGateMoveDTO;
import org.springframework.http.ResponseEntity;

public interface ReportGateMoveService {
    ResponseEntity<CreateReportGateMoveDTO> createReportGateMove(CreateReportGateMoveRequest createReportGateMoveRequest);
}
