package com.nle.service;

import com.nle.constant.VerificationType;
import com.nle.entity.DepoOwnerAccount;
import com.nle.entity.DepoWorkerAccount;
import com.nle.entity.VerificationToken;
import com.nle.exception.BadRequestException;
import com.nle.exception.ResourceNotFoundException;
import com.nle.repository.VerificationTokenRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
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
        // save VerificationToken
        return verificationTokenRepository.save(verificationToken);
    }

    @Override
    public VerificationToken createInvitationToken(DepoWorkerAccount depoWorkerAccount, VerificationType type) {
        final String token = RandomStringUtils.randomAlphanumeric(11).toUpperCase();
        // plus 7 days before token expired
        LocalDateTime expiredDate = LocalDateTime.now().plusDays(7);
        // create new VerificationToken
        final VerificationToken verificationToken = new VerificationToken();
        verificationToken.setToken(token);
        verificationToken.setExpiryDate(expiredDate);
        verificationToken.setTokenType(type);
        verificationToken.setDepoOwnerAccount(null);
        verificationToken.setDepoWorkerAccount(depoWorkerAccount);
        // save VerificationToken
        return verificationTokenRepository.save(verificationToken);
    }

    @Override
    public VerificationToken checkVerificationToken(String token) {
        VerificationToken verificationToken = this.findByToken(token);
        if (null == verificationToken) {
            throw new ResourceNotFoundException("Active token does not exist");
        }
        // check expired token
        long seconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), verificationToken.getExpiryDate());
        if (seconds < 0) {
            throw new BadRequestException("Your token has expired.");
        }
        return verificationToken;
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
