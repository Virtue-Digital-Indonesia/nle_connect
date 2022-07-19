package com.nle.service.depoWorker;

import com.nle.constant.AccountStatus;
import com.nle.controller.dto.DepoWorkerActivationDTO;
import com.nle.controller.dto.DepoWorkerApproveReqDto;
import com.nle.controller.dto.DepoWorkerLoginDto;
import com.nle.controller.dto.DepoWorkerUpdateGateNameReqDto;
import com.nle.controller.dto.JWTToken;
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
import com.nle.security.jwt.TokenProvider;
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
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final TokenProvider tokenProvider;

    @Override
    public void sendInvitationEmail(String email) {
        // create verification token
        String organizationCode = RandomStringUtils.randomAlphanumeric(11).toUpperCase();
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isPresent()) {
            Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountService.findByCompanyEmail(currentUserLogin.get());
            if (depoOwnerAccount.isPresent()) {
                organizationCode = depoOwnerAccount.get().getOrganizationCode();
            }
        }
        VerificationToken existToken = verificationTokenService.findByToken(organizationCode);
        String token;
        if (existToken == null) {
            VerificationToken invitationToken = verificationTokenService.createInvitationToken(organizationCode, ACTIVE_ACCOUNT);
            token = invitationToken.getToken();
        } else {
            token = existToken.getToken();
        }
        // send email
        emailService.sendDepoWorkerInvitationEmail(email, token);
    }

    @Override
    public void depoWorkerJoinRequest(DepoWorkerActivationDTO depoWorkerActivationDTO) {
        VerificationToken verificationToken = verificationTokenService.checkVerificationToken(depoWorkerActivationDTO.getOrganizationCode(), false);
        // create new record in depo worker table
        DepoWorkerAccount depoWorkerAccount = new DepoWorkerAccount();
        depoWorkerAccount.setAndroidId(depoWorkerActivationDTO.getAndroidId());
        depoWorkerAccount.setFullName(depoWorkerActivationDTO.getFullName());
        depoWorkerAccount.setOrganizationCode(depoWorkerActivationDTO.getOrganizationCode());
        depoWorkerAccount.setAccountStatus(AccountStatus.INACTIVE);
        depoWorkerAccount = depoWorkerAccountRepository.save(depoWorkerAccount);
        log.info("Depo worker " + depoWorkerAccount.getFullName() + " has been created, waiting for approve from depo owner.");
        // remove verification token
        if (verificationToken != null) {
            verificationTokenRepository.delete(verificationToken);
        }
    }

    @Override
    public void approveJoinRequest(DepoWorkerApproveReqDto depoWorkerApproveReqDto) {
        Optional<DepoWorkerAccount> depoWorkerAccountOptional = depoWorkerAccountRepository.findByAndroidId(depoWorkerApproveReqDto.getAndroidId());
        if (depoWorkerAccountOptional.isEmpty()) {
            throw new CommonException("Worker with android id " + depoWorkerApproveReqDto.getAndroidId() + " does not exist in system");
        }
        DepoWorkerAccount depoWorkerAccount = depoWorkerAccountOptional.get();
        depoWorkerAccount.setAccountStatus(AccountStatus.ACTIVE);
        depoWorkerAccountRepository.save(depoWorkerAccount);
    }

    @Override
    public void deleteJoinRequest(String androidId) {
        Optional<DepoWorkerAccount> depoWorkerAccountOptional = depoWorkerAccountRepository.findByAndroidId(androidId);
        if (depoWorkerAccountOptional.isEmpty()) {
            throw new CommonException("Worker with android id " + androidId + " does not exist in system");
        }
        DepoWorkerAccount depoWorkerAccount = depoWorkerAccountOptional.get();
        depoWorkerAccountRepository.delete(depoWorkerAccount);
    }

    @Override
    public DepoWorkerAccountDTO completeDepoWorkerRegistration(DepoWorkerUpdateGateNameReqDto depoWorkerUpdateGateNameReqDto) {
        Optional<DepoWorkerAccount> depoWorkerAccountOptional = depoWorkerAccountRepository.findByAndroidId(depoWorkerUpdateGateNameReqDto.getAndroidId());
        if (depoWorkerAccountOptional.isEmpty()) {
            throw new CommonException("Worker with android id " + depoWorkerUpdateGateNameReqDto.getAndroidId() + " does not exist in system");
        }
        DepoWorkerAccount depoWorkerAccount = depoWorkerAccountOptional.get();
        depoWorkerAccount.setGateName(depoWorkerUpdateGateNameReqDto.getGateName());
        depoWorkerAccountRepository.save(depoWorkerAccount);

        Optional<DepoOwnerAccount> depoOwnerAccountOptional = depoOwnerAccountService.findByOrganizationCode(depoWorkerAccount.getOrganizationCode());
        DepoWorkerAccountDTO depoWorkerAccountDTO = new DepoWorkerAccountDTO();
        BeanUtils.copyProperties(depoWorkerAccount, depoWorkerAccountDTO);
        depoWorkerAccountDTO.setOrganizationName(depoOwnerAccountOptional.get().getOrganizationName());
        return depoWorkerAccountDTO;
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
            return AccountStatus.MISSING;
        }
        return depoWorkerAccount.get().getAccountStatus();
    }

    @Override
    public JWTToken authenticateDepoWorker(DepoWorkerLoginDto androidId) {
        Optional<DepoWorkerAccount> optionalDepoWorkerAccount = depoWorkerAccountRepository.findByAndroidId(androidId.getAndroidId());
        if (optionalDepoWorkerAccount.isEmpty()) {
            throw new ResourceNotFoundException("Depo worker account with Android id: '" + androidId.getAndroidId() + "' doesn't exist");
        }
        DepoWorkerAccount workerAccount = optionalDepoWorkerAccount.get();
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(workerAccount.getFullName(), workerAccount.getAndroidId());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication);
        return new JWTToken(jwt);
    }

    private DepoWorkerListDTO convertFromEntity(DepoWorkerAccount depoWorkerAccount) {
        DepoWorkerListDTO depoWorkerListDTO = new DepoWorkerListDTO();
        BeanUtils.copyProperties(depoWorkerAccount, depoWorkerListDTO);
        return depoWorkerListDTO;
    }

}
