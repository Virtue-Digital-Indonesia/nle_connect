package com.nle.service;

import com.nle.entity.DepoOwnerAccount;
import com.nle.service.dto.DepoOwnerAccountDTO;

import java.util.Optional;

/**
 * Service Interface for managing {@link DepoOwnerAccountDTO}.
 */
public interface DepoOwnerAccountService {
    DepoOwnerAccountDTO save(DepoOwnerAccountDTO depoOwnerAccountDTO);

    Optional<DepoOwnerAccount> findByCompanyEmail(String companyEmail);

    Optional<DepoOwnerAccount> findByPhoneNumber(String phoneNumber);
}
