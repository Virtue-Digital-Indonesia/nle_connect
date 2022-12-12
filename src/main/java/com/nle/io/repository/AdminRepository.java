package com.nle.io.repository;

import com.nle.io.entity.admin.Admin;
import com.nle.io.repository.dto.LocationStatistic;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);

    @Query("select new com.nle.io.repository.dto.LocationStatistic(gm.depot, count (gm.depot)) " +
            "from GateMove gm " +
            "group by gm.depot " +
            "order by count(gm.depot) desc")
    List<LocationStatistic> countLocation();
}
