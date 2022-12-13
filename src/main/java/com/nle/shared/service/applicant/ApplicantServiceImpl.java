package com.nle.shared.service.applicant;

import com.nle.constant.enums.AccountStatus;
import com.nle.constant.enums.ApprovalStatus;
import com.nle.security.SecurityUtils;
import com.nle.ui.model.ApplicantListReqDTO;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.search.ApplicantSearchRequest;
import com.nle.ui.model.response.ApplicantResponse;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.exception.BadRequestException;
import com.nle.exception.ResourceNotFoundException;
import com.nle.io.repository.DepoOwnerAccountRepository;
import com.nle.io.repository.GateMoveRepository;
import com.nle.io.repository.dto.ShippingLineStatistic;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class ApplicantServiceImpl implements ApplicantService {
    private final DepoOwnerAccountRepository depoOwnerAccountRepository;
    private final GateMoveRepository gateMoveRepository;
    private static final LocalDateTime EPOCH_TIME = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);

    @Override
    public PagingResponseModel<ApplicantResponse> findAll(ApplicantListReqDTO applicantListReqDTO, Pageable pageable) {
        List<ApprovalStatus> approvalStatuses = null;
        if (applicantListReqDTO.getFrom() == null) {
            applicantListReqDTO.setFrom(EPOCH_TIME);
        }
        if (applicantListReqDTO.getTo() == null) {
            applicantListReqDTO.setTo(LocalDateTime.now());
        }

        if (applicantListReqDTO.getApprovalStatus() == null) {
            approvalStatuses = List.of(ApprovalStatus.values());
        } else {
            approvalStatuses = List.of(applicantListReqDTO.getApprovalStatus());
        }

        Page<DepoOwnerAccount> depoOwnerAccounts = depoOwnerAccountRepository.filter(applicantListReqDTO.getFrom(),
                applicantListReqDTO.getTo(),
                approvalStatuses, pageable);
        return new PagingResponseModel<>(depoOwnerAccounts.map(this::convertFromEntity));
    }

    @Override
    public ApplicantResponse updateApprovalStatus(Long applicantId, ApprovalStatus approvalStatus) {
        Optional<DepoOwnerAccount> depoOwnerAccountOptional = depoOwnerAccountRepository.findById(applicantId);
        if (depoOwnerAccountOptional.isEmpty()) {
            throw new ResourceNotFoundException("Applicant with id: '" + applicantId + "' doesn't exist");
        }
        DepoOwnerAccount depoOwnerAccount = depoOwnerAccountOptional.get();
        depoOwnerAccount.setApprovalStatus(approvalStatus);
        DepoOwnerAccount updatedDepoOwnerAccount = depoOwnerAccountRepository.save(depoOwnerAccount);
        return convertFromEntity(updatedDepoOwnerAccount);
    }

    @Override
    public ApplicantResponse updateAccountStatus(Long applicantId, AccountStatus accountStatus) {
        Optional<DepoOwnerAccount> depoOwnerAccountOptional = depoOwnerAccountRepository.findById(applicantId);
        if (depoOwnerAccountOptional.isEmpty()) {
            throw new ResourceNotFoundException("Applicant with id: '" + applicantId + "' doesn't exist");
        }
        DepoOwnerAccount depoOwnerAccount = depoOwnerAccountOptional.get();
        depoOwnerAccount.setAccountStatus(accountStatus);
        DepoOwnerAccount updatedDepoOwnerAccount = depoOwnerAccountRepository.save(depoOwnerAccount);
        return convertFromEntity(updatedDepoOwnerAccount);
    }

    public PagingResponseModel<ApplicantResponse> searchByCondition(ApplicantSearchRequest request, Pageable pageable) {
        Optional<String> currentadmin = SecurityUtils.getCurrentUserLogin();
        if (!currentadmin.isEmpty()) {
            Page<DepoOwnerAccount> list = depoOwnerAccountRepository.searchByCondition(request, pageable);
            return new PagingResponseModel<>(list.map(this::convertFromEntity));
        }

        return new PagingResponseModel<>();
    }

    public List<ApplicantResponse> getAllApplicant() {
        List<DepoOwnerAccount> accountList = depoOwnerAccountRepository.findAllByAccountStatus(AccountStatus.ACTIVE);
        List<ApplicantResponse> responseList = new ArrayList<>();
        for (DepoOwnerAccount entity : accountList) {
            responseList.add(this.convertFromEntity(entity));
        }
        return responseList;
    }

    private ApplicantResponse convertFromEntity(DepoOwnerAccount depoOwnerAccount) {
        ApplicantResponse applicantResponse = new ApplicantResponse();
        BeanUtils.copyProperties(depoOwnerAccount, applicantResponse);
        return applicantResponse;
    }

    @Override
    public List<ShippingLineStatistic> countFleetManager() {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isEmpty())
            throw new BadRequestException("Invalid token");

        return gateMoveRepository.countFleetManager();
    }
}
