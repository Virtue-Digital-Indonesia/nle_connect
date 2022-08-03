package com.nle.repository;

import com.nle.constant.AccountStatus;
import com.nle.constant.ApprovalStatus;
import com.nle.entity.DepoOwnerAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data SQL repository for the DepoOwnerAccount entity.
 */
@Repository
public interface DepoOwnerAccountRepository extends JpaRepository<DepoOwnerAccount, Long> {
    Optional<DepoOwnerAccount> findByCompanyEmail(String companyEmail);

    Optional<DepoOwnerAccount> findByPhoneNumber(String phoneNumber);

    Optional<DepoOwnerAccount> findByOrganizationCode(String organizationCode);

    List<DepoOwnerAccount> findAllByAccountStatus(AccountStatus accountStatus);

    Page<DepoOwnerAccount> findAllByCreatedDateBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

    Page<DepoOwnerAccount> findAllByApprovalStatus(ApprovalStatus approvalStatus, Pageable pageable);
}
