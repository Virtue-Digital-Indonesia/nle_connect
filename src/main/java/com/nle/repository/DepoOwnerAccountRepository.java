package com.nle.repository;

import com.nle.domain.DepoOwnerAccount;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data SQL repository for the DepoOwnerAccount entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DepoOwnerAccountRepository extends JpaRepository<DepoOwnerAccount, Long>, JpaSpecificationExecutor<DepoOwnerAccount> {
    Optional<DepoOwnerAccount> findByCompanyEmail(String companyEmail);
    Optional<DepoOwnerAccount> findByPhoneNumber(String phoneNumber);
}
