package com.nle.io.repository.report;

import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.entity.report.ReportParameter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportParameterRepository extends JpaRepository<ReportParameter, Long> {
    @Query(value = "SELECT rp FROM ReportParameter rp WHERE rp.depoOwnerId = :depoOwner")
    Page<ReportParameter> findAllByDepoOwnerId(@Param("depoOwner") DepoOwnerAccount depoOwner, Pageable pageable);
}
