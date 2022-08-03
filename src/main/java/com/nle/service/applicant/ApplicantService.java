package com.nle.service.applicant;

import com.nle.constant.AccountStatus;
import com.nle.constant.ApprovalStatus;
import com.nle.controller.dto.pageable.PagingResponseModel;
import com.nle.controller.dto.response.ApplicantDTO;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

public interface ApplicantService {
    PagingResponseModel<ApplicantDTO> findAll(Pageable pageable);

    PagingResponseModel<ApplicantDTO> filterByCreatedDate(Pageable pageable, LocalDateTime from, LocalDateTime to);

    PagingResponseModel<ApplicantDTO> filterApprovalStatus(Pageable pageable, ApprovalStatus approvalStatus);

    ApplicantDTO updateApprovalStatus(Long applicantId, ApprovalStatus approvalStatus);

    ApplicantDTO updateAccountStatus(Long applicantId, AccountStatus accountStatus);

}
