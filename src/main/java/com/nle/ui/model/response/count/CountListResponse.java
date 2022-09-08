package com.nle.ui.model.response.count;

import lombok.*;
import lombok.experimental.SuperBuilder;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Data
@SuperBuilder
public class CountListResponse {
    private String tx_date;
    private Double total_gate_in;
    private Double total_gate_out;
    private Double total_gateMove;
}
