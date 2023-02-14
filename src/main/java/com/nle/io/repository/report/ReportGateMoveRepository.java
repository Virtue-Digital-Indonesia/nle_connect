package com.nle.io.repository.report;

import com.nle.io.entity.report.ReportGateMove;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportGateMoveRepository extends JpaRepository<ReportGateMove, Long> {

}
