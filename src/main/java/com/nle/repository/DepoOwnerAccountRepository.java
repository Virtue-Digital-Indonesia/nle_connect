package com.nle.repository;

import com.nle.entity.DepoOwnerAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data SQL repository for the DepoOwnerAccount entity.
 */
@Repository
public interface DepoOwnerAccountRepository extends JpaRepository<DepoOwnerAccount, Long>, JpaSpecificationExecutor<DepoOwnerAccount> {
    Optional<DepoOwnerAccount> findByCompanyEmail(String companyEmail);

    Optional<DepoOwnerAccount> findByPhoneNumber(String phoneNumber);
}
