package com.nle.shared.service.report;

import com.nle.constant.enums.ReportType;
import com.nle.exception.BadRequestException;
import com.nle.exception.CommonException;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.entity.InswShipping;
import com.nle.io.entity.report.ReportParameter;
import com.nle.io.repository.DepoOwnerAccountRepository;
import com.nle.io.repository.InswShippingRepository;
import com.nle.io.repository.report.ReportParameterRepository;
import com.nle.security.SecurityUtils;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.report.CreateReportGateMoveRequest;
import com.nle.ui.model.request.report.CreateReportPaymentRequest;
import com.nle.ui.model.response.InswShippingResponse;
import com.nle.ui.model.response.report.CreateReportGateMoveDTO;
import com.nle.ui.model.response.report.CreateReportPaymentDTO;
import com.nle.ui.model.response.report.ReportParameterResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class ReportParameterServiceImpl implements ReportParameterService {
    private final DepoOwnerAccountRepository depoOwnerAccountRepository;
    private final ReportParameterRepository reportParameterRepository;
    private final InswShippingRepository inswShippingRepository;
    @Override
    public CreateReportGateMoveDTO createReportGateMove(CreateReportGateMoveRequest createReportRequest) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isEmpty())
            throw new BadRequestException("Token Is Invalid!");

        Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountRepository
                .findByCompanyEmail(currentUserLogin.get());
        if (depoOwnerAccount.isEmpty())
            throw new CommonException("Cannot find this depo owner ");

        Optional<InswShipping> inswShippingOpt = inswShippingRepository.findByCode(createReportRequest.getFleetCode());
        if (inswShippingOpt.isEmpty())
            throw new BadRequestException("Fleet Code Not Found!");

        ReportParameter reportParameter = new ReportParameter();
        BeanUtils.copyProperties(createReportRequest, reportParameter);
        reportParameter.setDepoOwnerId(depoOwnerAccount.get());
        reportParameter.setFleet(inswShippingOpt.get());
        reportParameter.setReportType(ReportType.GATE_MOVE);

        ReportParameter savedReport = reportParameterRepository.save(reportParameter);
        return convertToCreateReportGateMoveDTO(savedReport);
    }

    @Override
    public CreateReportPaymentDTO createReportPayment(CreateReportPaymentRequest createReportPaymentRequest) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isEmpty())
            throw new BadRequestException("Token Is Invalid!");

        Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountRepository
                .findByCompanyEmail(currentUserLogin.get());
        if (depoOwnerAccount.isEmpty())
            throw new CommonException("Cannot find this depo owner ");

        ReportParameter reportParameter = new ReportParameter();
        BeanUtils.copyProperties(createReportPaymentRequest, reportParameter);
        reportParameter.setDepoOwnerId(depoOwnerAccount.get());
        reportParameter.setReportType(ReportType.PAYMENT);

        ReportParameter savedReport = reportParameterRepository.save(reportParameter);
        return convertToCreateReportPaymentDTO(savedReport);
    }

    @Override
    public PagingResponseModel<ReportParameterResponse> getAllReport(Pageable pageable) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isEmpty())
            throw new BadRequestException("Token Is Invalid!");

        Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountRepository
                .findByCompanyEmail(currentUserLogin.get());
        if (depoOwnerAccount.isEmpty())
            throw new CommonException("Cannot find this depo owner ");

        Page<ReportParameter> reportParameters = reportParameterRepository.findAllByDepoOwnerId(depoOwnerAccount.get(), pageable);
        return new PagingResponseModel<>(reportParameters.map(this::convertToReportPrameterResponse));
    }

    private ReportParameterResponse convertToReportPrameterResponse(ReportParameter reportParameter){
        ReportParameterResponse reportParameterResponse = new ReportParameterResponse();
        BeanUtils.copyProperties(reportParameter, reportParameterResponse);
        reportParameterResponse.setDepoId(reportParameter.getDepoOwnerId().getId());

        return reportParameterResponse;
    }

    private CreateReportPaymentDTO convertToCreateReportPaymentDTO(ReportParameter savedReport) {
        CreateReportPaymentDTO createReportPaymentDTO = new CreateReportPaymentDTO();
        BeanUtils.copyProperties(savedReport, createReportPaymentDTO);

        return createReportPaymentDTO;
    }

    private CreateReportGateMoveDTO convertToCreateReportGateMoveDTO(ReportParameter reportParameter){
        CreateReportGateMoveDTO createReportGateMoveDTO = new CreateReportGateMoveDTO();
        BeanUtils.copyProperties(reportParameter, createReportGateMoveDTO);

        InswShippingResponse inswShippingResponse = new InswShippingResponse();
        BeanUtils.copyProperties(reportParameter.getFleet(), inswShippingResponse);
        createReportGateMoveDTO.setFleet(inswShippingResponse);

        return createReportGateMoveDTO;
    }
}
