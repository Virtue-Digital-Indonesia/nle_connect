package com.nle.shared.service.report;

import com.nle.exception.BadRequestException;
import com.nle.exception.CommonException;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.entity.report.ReportGateMove;
import com.nle.io.repository.DepoOwnerAccountRepository;
import com.nle.io.repository.report.ReportGateMoveRepository;
import com.nle.security.SecurityUtils;
import com.nle.ui.model.request.report.CreateReportGateMoveRequest;
import com.nle.ui.model.response.ItemResponse;
import com.nle.ui.model.response.report.CreateReportGateMoveDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class ReportGateMoveServiceImpl implements ReportGateMoveService{
    private final DepoOwnerAccountRepository depoOwnerAccountRepository;
    private final ReportGateMoveRepository reportGateMoveRepository;
    @Override
    public ResponseEntity<CreateReportGateMoveDTO> createReportGateMove(CreateReportGateMoveRequest createReportRequest) {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isEmpty())
            throw new BadRequestException("Token Is Invalid!");

        Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountRepository
                .findByCompanyEmail(currentUserLogin.get());
        if (depoOwnerAccount.isEmpty())
            throw new CommonException("Cannot find this depo owner ");

        ReportGateMove reportGateMove = new ReportGateMove();
        BeanUtils.copyProperties(createReportRequest, reportGateMove);

        ReportGateMove savedReport = reportGateMoveRepository.save(reportGateMove);
        return null;
    }
}
