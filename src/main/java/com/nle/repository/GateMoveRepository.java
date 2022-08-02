package com.nle.repository;

import com.nle.entity.GateMove;
import com.nle.repository.dto.MoveStatistic;
import com.nle.repository.dto.ShippingLineStatistic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data SQL repository for the GateMove entity.
 */
@Repository
public interface GateMoveRepository extends JpaRepository<GateMove, Long> {
    Page<GateMove> findAllByDepoOwnerAccount_CompanyEmail(String depoOwnerAccount, Pageable pageable);

    Page<GateMove> findAllByDepoOwnerAccount_CompanyEmailAndGateMoveType(String depoOwnerAccount, String gateMoveType, Pageable pageable);

    @Query("select new com.nle.repository.dto.MoveStatistic(gm.gateMoveType, count (gm.gateMoveType)) from GateMove gm group by gm.gateMoveType")
    List<MoveStatistic> countTotalGateMoveByType();

    @Query("select new com.nle.repository.dto.ShippingLineStatistic(gm.fleetManager, count (gm.fleetManager)) from GateMove gm group by gm.fleetManager")
    List<ShippingLineStatistic> countTotalGateMoveByShippingLine();

}
