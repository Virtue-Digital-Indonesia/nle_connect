package com.nle.service.applicant;

import com.nle.constant.AccountStatus;
import com.nle.constant.ApprovalStatus;
import com.nle.controller.dto.ApplicantListReqDTO;
import com.nle.controller.dto.pageable.PagingResponseModel;
import com.nle.controller.dto.response.ApplicantDTO;
import org.springframework.data.domain.Pageable;

public interface ApplicantService {
    PagingResponseModel<ApplicantDTO> findAll(ApplicantListReqDTO applicantListReqDTO, Pageable pageable);

    ApplicantDTO updateApprovalStatus(Long applicantId, ApprovalStatus approvalStatus);

    ApplicantDTO updateAccountStatus(Long applicantId, AccountStatus accountStatus);

}
