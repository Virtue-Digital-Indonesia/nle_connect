package com.nle.ui.model.response.report;

import com.nle.constant.enums.ReportType;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CreateReportPaymentDTO {
    private Long id;
    private String labelReport;
    private ReportType reportType;
    private String bankCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String days;
}
