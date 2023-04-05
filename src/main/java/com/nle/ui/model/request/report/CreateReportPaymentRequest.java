package com.nle.ui.model.request.report;

import lombok.Data;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@ToString
public class CreateReportPaymentRequest {
    private String labelReport;
    private String bankCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String days;
}
