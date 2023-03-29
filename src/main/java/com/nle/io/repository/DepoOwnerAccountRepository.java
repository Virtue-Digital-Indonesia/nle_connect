package com.nle.io.repository;

import com.nle.constant.enums.AccountStatus;
import com.nle.constant.enums.ApprovalStatus;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.ui.model.request.search.ApplicantSearchRequest;
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

    static final String DEPO_OWNER_SEARCH_QUERRY = "SELECT doa FROM DepoOwnerAccount doa " +
            "WHERE (:#{#request.companyEmail} IS NULL OR LOWER(doa.companyEmail) LIKE LOWER(CONCAT('%', :#{#request.companyEmail}, '%'))) " +
            "AND (:#{#request.phoneNumber} IS NULL OR LOWER (doa.phoneNumber) LIKE LOWER(CONCAT('%', :#{#request.phoneNumber}, '%'))) " +
            "AND (:#{#request.fullName} IS NULL OR LOWER(doa.fullName) LIKE LOWER(CONCAT('%', :#{#request.fullName}, '%'))) " +
            "AND (:#{#request.organizationName} IS NULL OR LOWER(doa.organizationName) LIKE LOWER(CONCAT('%', :#{#request.organizationName}, '%'))) " +
            "AND (:#{#request.organizationCode} IS NULL OR LOWER(doa.organizationCode) LIKE LOWER(CONCAT('%', :#{#request.organizationCode}, '%'))) " +
            "AND (:#{#request.globalSearch} IS NULL " +
            "OR LOWER(doa.companyEmail) LIKE LOWER(CONCAT('%', :#{#request.globalSearch}, '%')) " +
            "OR LOWER(doa.phoneNumber) LIKE LOWER(CONCAT('%', :#{#request.globalSearch}, '%')) " +
            "OR LOWER(doa.fullName) LIKE LOWER(CONCAT('%', :#{#request.globalSearch}, '%')) " +
            "OR LOWER(doa.organizationName) LIKE LOWER(CONCAT('%', :#{#request.globalSearch}, '%')) " +
            "OR LOWER(doa.organizationCode) LIKE LOWER(CONCAT('%', :#{#request.globalSearch}, '%')) " +
            ")";

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

    @Query(value = DEPO_OWNER_SEARCH_QUERRY)
    Page<DepoOwnerAccount> searchByCondition(@Param("request") ApplicantSearchRequest request,
                                             Pageable pageable);

    Optional<DepoOwnerAccount> findByXenditVaId(String xendit_id);
}
