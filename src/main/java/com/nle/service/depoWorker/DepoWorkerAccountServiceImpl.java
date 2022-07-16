package com.nle.service.depoWorker;

import com.nle.constant.AccountStatus;
import com.nle.controller.dto.DepoWorkerActivationDTO;
import com.nle.entity.DepoOwnerAccount;
import com.nle.entity.DepoWorkerAccount;
import com.nle.entity.VerificationToken;
import com.nle.exception.CommonException;
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
        depoWorkerAccount.setFullName(depoWorkerActivationDTO.getFullName());
        depoWorkerAccount.setAccountStatus(AccountStatus.WAITING_FOR_APPROVE);
        depoWorkerAccountRepository.save(depoWorkerAccount);
        log.info("Depo worker " + depoWorkerAccount.getFullName() + " has been active.");
        // remove verification token
        verificationTokenRepository.delete(verificationToken);
    }

    @Override
    public void approveJoinRequest(String email) {
        Optional<DepoWorkerAccount> depoWorkerAccountOptional = depoWorkerAccountRepository.findByEmail(email);
        if (depoWorkerAccountOptional.isEmpty()) {
            throw new CommonException("Worker with email " + email + " does not exist in system");
        }
        DepoWorkerAccount depoWorkerAccount = depoWorkerAccountOptional.get();
        if (AccountStatus.INACTIVE == depoWorkerAccount.getAccountStatus()) {
            throw new CommonException("Depo owner account with email " + email + " did not send join request");
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

}
