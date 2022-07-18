package com.nle.service.depoWorker;

import com.nle.constant.AccountStatus;
import com.nle.controller.dto.DepoWorkerActivationDTO;
import com.nle.controller.dto.DepoWorkerApproveReqDto;
import com.nle.controller.dto.DepoWorkerUpdateGateNameReqDto;
import com.nle.controller.dto.pageable.PagingResponseModel;
import com.nle.controller.dto.response.DepoWorkerListDTO;
import com.nle.entity.DepoOwnerAccount;
import com.nle.entity.DepoWorkerAccount;
import com.nle.entity.VerificationToken;
import com.nle.exception.CommonException;
import com.nle.exception.ResourceNotFoundException;
import com.nle.mapper.DepoWorkerAccountMapper;
import com.nle.repository.DepoWorkerAccountRepository;
import com.nle.repository.VerificationTokenRepository;
import com.nle.security.SecurityUtils;
import com.nle.service.VerificationTokenService;
import com.nle.service.depoOwner.DepoOwnerAccountService;
import com.nle.service.dto.DepoWorkerAccountDTO;
import com.nle.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.nle.constant.VerificationType.ACTIVE_ACCOUNT;

@RequiredArgsConstructor
@Service
@Transactional
public class DepoWorkerAccountServiceImpl implements DepoWorkerAccountService {
    private final Logger log = LoggerFactory.getLogger(DepoWorkerAccountServiceImpl.class);
    private final DepoWorkerAccountRepository depoWorkerAccountRepository;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;
    private final DepoWorkerAccountMapper depoWorkerAccountMapper;
    private final VerificationTokenRepository verificationTokenRepository;
    private final DepoOwnerAccountService depoOwnerAccountService;

    @Override
    public Optional<DepoWorkerAccount> findByEmail(String email) {
        return depoWorkerAccountRepository.findByEmail(email);
    }

    @Override
    public DepoWorkerAccountDTO createAndSendInvitationEmail(String email) {
        // create new record in depo worker table
        DepoWorkerAccount depoWorkerAccount = new DepoWorkerAccount();
        depoWorkerAccount.setEmail(email);
        depoWorkerAccount.setAccountStatus(AccountStatus.INACTIVE);
        // create verification token
        String organizationCode = RandomStringUtils.randomAlphanumeric(11).toUpperCase();
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isPresent()) {
            Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountService.findByCompanyEmail(currentUserLogin.get());
            if (depoOwnerAccount.isPresent()) {
                organizationCode = depoOwnerAccount.get().getOrganizationCode();
            }
        }
        VerificationToken invitationToken = verificationTokenService.createInvitationToken(organizationCode, depoWorkerAccount, ACTIVE_ACCOUNT);
        depoWorkerAccount.setInvitationCode(invitationToken.getToken());
        depoWorkerAccount.setOrganizationCode(organizationCode);
        depoWorkerAccount = depoWorkerAccountRepository.save(depoWorkerAccount);
        // send email
        emailService.sendDepoWorkerInvitationEmail(email, invitationToken.getToken());
        return depoWorkerAccountMapper.toDto(depoWorkerAccount);
    }

    @Override
    public void depoWorkerJoinRequest(DepoWorkerActivationDTO depoWorkerActivationDTO) {
        VerificationToken verificationToken = verificationTokenService.checkVerificationToken(depoWorkerActivationDTO.getActivationCode());
        // active user
        DepoWorkerAccount depoWorkerAccount = verificationToken.getDepoWorkerAccount();
        depoWorkerAccount.setAndroidId(depoWorkerActivationDTO.getAndroidId());
        depoWorkerAccount.setFullName(depoWorkerActivationDTO.getFullName());
        depoWorkerAccount.setAccountStatus(AccountStatus.WAITING_FOR_APPROVE);
        depoWorkerAccountRepository.save(depoWorkerAccount);
        log.info("Depo worker " + depoWorkerAccount.getFullName() + " has been active.");
        // remove verification token
        verificationTokenRepository.delete(verificationToken);
    }

    @Override
    public void approveJoinRequest(DepoWorkerApproveReqDto depoWorkerApproveReqDto) {
        Optional<DepoWorkerAccount> depoWorkerAccountOptional = depoWorkerAccountRepository.findByEmail(depoWorkerApproveReqDto.getEmail());
        if (depoWorkerAccountOptional.isEmpty()) {
            throw new CommonException("Worker with email " + depoWorkerApproveReqDto.getEmail() + " does not exist in system");
        }
        DepoWorkerAccount depoWorkerAccount = depoWorkerAccountOptional.get();
        if (AccountStatus.INACTIVE == depoWorkerAccount.getAccountStatus()) {
            throw new CommonException("Depo owner account with email " + depoWorkerApproveReqDto.getEmail() + " did not send join request");
        }
        depoWorkerAccount.setAccountStatus(AccountStatus.ACTIVE);
        depoWorkerAccountRepository.save(depoWorkerAccount);
        // send email
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isPresent()) {
            Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountService.findByCompanyEmail(currentUserLogin.get());
            emailService.sendDepoWorkerApproveEmail(depoWorkerAccount.getFullName(), depoOwnerAccount.get().getFullName(), depoWorkerAccount.getEmail());
        }
    }

    @Override
    public void deleteJoinRequest(String email) {
        Optional<DepoWorkerAccount> depoWorkerAccountOptional = depoWorkerAccountRepository.findByEmail(email);
        if (depoWorkerAccountOptional.isEmpty()) {
            throw new CommonException("Worker with email " + email + " does not exist in system");
        }
        DepoWorkerAccount depoWorkerAccount = depoWorkerAccountOptional.get();
        depoWorkerAccountRepository.delete(depoWorkerAccount);
    }

    @Override
    public DepoWorkerAccountDTO completeDepoWorkerRegistration(DepoWorkerUpdateGateNameReqDto depoWorkerUpdateGateNameReqDto) {
        Optional<DepoWorkerAccount> depoWorkerAccountOptional = depoWorkerAccountRepository.findByEmail(depoWorkerUpdateGateNameReqDto.getEmail());
        if (depoWorkerAccountOptional.isEmpty()) {
            throw new CommonException("Worker with email " + depoWorkerUpdateGateNameReqDto.getEmail() + " does not exist in system");
        }
        DepoWorkerAccount depoWorkerAccount = depoWorkerAccountOptional.get();
        depoWorkerAccount.setGateName(depoWorkerUpdateGateNameReqDto.getGateName());
        depoWorkerAccountRepository.save(depoWorkerAccount);
        return depoWorkerAccountMapper.toDto(depoWorkerAccount);
    }

    @Override
    public PagingResponseModel<DepoWorkerListDTO> findAll(Pageable pageable) {
        Page<DepoWorkerAccount> depoWorkerAccounts = depoWorkerAccountRepository.findAll(pageable);
        return new PagingResponseModel<>(depoWorkerAccounts.map(this::convertFromEntity));
    }

    @Override
    public AccountStatus checkDepoWorkerRegistrationStatus(String androidId) {
        Optional<DepoWorkerAccount> depoWorkerAccount = depoWorkerAccountRepository.findByAndroidId(androidId);
        if (depoWorkerAccount.isEmpty()) {
            throw new ResourceNotFoundException("Depo worker account with Android id : " + androidId + " doesn't exist");
        }
        return depoWorkerAccount.get().getAccountStatus();
    }

    private DepoWorkerListDTO convertFromEntity(DepoWorkerAccount depoWorkerAccount) {
        DepoWorkerListDTO depoWorkerListDTO = new DepoWorkerListDTO();
        BeanUtils.copyProperties(depoWorkerAccount, depoWorkerListDTO);
        return depoWorkerListDTO;
    }

}
