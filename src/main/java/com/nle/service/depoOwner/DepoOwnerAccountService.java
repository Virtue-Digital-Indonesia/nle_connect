package com.nle.service.depoOwner;

import com.nle.constant.AccountStatus;
import com.nle.entity.DepoOwnerAccount;
import com.nle.service.dto.DepoOwnerAccountDTO;
import com.nle.service.dto.DepoOwnerAccountProfileDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link DepoOwnerAccountDTO}.
 */
public interface DepoOwnerAccountService {
    DepoOwnerAccountDTO createDepoOwnerAccount(DepoOwnerAccountDTO depoOwnerAccountDTO);

    void activeDepoOwnerAccount(String token);

    Optional<DepoOwnerAccount> findByCompanyEmail(String companyEmail);

    Optional<DepoOwnerAccount> findByPhoneNumber(String phoneNumber);

    Optional<DepoOwnerAccount> findByOrganizationCode(String organizationCode);

    List<DepoOwnerAccount> findAllByStatus(AccountStatus accountStatus);

    DepoOwnerAccountProfileDTO getProfileDetails();
}
