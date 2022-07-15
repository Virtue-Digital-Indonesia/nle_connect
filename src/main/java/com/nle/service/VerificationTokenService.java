package com.nle.service;

import com.nle.constant.VerificationType;
import com.nle.entity.DepoOwnerAccount;
import com.nle.entity.VerificationToken;

public interface VerificationTokenService {
    void delete(Long id);

    VerificationToken createVerificationToken(DepoOwnerAccount depoOwnerAccount, VerificationType type);

    VerificationToken checkVerificationToken(String token);

    VerificationToken findByToken(String token);
}
