package com.nle.shared.service.depoOwner;

import com.nle.constant.enums.AccountStatus;
import com.nle.constant.enums.ApprovalStatus;
import com.nle.constant.enums.VerificationType;
import com.nle.constant.enums.TaxMinistryStatusEnum;
import com.nle.security.jwt.TokenProvider;
import com.nle.ui.model.ActiveDto;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.entity.VerificationToken;
import com.nle.exception.CommonException;
import com.nle.exception.ResourceNotFoundException;
import com.nle.mapper.DepoOwnerAccountMapper;
import com.nle.io.repository.DepoOwnerAccountRepository;
import com.nle.io.repository.VerificationTokenRepository;
import com.nle.security.SecurityUtils;
import com.nle.shared.service.VerificationTokenService;
import com.nle.shared.dto.DepoOwnerAccountDTO;
import com.nle.shared.dto.DepoOwnerAccountProfileDTO;
import com.nle.shared.service.email.EmailService;
import com.nle.shared.service.ftp.SSHService;
import com.nle.ui.model.JWTToken;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static com.nle.constant.AppConstant.VerificationStatus.ACTIVE;
import static com.nle.constant.AppConstant.VerificationStatus.ALREADY_ACTIVE;

@RequiredArgsConstructor
@Service
@Transactional
public class DepoOwnerAccountServiceImpl implements DepoOwnerAccountService {
    private final Logger log = LoggerFactory.getLogger(DepoOwnerAccountServiceImpl.class);

    private final DepoOwnerAccountRepository depoOwnerAccountRepository;

    private final DepoOwnerAccountMapper depoOwnerAccountMapper;

    private final PasswordEncoder passwordEncoder;

    private final VerificationTokenService verificationTokenService;

    private final EmailService emailService;

    private final VerificationTokenRepository verificationTokenRepository;

    private final SSHService sshService;
    private final TokenProvider tokenProvider;

    @Override
    public DepoOwnerAccountDTO createDepoOwnerAccount(DepoOwnerAccountDTO depoOwnerAccountDTO) {
        log.debug("Request to save DepoOwnerAccount : {}", depoOwnerAccountDTO);
        // check email exist or not
        Optional<DepoOwnerAccount> companyEmail = this.findByCompanyEmail(depoOwnerAccountDTO.getCompanyEmail());
        if (companyEmail.isPresent()) {
            throw new CommonException("Email is already in use!");
        }
        // check phone number
        Optional<DepoOwnerAccount> phoneNumber = this.findByPhoneNumber(depoOwnerAccountDTO.getPhoneNumber());
        if (phoneNumber.isPresent()) {
            throw new CommonException("Phone number is already in use!");
        }
        // encoded tmp password
        depoOwnerAccountDTO.setPassword(Base64.getEncoder().encodeToString(depoOwnerAccountDTO.getPassword().getBytes()));
        // generate organization code
        String organizationCode = RandomStringUtils.randomAlphabetic(3).toUpperCase() +
            RandomStringUtils.randomNumeric(2).toUpperCase();
        depoOwnerAccountDTO.setOrganizationCode(organizationCode);
        // map to entity
        DepoOwnerAccount depoOwnerAccount = depoOwnerAccountMapper.toEntity(depoOwnerAccountDTO);
        depoOwnerAccount.setAccountStatus(AccountStatus.INACTIVE);
        depoOwnerAccount.setApprovalStatus(ApprovalStatus.REQUEST);
        depoOwnerAccount.setTaxMinistryStatusEnum(TaxMinistryStatusEnum.DISABLE);
        // save to db
        depoOwnerAccount = depoOwnerAccountRepository.save(depoOwnerAccount);
        VerificationToken verificationToken = verificationTokenService.createVerificationToken(depoOwnerAccount, VerificationType.ACTIVE_ACCOUNT);
        // send activation email
        emailService.sendDepoOwnerActiveEmail(depoOwnerAccount, verificationToken.getToken());
        return depoOwnerAccountMapper.toDto(depoOwnerAccount);
    }

    @Override
    public ActiveDto activeDepoOwnerAccount(String token) {
        VerificationToken verificationToken = verificationTokenService.checkVerificationToken(token, true);
        if (ACTIVE.equals(verificationToken.getActiveStatus())) {
            ActiveDto activeDto = new ActiveDto();
            activeDto.setMessage("Your account is already activated!");
            activeDto.setActiveStatus(ALREADY_ACTIVE);
            return activeDto;
        }
        // active user
        DepoOwnerAccount depoOwnerAccount = verificationToken.getDepoOwnerAccount();
        depoOwnerAccount.setAccountStatus(AccountStatus.ACTIVE);
        byte[] decodedBytes = Base64.getDecoder().decode(depoOwnerAccount.getPassword());
        depoOwnerAccount.setFtpPassword(depoOwnerAccount.getPassword());
        String rawPassword = new String(decodedBytes);
        depoOwnerAccount.setPassword(passwordEncoder.encode(rawPassword));
        log.info("Depo owner " + depoOwnerAccount.getFullName() + " has been active.");
        // create FTP account
        try {
            sshService.createFtpUser(depoOwnerAccount.getCompanyEmail(), rawPassword);
        } catch (Exception e) {
            log.error("Error while creating FTP account", e);
        }
        depoOwnerAccount.setFtpFolder("/home/" + depoOwnerAccount.getCompanyEmail() + "/ftp/files");
        depoOwnerAccountRepository.save(depoOwnerAccount);
        log.info("FTP account for depo owner " + depoOwnerAccount.getFullName() + " has been created.");
        // remove verification token
        verificationToken.setActiveStatus(ACTIVE);
        verificationTokenRepository.save(verificationToken);
        ActiveDto activeDto = new ActiveDto();
        activeDto.setMessage("Your account is activated!");
        activeDto.setActiveStatus(ACTIVE);
        return activeDto;
    }

    @Override
    public Optional<DepoOwnerAccount> findByCompanyEmail(String companyEmail) {
        return depoOwnerAccountRepository.findByCompanyEmail(companyEmail);
    }

    @Override
    public Optional<DepoOwnerAccount> findByPhoneNumber(String phoneNumber) {
        return depoOwnerAccountRepository.findByPhoneNumber(phoneNumber);
    }

    @Override
    public Optional<DepoOwnerAccount> findByOrganizationCode(String organizationCode) {
        return depoOwnerAccountRepository.findByOrganizationCode(organizationCode);
    }

    @Override
    public List<DepoOwnerAccount> findAllByStatus(AccountStatus accountStatus) {
        return depoOwnerAccountRepository.findAllByAccountStatus(accountStatus);
    }

    @Override
    public DepoOwnerAccountProfileDTO getProfileDetails() {
        Optional<String> currentUserLogin = SecurityUtils.getCurrentUserLogin();
        if (currentUserLogin.isEmpty()) {
            throw new ResourceNotFoundException("No user login information");
        }
        Optional<DepoOwnerAccount> depoOwnerAccount = depoOwnerAccountRepository.findByCompanyEmail(currentUserLogin.get());
        if (!depoOwnerAccount.isPresent()) {
            throw new ResourceNotFoundException("Depo worker account with email: '" + currentUserLogin.get() + "' doesn't exist");
        }
        DepoOwnerAccountProfileDTO depoOwnerAccountProfileDTO = new DepoOwnerAccountProfileDTO();
        BeanUtils.copyProperties(depoOwnerAccount.get(), depoOwnerAccountProfileDTO);
        return depoOwnerAccountProfileDTO;
    }

    @Override
    public JWTToken resetPasswordToken (String email) {
        Optional<DepoOwnerAccount> optionalDepoOwnerAccount = findByCompanyEmail(email);
        String token = null;
        if (!optionalDepoOwnerAccount.isEmpty()) {
            token = tokenProvider.generateManualToken(optionalDepoOwnerAccount.get(), "DEPO_OWNER");
            emailService.sendResetPassword(optionalDepoOwnerAccount.get(), token);
        }
        return new JWTToken(token);
    };
}
