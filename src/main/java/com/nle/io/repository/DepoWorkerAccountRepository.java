package com.nle.io.repository;

import com.nle.io.entity.DepoWorkerAccount;
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

    @Query (value = "SELECT * FROM depo_worker_account WHERE organization_code = :code" , nativeQuery = true)
    Page<DepoWorkerAccount> findAllWorker (@Param("code") String code,
                                           Pageable pageable);
}
