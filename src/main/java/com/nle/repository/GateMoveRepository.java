package com.nle.repository;

import com.nle.entity.GateMove;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the GateMove entity.
 */
@Repository
public interface GateMoveRepository extends JpaRepository<GateMove, Long> {
    Page<GateMove> findAllByDepoOwnerAccount_CompanyEmail(String depoOwnerAccount, Pageable pageable);

    Page<GateMove> findAllByDepoOwnerAccount_CompanyEmailAndGateMoveType(String depoOwnerAccount, String gateMoveType, Pageable pageable);

}
