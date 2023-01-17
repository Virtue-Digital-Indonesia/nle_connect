package com.nle.ui.model.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BookingTempDto {
    private String bank_code;
    private LocalDateTime paid_date;
}
