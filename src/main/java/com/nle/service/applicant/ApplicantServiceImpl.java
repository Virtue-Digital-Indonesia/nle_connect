package com.nle.service.applicant;

import com.nle.constant.AccountStatus;
import com.nle.constant.ApprovalStatus;
import com.nle.controller.dto.pageable.PagingResponseModel;
import com.nle.controller.dto.response.ApplicantDTO;
import com.nle.entity.DepoOwnerAccount;
import com.nle.exception.ResourceNotFoundException;
import com.nle.repository.DepoOwnerAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class ApplicantServiceImpl implements ApplicantService {
    private final DepoOwnerAccountRepository depoOwnerAccountRepository;

    @Override
    public PagingResponseModel<ApplicantDTO> findAll(Pageable pageable) {
        Page<DepoOwnerAccount> depoOwnerAccounts = depoOwnerAccountRepository.findAll(pageable);
        return new PagingResponseModel<>(depoOwnerAccounts.map(this::convertFromEntity));
    }

    @Override
    public PagingResponseModel<ApplicantDTO> filterByCreatedDate(Pageable pageable, LocalDateTime from, LocalDateTime to) {
        Page<DepoOwnerAccount> depoOwnerAccounts = depoOwnerAccountRepository.findAllByCreatedDateBetween(from, to, pageable);
        return new PagingResponseModel<>(depoOwnerAccounts.map(this::convertFromEntity));
    }

    @Override
    public PagingResponseModel<ApplicantDTO> filterApprovalStatus(Pageable pageable, ApprovalStatus approvalStatus) {
        Page<DepoOwnerAccount> depoOwnerAccounts = depoOwnerAccountRepository.findAllByApprovalStatus(approvalStatus, pageable);
        return new PagingResponseModel<>(depoOwnerAccounts.map(this::convertFromEntity));
    }

    @Override
    public ApplicantDTO updateApprovalStatus(Long applicantId, ApprovalStatus approvalStatus) {
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
    public ApplicantDTO updateAccountStatus(Long applicantId, AccountStatus accountStatus) {
        Optional<DepoOwnerAccount> depoOwnerAccountOptional = depoOwnerAccountRepository.findById(applicantId);
        if (depoOwnerAccountOptional.isEmpty()) {
            throw new ResourceNotFoundException("Applicant with id: '" + applicantId + "' doesn't exist");
        }
        DepoOwnerAccount depoOwnerAccount = depoOwnerAccountOptional.get();
        depoOwnerAccount.setAccountStatus(accountStatus);
        DepoOwnerAccount updatedDepoOwnerAccount = depoOwnerAccountRepository.save(depoOwnerAccount);
        return convertFromEntity(updatedDepoOwnerAccount);
    }

    public ApplicantDTO convertFromEntity(DepoOwnerAccount depoOwnerAccount) {
        ApplicantDTO applicantDTO = new ApplicantDTO();
        BeanUtils.copyProperties(depoOwnerAccount, applicantDTO);
        return applicantDTO;
    }
}
