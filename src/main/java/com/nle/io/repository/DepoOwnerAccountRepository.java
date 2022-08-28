package com.nle.io.repository;

import com.nle.constant.enums.AccountStatus;
import com.nle.constant.enums.ApprovalStatus;
import com.nle.io.entity.DepoOwnerAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("select d from DepoOwnerAccount d where d.createdDate between :from and :to " +
        "and d.approvalStatus in (:approvalStatuses)")
    Page<DepoOwnerAccount> filter(@Param("from") LocalDateTime from,
                                  @Param("to") LocalDateTime to,
                                  @Param("approvalStatuses") List<ApprovalStatus> approvalStatuses,
                                  Pageable pageable);

}
