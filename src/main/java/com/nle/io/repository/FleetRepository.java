package com.nle.io.repository;

import com.nle.io.entity.Fleet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FleetRepository extends JpaRepository<Fleet, Long> {

    @Query(value = "SELECT * FROM fleets", nativeQuery = true)
    Page<Fleet> getAllFleet(Pageable pageable);

    @Query(value = "SELECT * FROM fleets WHERE fleets.fleet_code = :code", nativeQuery = true)
    Optional<Fleet> getByCode(@Param("code") String Code);

}
