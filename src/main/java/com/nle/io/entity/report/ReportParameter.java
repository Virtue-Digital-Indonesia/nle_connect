package com.nle.io.entity.report;

import com.nle.constant.enums.ReportType;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.entity.InswShipping;
import com.nle.io.entity.common.AbstractAuditingEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
@Entity
@Table(name = "report_parameter")
@Getter
@Setter
public class ReportParameter extends AbstractAuditingEntity implements Serializable {
    private static final long serialVersionUID=1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "label_report")
    private String labelReport;

    @Column(name = "report_type")
    @Enumerated(EnumType.STRING)
    private ReportType reportType;
    @ManyToOne
    @JoinColumn(name = "depo_owner_id", referencedColumnName = "id")
    private DepoOwnerAccount depoOwnerId;
    @ManyToOne
    @JoinColumn(name = "fleet_id", referencedColumnName = "id")
    private InswShipping fleet;
    @Column(name = "bank_code")
    private String bankCode;
    @Column(name = "start_date")
    private LocalDate startDate;
    @Column(name = "end_date")
    private LocalDate endDate;
    @Column(name = "start_time")
    private LocalTime startTime;
    @Column(name = "end_time")
    private LocalTime endTime;
    @Column(name = "days")
    private String days;
}
