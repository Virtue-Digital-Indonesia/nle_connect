package com.nle.io.repository;

import com.nle.ui.model.request.search.GateMoveSearchRequest;
import com.nle.io.entity.GateMove;
import com.nle.io.repository.dto.GateMovesStatistic;
import com.nle.io.repository.dto.LocationStatistic;
import com.nle.io.repository.dto.MoveStatistic;
import com.nle.io.repository.dto.ShippingLineStatistic;
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

        static final String SEARCH_GATEMOVE_QUERY = "SELECT gm FROM GateMove gm " +
                        "WHERE gm.depoOwnerAccount.companyEmail = :companyEmail " +
                        "AND (:#{#request.process_type} IS NULL OR LOWER(gm.process_type) LIKE LOWER(concat('%',:#{#request.process_type}, '%'))) "
                        +
                        "AND (:#{#request.depot} IS NULL OR LOWER(gm.depot) LIKE LOWER(concat('%', :#{#request.depot}, '%'))) "
                        +
                        "AND (:#{#request.fleet_manager} IS NULL OR LOWER(gm.fleet_manager) LIKE LOWER(concat('%', :#{#request.fleet_manager}, '%'))) "
                        +
                        "AND (:#{#request.container_number} IS NULL OR gm.container_number LIKE concat('%', :#{#request.container_number}, '%')) "
                        +
                        "AND (:#{#request.iso_code} IS NULL OR LOWER(gm.iso_code) LIKE LOWER(concat('%', :#{#request.iso_code}, '%'))) "
                        +
                        "AND (:#{#request.condition} IS NULL OR LOWER(gm.condition) LIKE LOWER(concat('%', :#{#request.condition}, '%'))) "
                        +
                        "AND (:#{#request.grade} IS NULL OR LOWER(gm.grade) LIKE LOWER(concat('%', :#{#request.grade}, '%'))) "
                        +
                        "AND (:#{#request.order_number} IS NULL OR LOWER(gm.order_number) LIKE LOWER(concat('%', :#{#request.order_number}, '%'))) "
                        +
                        "AND (:#{#request.customer} IS NULL OR LOWER(gm.customer) LIKE LOWER(concat('%', :#{#request.customer}, '%'))) "
                        +
                        "AND (:#{#request.carrier} IS NULL OR LOWER(gm.carrier) LIKE LOWER(concat('%', :#{#request.carrier}, '%'))) "
                        +
                        "AND (:#{#request.transport_number} IS NULL OR LOWER(gm.transport_number) LIKE LOWER(concat('%', :#{#request.transport_number}, '%'))) "
                        +
                        "AND (:#{#request.date_manufacturer} IS NULL OR LOWER(gm.date_manufacturer) LIKE LOWER(concat('%', :#{#request.date_manufacturer}, '%'))) "
                        +
                        "AND (:#{#request.gateMoveType} IS NULL OR LOWER(gm.gateMoveType) LIKE LOWER(concat('%', :#{#request.gateMoveType}, '%'))) "
                        +
                        "AND (:#{#request.status} IS NULL OR LOWER(gm.status) LIKE LOWER(concat('%', :#{#request.status}, '%'))) "
                        +
                        "AND (:#{#request.source} IS NULL OR UPPER(gm.source) LIKE UPPER(concat('%', :#{#request.source}, '%'))) "
                        +
                        "AND (:#{#request.tare} IS NULL OR gm.tare LIKE concat('%', :#{#request.tare}, '%' )) " +
                        "AND (:#{#request.payload} IS NULL OR gm.payload LIKE concat('%', :#{#request.payload}, '%')) "
                        +
                        "AND (:#{#request.max_gross} IS NULL OR gm.max_gross LIKE concat('%', :#{#request.max_gross}, '%')) "
                        +
                        "AND (:#{#request.from} IS NULL OR gm.tx_date >= :#{#request.from}) " +
                        "AND (:#{#request.to} IS NULL OR gm.tx_date <= CONCAT(:#{#request.to},'T24:00:01')) " +
                        "AND (:#{#request.globalSearch} IS NULL " +
                        "OR LOWER(gm.process_type) LIKE LOWER(concat('%', :#{#request.globalSearch}, '%')) " +
                        "OR LOWER(gm.depot) LIKE LOWER(concat('%', :#{#request.globalSearch}, '%')) " +
                        "OR LOWER(gm.fleet_manager) LIKE LOWER(concat('%', :#{#request.globalSearch}, '%')) " +
                        "OR LOWER(gm.container_number) LIKE LOWER(concat('%', :#{#request.globalSearch}, '%')) " +
                        "OR LOWER(gm.iso_code) LIKE LOWER(concat('%', :#{#request.globalSearch}, '%')) " +
                        "OR LOWER(gm.condition) LIKE LOWER(concat('%', :#{#request.globalSearch}, '%')) " +
                        "OR LOWER(gm.grade) LIKE LOWER(concat('%', :#{#request.globalSearch}, '%')) " +
                        "OR LOWER(gm.order_number) LIKE LOWER(concat('%', :#{#request.globalSearch}, '%')) " +
                        "OR LOWER(gm.customer) LIKE LOWER(concat('%', :#{#request.globalSearch}, '%')) " +
                        "OR LOWER(gm.carrier) LIKE LOWER(concat('%', :#{#request.globalSearch}, '%')) " +
                        "OR LOWER(gm.transport_number) LIKE LOWER(concat('%', :#{#request.globalSearch}, '%')) " +
                        "OR LOWER(gm.date_manufacturer) LIKE LOWER(concat('%', :#{#request.globalSearch}, '%')) " +
                        "OR LOWER(gm.gateMoveType) LIKE LOWER(concat('%', :#{#request.globalSearch}, '%')) " +
                        "OR LOWER(gm.status) LIKE LOWER(concat('%', :#{#request.globalSearch}, '%')) " +
                        "OR LOWER(gm.source) LIKE LOWER(concat('%', :#{#request.globalSearch}, '%')) " +
                        ") ";

    Page<GateMove> findAllByDepoOwnerAccount_CompanyEmailAndTxDateFormattedBetween(String depoOwnerAccount,
                                                                                   LocalDateTime from,
                                                                                   LocalDateTime to,
                                                                                   Pageable pageable);

    @Query("select new com.nle.io.repository.dto.ShippingLineStatistic(gm.fleet_manager, count (gm.fleet_manager)) " +
            "from GateMove gm " +
            "where gm.depoOwnerAccount.companyEmail =:companyEmail " +
            "group by gm.fleet_manager")
    List<ShippingLineStatistic> countTotalGateMoveByShippingLine(@Param("companyEmail") String companyEmail);

    List<GateMove> findAllByStatus(String status);

    @Modifying
    @Query("update GateMove gm set gm.status =:status, " +
            "gm.syncToTaxMinistryDate =:syncToTaxMinistryDate " +
            "gm.nleId = :IdTraffic " +
            "where gm.id =:id")
    int updateGateMoveStatusById(@Param("status") String status,
                                 @Param("syncToTaxMinistryDate") LocalDateTime syncToTaxMinistryDate,
                                 @Param("IdTraffic") String IdTraffic,
                                 @Param("id") Long id);

    @Query(value = SEARCH_GATEMOVE_QUERY)
    Page<GateMove>searchByCondition(@Param("companyEmail") String companyEmail,
                                    Pageable pageable,
                                    @Param("request") GateMoveSearchRequest request);

    @Query(value = "SELECT gm FROM GateMove gm WHERE gm.depoOwnerAccount.companyEmail = :companyEmail " +
            "AND (gm.tx_date BETWEEN :from AND :to)  ORDER BY gm.tx_date")
    List<GateMove> countTotalGateMoveByDuration(@Param("companyEmail") String companyEmail,
                                                @Param("from") String from,
                                                @Param("to") String to);

    Page<GateMove> findAllByDepoOwnerAccount_CompanyEmailAndGateMoveType(String depoOwnerAccount,
                    String gateMoveType,
                    Pageable pageable);

    @Query("select new com.nle.io.repository.dto.MoveStatistic(gm.gateMoveType, count (gm.gateMoveType)) " +
                    "from GateMove gm " +
                    "where gm.depoOwnerAccount.companyEmail =:companyEmail " +
                    "group by gm.gateMoveType")
    List<MoveStatistic> countTotalGateMoveByType(@Param("companyEmail") String companyEmail);

    @Modifying
    @Query("update GateMove gm set gm.status =:status, gm.syncToTaxMinistryDate =:syncToTaxMinistryDate where gm.id =:id")
    int updateGateMoveStatusById(@Param("status") String status,
                    @Param("syncToTaxMinistryDate") LocalDateTime syncToTaxMinistryDate,
                    @Param("id") Long id);

    @Query("select new com.nle.io.repository.dto.LocationStatistic(gm.depot, count (gm.depot)) " +
                    "from GateMove gm " +
                    "group by gm.depot " +
                    "order by count(gm.depot) desc")
    List<LocationStatistic> countLocation();

    @Query("select new com.nle.io.repository.dto.ShippingLineStatistic(gm.fleet_manager, count (gm.id)) "
                    +
                    "from GateMove gm " +
                    "where (gm.tx_date >= :from and gm.tx_date < :to) " +
                    "group by gm.fleet_manager " +
                    "order by count(gm.id) desc")
    List<ShippingLineStatistic> countFleetManagerByDate(@Param("from") String from, @Param("to") String to);

    @Query("select count(gm.id) "
                    +
                    "from GateMove gm " +
                    "where (gm.tx_date >= :from and gm.tx_date < :to)")
    Long countTotalFleetManagerByDate(@Param("from") String from,
                    @Param("to") String to);

    @Query("select new com.nle.io.repository.dto.ShippingLineStatistic(gm.fleet_manager, count (gm.id)) " +
                    "from GateMove gm " +
                    "group by gm.fleet_manager " +
                    "order by count(gm.id) desc")
    List<ShippingLineStatistic> countFleetManager();

    @Query("select new com.nle.io.repository.dto.GateMovesStatistic(gm.depot, " +
                    "sum(case when gm.gateMoveType='gate_in' then 1 when gm.gateMoveType='gate_out' then 0 end), " +
                    "sum(case when gm.gateMoveType='gate_in' then 0 when gm.gateMoveType='gate_out' then 1 end), " +
                    "count (gm.id)) "
                    +
                    "from GateMove gm " +
                    "where (gm.tx_date >= :from and gm.tx_date < :to) " +
                    "group by gm.depot")
    List<GateMovesStatistic> countGateMovesByDepot(@Param("from") String from, @Param("to") String to);
}
