package com.nle.io.repository;

import com.nle.io.entity.DepoFleet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepoFleetRepository extends JpaRepository<DepoFleet, Long> {

    @Query(value = "SELECT df FROM DepoFleet df WHERE df.depoOwnerAccount.companyEmail =:companyEmail and df.deleted = false")
    Page<DepoFleet> getAllDepoFleet(@Param("companyEmail") String companyEmail, Pageable pageable);

    @Query(value = "SELECT df FROM DepoFleet df WHERE df.depoOwnerAccount.companyEmail =:companyEmail AND df.fleet.code = :fleetCode AND df.deleted = false")
    Optional<DepoFleet> getFleetInDepo(@Param("companyEmail") String companyEmail, @Param("fleetCode") String fleetCode);

    @Query(value = "SELECT df FROM DepoFleet df " +
            "WHERE df.depoOwnerAccount.companyEmail =:companyEmail " +
            "AND (:name is null OR (lower(df.name) like lower(concat('%',:name,'%')))) " +
            "AND (:id is null OR(df.id= :id)) " +
            "AND (:fleetCode is null OR(lower(df.fleet.code) like lower(concat('%',:fleetCode,'%')))) " +
            "AND (:fleetManagerCompany is null OR(lower(df.fleet.description) like lower(concat('%',:fleetManagerCompany,'%')))) " +
            "AND (:globalSearch is null " +
            "       OR(lower(df.name) like lower(concat('%',:globalSearch,'%'))) " +
            "       OR(lower(df.fleet.code) like lower(concat('%',:globalSearch,'%'))) " +
            "       OR(lower(df.fleet.description) like lower(concat('%',:globalSearch,'%'))) " +
            ") " +
            "AND deleted=false ")
    Page<DepoFleet> searchDepoFleet(String companyEmail,
                                    String name,
                                    Long id,
                                    String fleetCode,
                                    String fleetManagerCompany,
                                    String globalSearch,
                                    Pageable pageable);

    @Query("SELECT df FROM DepoFleet df "+
            "WHERE (:location IS NULL OR LOWER(df.depoOwnerAccount.address) LIKE LOWER(CONCAT('%', :location, '%'))) "+
            "AND (:shippingLine IS NULL OR LOWER(df.fleet.code) LIKE LOWER(CONCAT('%', :shippingLine, '%')))")
    List<DepoFleet> getFromPortal(@Param("location") String location,@Param("shippingLine") String shippingLine);
}
