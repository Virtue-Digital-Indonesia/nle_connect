package com.nle.shared.service;

import com.nle.constant.AppConstant;
import com.nle.constant.enums.VerificationType;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.entity.VerificationToken;
import com.nle.exception.BadRequestException;
import com.nle.exception.ResourceNotFoundException;
import com.nle.io.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService {
    private final Logger log = LoggerFactory.getLogger(VerificationTokenServiceImpl.class);

    private final VerificationTokenRepository verificationTokenRepository;

    @Override
    public void delete(Long id) {
        verificationTokenRepository.deleteById(id);
    }

    @Override
    public VerificationToken createVerificationToken(DepoOwnerAccount depoOwnerAccount, VerificationType type) {
        final String token = UUID.randomUUID().toString();
        // plus 7 days before token expired
        LocalDateTime expiredDate = LocalDateTime.now().plusDays(7);
        // create new VerificationToken
        final VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setExpiryDate(expiredDate);
        verificationToken.setTokenType(type);
        verificationToken.setDepoOwnerAccount(depoOwnerAccount);
        verificationToken.setDepoWorkerAccount(null);
        verificationToken.setActiveStatus(AppConstant.VerificationStatus.INACTIVE);
        // save VerificationToken
        return verificationTokenRepository.save(verificationToken);
    }

    @Override
    public VerificationToken createInvitationToken(String organizationCode, VerificationType type) {
        // plus 7 days before token expired
        LocalDateTime expiredDate = LocalDateTime.now().plusDays(7);
        // create new VerificationToken
        final VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(organizationCode);
        verificationToken.setExpiryDate(expiredDate);
        verificationToken.setTokenType(type);
        verificationToken.setDepoOwnerAccount(null);
        verificationToken.setDepoWorkerAccount(null);
        verificationToken.setActiveStatus(AppConstant.VerificationStatus.INACTIVE);
        // save VerificationToken
        return verificationTokenRepository.save(verificationToken);
    }

    @Override
    public VerificationToken checkVerificationToken(String token, boolean required) {
        VerificationToken verificationToken = this.findByToken(token);
        if (null == verificationToken && required) {
            throw new ResourceNotFoundException("Active token does not exist");
        }
        if (verificationToken != null) {
            if (AppConstant.VerificationStatus.ACTIVE.equals(verificationToken.getActiveStatus())) {
                return verificationToken;
            }
            // check expired token
            long seconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), verificationToken.getExpiryDate());
            if (seconds < 0) {
                throw new BadRequestException("Your token has expired.");
            }
            return verificationToken;
        }
        return null;
    }

    @Override
    public VerificationToken findByToken(String token) {
        return verificationTokenRepository.findByToken(token);
    }

    @Override
    public Optional<VerificationToken> findByEmailAndType(String email, VerificationType verificationType) {
        return verificationTokenRepository.findByDepoOwnerAccount_CompanyEmailAndTokenType(email, verificationType);
    }
}
