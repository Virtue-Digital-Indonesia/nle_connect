package com.nle.ui.model.response.report;

import com.nle.constant.enums.ReportType;
import com.nle.io.entity.InswShipping;
import lombok.Data;
import lombok.ToString;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
@Data
@ToString
public class ReportParameterResponse {

    private Long id;
    private String labelReport;
    private ReportType reportType;
    private Long depoId;
    private InswShipping fleet;
    private String bankCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String days;
    private LocalDateTime createdDate;
}
