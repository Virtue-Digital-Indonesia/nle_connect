package com.nle.repository;

import com.nle.entity.DepoOwnerAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data SQL repository for the DepoOwnerAccount entity.
 */
@Repository
public interface DepoOwnerAccountRepository extends JpaRepository<DepoOwnerAccount, Long> {
    Optional<DepoOwnerAccount> findByCompanyEmail(String companyEmail);

    Optional<DepoOwnerAccount> findByPhoneNumber(String phoneNumber);

    Optional<DepoOwnerAccount> findByOrganizationCode(String organizationCode);
}
