package com.nle.ui.model.request.report;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;
@Getter
@Setter
@ToString
public class CreateReportGateMoveRequest {
    private String labelReport;
    private String fleetCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String days;
}
