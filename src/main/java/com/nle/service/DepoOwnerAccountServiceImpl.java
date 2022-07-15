package com.nle.service;

import com.nle.constant.AccountStatus;
import com.nle.constant.VerificationType;
import com.nle.entity.DepoOwnerAccount;
import com.nle.entity.VerificationToken;
import com.nle.exception.CommonException;
import com.nle.mapper.DepoOwnerAccountMapper;
import com.nle.repository.DepoOwnerAccountRepository;
import com.nle.service.dto.DepoOwnerAccountDTO;
import com.nle.service.mail.NotificationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class DepoOwnerAccountServiceImpl implements DepoOwnerAccountService {
    private final Logger log = LoggerFactory.getLogger(DepoOwnerAccountServiceImpl.class);

    private final DepoOwnerAccountRepository depoOwnerAccountRepository;

    private final DepoOwnerAccountMapper depoOwnerAccountMapper;

    private final PasswordEncoder passwordEncoder;

    private final VerificationTokenService verificationTokenService;

    private final NotificationService notificationService;

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
        // encoded password
        depoOwnerAccountDTO.setPassword(passwordEncoder.encode(depoOwnerAccountDTO.getPassword()));
        // map to entity
        DepoOwnerAccount depoOwnerAccount = depoOwnerAccountMapper.toEntity(depoOwnerAccountDTO);
        depoOwnerAccount.setAccountStatus(AccountStatus.INACTIVE);
        // save to db
        depoOwnerAccount = depoOwnerAccountRepository.save(depoOwnerAccount);
        VerificationToken verificationToken = verificationTokenService.createVerificationToken(depoOwnerAccount, VerificationType.ACTIVE_ACCOUNT);
        // send activation email
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("noreply@transporta.id");
        simpleMailMessage.setTo(depoOwnerAccountDTO.getCompanyEmail());
        simpleMailMessage.setSubject("test subject");
        simpleMailMessage.setText("Your token: " + verificationToken.getToken());
        notificationService.sendMailMessage(simpleMailMessage);
        return depoOwnerAccountMapper.toDto(depoOwnerAccount);
    }

    @Override
    public Optional<DepoOwnerAccount> findByCompanyEmail(String companyEmail) {
        return depoOwnerAccountRepository.findByCompanyEmail(companyEmail);
    }

    @Override
    public Optional<DepoOwnerAccount> findByPhoneNumber(String phoneNumber) {
        return depoOwnerAccountRepository.findByPhoneNumber(phoneNumber);
    }
}
