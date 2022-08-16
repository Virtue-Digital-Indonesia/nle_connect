package com.nle.repository;

import com.nle.entity.GateMove;
import com.nle.repository.dto.MoveStatistic;
import com.nle.repository.dto.ShippingLineStatistic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Spring Data SQL repository for the GateMove entity.
 */
@Repository
public interface GateMoveRepository extends JpaRepository<GateMove, Long> {
    Page<GateMove> findAllByDepoOwnerAccount_CompanyEmailAndTxDateFormattedBetween(String depoOwnerAccount, LocalDateTime from, LocalDateTime to, Pageable pageable);

    Page<GateMove> findAllByDepoOwnerAccount_CompanyEmailAndGateMoveType(String depoOwnerAccount, String gateMoveType, Pageable pageable);

    @Query("select new com.nle.repository.dto.MoveStatistic(gm.gateMoveType, count (gm.gateMoveType)) " +
        "from GateMove gm " +
        "where gm.depoOwnerAccount.companyEmail =:companyEmail " +
        "group by gm.gateMoveType")
    List<MoveStatistic> countTotalGateMoveByType(@Param("companyEmail") String companyEmail);

    @Query("select new com.nle.repository.dto.ShippingLineStatistic(gm.fleet_manager, count (gm.fleet_manager)) " +
        "from GateMove gm " +
        "where gm.depoOwnerAccount.companyEmail =:companyEmail " +
        "group by gm.fleet_manager")
    List<ShippingLineStatistic> countTotalGateMoveByShippingLine(@Param("companyEmail") String companyEmail);

    List<GateMove> findAllByStatus(String status);

    @Modifying
    @Query("update GateMove gm set gm.status =:status, gm.syncToTaxMinistryDate =:syncToTaxMinistryDate where gm.id =:id")
    int updateGateMoveStatusById(@Param("status") String status,
                                 @Param("syncToTaxMinistryDate") LocalDateTime syncToTaxMinistryDate,
                                 @Param("id") Long id);

}
