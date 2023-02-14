package com.nle.ui.model.response.report;

import com.nle.constant.enums.ReportType;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class CreateReportGateMoveDTO {
    private Long id;
    private ReportType reportType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String days;
}
