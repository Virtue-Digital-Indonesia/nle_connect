package com.nle.shared.service.applicant;

import com.nle.constant.enums.AccountStatus;
import com.nle.constant.enums.ApprovalStatus;
import com.nle.io.repository.dto.ShippingLineStatistic;
import com.nle.ui.model.ApplicantListReqDTO;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.search.ApplicantSearchRequest;
import com.nle.ui.model.response.ApplicantResponse;
import com.nle.ui.model.response.count.TotalMoves;

import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ApplicantService {
    PagingResponseModel<ApplicantResponse> findAll(ApplicantListReqDTO applicantListReqDTO, Pageable pageable);

    ApplicantResponse updateApprovalStatus(Long applicantId, ApprovalStatus approvalStatus);

    ApplicantResponse updateAccountStatus(Long applicantId, AccountStatus accountStatus);

    PagingResponseModel<ApplicantResponse> searchByCondition(ApplicantSearchRequest request, Pageable pageable);

    List<ApplicantResponse> getAllApplicant();

    List<TotalMoves> totalMovesPerDay(int duration);

    List<ShippingLineStatistic> countFleetManagerByDate(String from, String to);

    Long countTotalFleetManagerByDate(String from, String to);

}
