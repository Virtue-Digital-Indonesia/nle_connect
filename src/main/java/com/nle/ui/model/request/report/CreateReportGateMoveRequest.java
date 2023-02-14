package com.nle.ui.model.request.report;

import com.nle.constant.enums.ReportType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.LocalTime;
@Getter
@Setter
@ToString
public class CreateReportGateMoveRequest {
    private ReportType reportType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String days;
}
