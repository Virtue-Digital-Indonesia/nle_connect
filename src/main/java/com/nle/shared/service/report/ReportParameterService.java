package com.nle.shared.service.report;

import com.nle.io.entity.report.ReportParameter;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.report.CreateReportGateMoveRequest;
import com.nle.ui.model.request.report.CreateReportPaymentRequest;
import com.nle.ui.model.response.report.CreateReportGateMoveDTO;
import com.nle.ui.model.response.report.CreateReportPaymentDTO;
import com.nle.ui.model.response.report.ReportParameterResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReportParameterService {
    CreateReportGateMoveDTO createReportGateMove(CreateReportGateMoveRequest createReportGateMoveRequest);
    CreateReportPaymentDTO createReportPayment(CreateReportPaymentRequest createReportPaymentRequest);
    PagingResponseModel<ReportParameterResponse> getAllReport(Pageable pageable);


}
