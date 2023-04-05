package com.nle.ui.model.response.count;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class MovesDownload {
    private String tx_date;
    private Long total;
    private Long gate_in;
    private Long gate_out;
}
