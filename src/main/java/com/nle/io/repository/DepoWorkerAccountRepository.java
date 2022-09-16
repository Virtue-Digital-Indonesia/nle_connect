package com.nle.io.repository;

import com.nle.io.entity.DepoWorkerAccount;
import com.nle.ui.model.request.search.DepoWorkerSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data SQL repository for the DepoWorkerAccount entity.
 */
@Repository
public interface DepoWorkerAccountRepository extends JpaRepository<DepoWorkerAccount, Long> {
    Optional<DepoWorkerAccount> findByAndroidId(String androidId);

    @Query (value = "SELECT dwc FROM DepoWorkerAccount dwc WHERE dwc.organizationCode = :code")
    Page<DepoWorkerAccount> findAllWorker (@Param("code") String code,
                                           Pageable pageable);

    @Query(value = "SELECT dwc FROM DepoWorkerAccount dwc " +
            "WHERE dwc.organizationCode = :code " +
            "AND (:#{#request.androidId} IS NULL OR LOWER(dwc.androidId) LIKE LOWER(CONCAT('%', :#{#request.androidId}, '%'))) ")
    Page<DepoWorkerAccount> searchByCondition(@Param("code") String code,
                                              @Param("request")DepoWorkerSearchRequest request,
                                              Pageable pageable);
}
