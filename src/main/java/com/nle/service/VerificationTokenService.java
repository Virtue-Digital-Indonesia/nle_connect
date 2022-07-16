package com.nle.service;

import com.nle.constant.VerificationType;
import com.nle.entity.DepoOwnerAccount;
import com.nle.entity.DepoWorkerAccount;
import com.nle.entity.VerificationToken;

import java.util.Optional;

public interface VerificationTokenService {
    void delete(Long id);

    VerificationToken createVerificationToken(DepoOwnerAccount depoOwnerAccount, VerificationType type);

    VerificationToken createInvitationToken(String organizationCode, DepoWorkerAccount depoWorkerAccount, VerificationType type);

    VerificationToken checkVerificationToken(String token);

    VerificationToken findByToken(String token);

    Optional<VerificationToken> findByEmailAndType(String email, VerificationType verificationType);
}
