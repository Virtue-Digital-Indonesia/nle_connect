package com.nle.io.entity.report;

import com.nle.constant.enums.ReportType;
import com.nle.io.entity.common.AbstractAuditingEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.LocalTime;
@Entity
@Table(name = "report_gatemove")
@Getter
@Setter
public class ReportGateMove extends AbstractAuditingEntity implements Serializable {
    private static final long serialVersionUID=1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "report_type")
    private ReportType reportType;
    @Column(name = "start_date")
    private LocalDateTime startDate;
    @Column(name = "end_date")
    private LocalDateTime endDate;
    @Column(name = "start_time")
    private LocalTime startTime;
    @Column(name = "end_time")
    private LocalTime endTime;
    @Column(name = "days")
    private String days;
}
