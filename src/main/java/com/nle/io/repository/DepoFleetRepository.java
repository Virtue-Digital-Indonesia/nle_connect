package com.nle.io.repository;

import com.nle.io.entity.DepoFleet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepoFleetRepository extends JpaRepository<DepoFleet, Long> {

    @Query(value = "SELECT df FROM DepoFleet df WHERE df.depoOwnerAccount.companyEmail =:companyEmail")
    Page<DepoFleet> getAllDepoFleet(@Param("companyEmail") String companyEmail, Pageable pageable);

    @Query(value = "SELECT df FROM DepoFleet df WHERE df.depoOwnerAccount.companyEmail =:companyEmail AND df.fleet.code = :fleetCode")
    Optional<DepoFleet> getFleetInDepo(@Param("companyEmail") String companyEmail, @Param("fleetCode") String fleetCode);

    @Query(value = "SELECT df FROM DepoFleet df " +
            "WHERE df.depoOwnerAccount.companyEmail =:companyEmail " +
            "AND (:name is null OR (lower(df.name) like lower(concat('%',:name,'%')))) " +
            "AND (:id is null OR(df.id= :id)) " +
            "AND (:fleetCode is null OR(lower(df.fleet.code) like lower(concat('%',:fleetCode,'%')))) " +
            "AND (:fleetManagerCompany is null OR(lower(df.fleet.fleet_manager_company) like lower(concat('%',:fleetManagerCompany,'%')))) " +
            "AND (:city is null OR(lower(df.fleet.city) like lower(concat('%',:city,'%')))) "+
            "AND (:country is null OR(lower(df.fleet.country) like lower(concat('%',:country,'%')))) " +
            "AND (:globalSearch is null " +
            "       OR(lower(df.name) like lower(concat('%',:globalSearch,'%'))) " +
            "       OR(lower(df.fleet.code) like lower(concat('%',:globalSearch,'%'))) " +
            "       OR(lower(df.fleet.fleet_manager_company) like lower(concat('%',:globalSearch,'%'))) " +
            "       OR(lower(df.fleet.city) like lower(concat('%',:globalSearch,'%'))) " +
            "       OR(lower(df.fleet.country) like lower(concat('%',:globalSearch,'%')))" +
            ")")
    Page<DepoFleet> searchDepoFleet(String companyEmail,
                                    String name,
                                    Long id,
                                    String fleetCode,
                                    String fleetManagerCompany,
                                    String city,
                                    String country,
                                    String globalSearch,
                                    Pageable pageable);
}
