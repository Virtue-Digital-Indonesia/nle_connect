package com.nle.repository;

import com.nle.entity.DepoWorkerAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data SQL repository for the DepoWorkerAccount entity.
 */
@Repository
public interface DepoWorkerAccountRepository extends JpaRepository<DepoWorkerAccount, Long> {
    Optional<DepoWorkerAccount> findByEmail(String email);
}
