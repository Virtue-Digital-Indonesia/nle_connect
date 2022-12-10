package com.nle.io.repository;

import com.nle.io.entity.admin.Admin;
import com.nle.io.repository.dto.ShippingLineStatistic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);

    @Query("select new com.nle.io.repository.dto.ShippingLineStatistic(gm.fleet_manager, count (gm.fleet_manager)) " +
            "from GateMove gm " +
            "group by gm.fleet_manager " +
            "order by count(gm.fleet_manager) desc")
    List<ShippingLineStatistic> countFleetManager();
}
