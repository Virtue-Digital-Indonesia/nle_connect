package com.nle.service.applicant;

import com.nle.constant.AccountStatus;
import com.nle.constant.ApprovalStatus;
import com.nle.controller.dto.ApplicantListReqDTO;
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
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class ApplicantServiceImpl implements ApplicantService {
    private final DepoOwnerAccountRepository depoOwnerAccountRepository;
    private static final LocalDateTime EPOCH_TIME = LocalDateTime.ofEpochSecond(0, 0, ZoneOffset.UTC);

    @Override
    public PagingResponseModel<ApplicantDTO> findAll(ApplicantListReqDTO applicantListReqDTO, Pageable pageable) {
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
