package com.nle.io.repository;

import com.nle.io.entity.DepoFleet;
import com.nle.io.entity.Fleet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepoFleetRepository extends JpaRepository<DepoFleet, Long> {

    @Query(value = "SELECT df.fleet FROM DepoFleet df WHERE df.depoOwnerAccount.companyEmail =:companyEmail")
    Page<Fleet> getAllDepoFleet(@Param("companyEmail") String companyEmail, Pageable pageable);

    @Query(value = "SELECT df.fleet FROM DepoFleet df WHERE df.depoOwnerAccount.companyEmail =:companyEmail AND df.fleet.code = :fleetCode")
    Optional<Fleet> getFleetInDepo(@Param("companyEmail") String companyEmail, @Param("fleetCode") String fleetCode);
}
