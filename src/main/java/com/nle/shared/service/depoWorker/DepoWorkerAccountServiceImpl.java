package com.nle.shared.service.depoWorker;

import com.nle.constant.enums.AccountStatus;
import com.nle.constant.AppConstant;
import com.nle.exception.BadRequestException;
import com.nle.io.repository.DepoOwnerAccountRepository;
import com.nle.ui.model.DepoWorkerActivationDTO;
import com.nle.ui.model.DepoWorkerLoginDto;
import com.nle.ui.model.JWTToken;
import com.nle.ui.model.pageable.PagingResponseModel;
import com.nle.ui.model.request.DepoWorkerApproveReqDto;
import com.nle.ui.model.request.DepoWorkerUpdateGateNameReqDto;
import com.nle.ui.model.request.search.DepoWorkerSearchRequest;
import com.nle.ui.model.response.DepoWorkerListDTO;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.entity.DepoWorkerAccount;
import com.nle.io.entity.VerificationToken;
import com.nle.exception.CommonException;
import com.nle.exception.ResourceNotFoundException;
import com.nle.mapper.DepoWorkerAccountMapper;
import com.nle.io.repository.DepoWorkerAccountRepository;
import com.nle.io.repository.VerificationTokenRepository;
import com.nle.security.SecurityUtils;
import com.nle.security.jwt.TokenProvider;
import com.nle.shared.service.VerificationTokenService;
import com.nle.shared.service.depoOwner.DepoOwnerAccountService;
import com.nle.shared.dto.DepoWorkerAccountDTO;
import com.nle.shared.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.nle.constant.enums.VerificationType.ACTIVE_ACCOUNT;

@RequiredArgsConstructor
@Service
@Transactional
public class DepoWorkerAccountServiceImpl implements DepoWorkerAccountService {
    private final Logger log = LoggerFactory.getLogger(DepoWorkerAccountServiceImpl.class);
    private final DepoWorkerAccountRepository depoWorkerAccountRepository;
    private final DepoOwnerAccountRepository depoOwnerAccountRepository;
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
            verificationToken.setActiveStatus(AppConstant.VerificationStatus.ACTIVE);
            verificationTokenRepository.save(verificationToken);
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
        depoOwnerAccountOptional.ifPresent(depoOwnerAccount -> depoWorkerAccountDTO.setOrganizationName(depoOwnerAccount.getOrganizationName()));
        return depoWorkerAccountDTO;
    }

    @Override
    public PagingResponseModel<DepoWorkerListDTO> findAll(Pageable pageable) {

        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();

        if (currentUserLogin.isPresent()) {
            String email = currentUserLogin.get();
            Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountRepository.findByCompanyEmail(email);

            if (!depoOwnerAccount.isEmpty()) {
                DepoOwnerAccount entity = depoOwnerAccount.get();
                Page<DepoWorkerAccount> depoWorkerAccounts = depoWorkerAccountRepository.findAllWorker(entity.getOrganizationCode(), pageable);
                return new PagingResponseModel<>(depoWorkerAccounts.map(this::convertFromEntity));
            }
            else throw new BadRequestException("this email is not register");

        }


        return new PagingResponseModel<>();
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
        List<GrantedAuthority> grantedAuthorities = Collections.singletonList(new SimpleGrantedAuthority("DEPO_WORKER"));
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(workerAccount.getAndroidId(), workerAccount.getAndroidId(), grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication);
        return new JWTToken(jwt);
    }

    @Override
    public DepoWorkerAccountDTO getDepoWorkerAccountDetails() {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isPresent()) {
            Optional<DepoWorkerAccount> optionalDepoWorkerAccount = depoWorkerAccountRepository.findByAndroidId(currentUserLogin.get());
            if (optionalDepoWorkerAccount.isEmpty()) {
                throw new ResourceNotFoundException("Depo worker account with Android id: '" + currentUserLogin.get() + "' doesn't exist");
            }
            DepoWorkerAccountDTO depoWorkerAccountDTO = depoWorkerAccountMapper.toDto(optionalDepoWorkerAccount.get());
            Optional<DepoOwnerAccount> depoOwnerAccountOptional = depoOwnerAccountService.findByOrganizationCode(optionalDepoWorkerAccount.get().getOrganizationCode());
            depoOwnerAccountOptional.ifPresent(depoOwnerAccount -> depoWorkerAccountDTO.setOrganizationName(depoOwnerAccount.getOrganizationName()));
            return depoWorkerAccountDTO;
        }
        return null;
    }

    @Override
    public PagingResponseModel<DepoWorkerListDTO> searchByCondition(DepoWorkerSearchRequest request, Pageable pageable) {
        Optional<String> currentUser = SecurityUtils.getCurrentUserLogin();
        if (!currentUser.isEmpty()) {
            String email = currentUser.get();
            Optional<DepoOwnerAccount> owner = depoOwnerAccountRepository.findByCompanyEmail(email);
            if (!owner.isEmpty()) {
                String organizationCode = owner.get().getOrganizationCode();
                Page<DepoWorkerAccount> list = depoWorkerAccountRepository.searchByCondition(organizationCode, request, pageable);
                return new PagingResponseModel<>(list.map(this::convertFromEntity));
            }
        }

        return new PagingResponseModel<>();
    }

    private DepoWorkerListDTO convertFromEntity(DepoWorkerAccount depoWorkerAccount) {
        DepoWorkerListDTO depoWorkerListDTO = new DepoWorkerListDTO();
        BeanUtils.copyProperties(depoWorkerAccount, depoWorkerListDTO);
        return depoWorkerListDTO;
    }

}
