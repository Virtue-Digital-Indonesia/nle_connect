package com.nle.shared.service.depoOwner;

import com.nle.constant.enums.AccountStatus;
import com.nle.ui.model.ActiveDto;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.shared.dto.DepoOwnerAccountDTO;
import com.nle.shared.dto.DepoOwnerAccountProfileDTO;
import com.nle.ui.model.JWTToken;
import com.nle.ui.model.request.ForgotPasswordRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service Interface for managing {@link DepoOwnerAccountDTO}.
 */
public interface DepoOwnerAccountService {
    DepoOwnerAccountDTO createDepoOwnerAccount(DepoOwnerAccountDTO depoOwnerAccountDTO);

    ActiveDto activeDepoOwnerAccount(String token);

    Optional<DepoOwnerAccount> findByCompanyEmail(String companyEmail);

    Optional<DepoOwnerAccount> findByPhoneNumber(String phoneNumber);

    Optional<DepoOwnerAccount> findByOrganizationCode(String organizationCode);

    List<DepoOwnerAccount> findAllByStatus(AccountStatus accountStatus);

    DepoOwnerAccountProfileDTO getProfileDetails();

    JWTToken resetPasswordToken(String email);

    String changeForgotPassword(ForgotPasswordRequest request, Map<String, String> token);
}
