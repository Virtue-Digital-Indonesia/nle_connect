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

    public static CountListResponse factory (String tx_date, Double totalGate, Double totalGateIN, Double totalGateOUT) {
        return  CountListResponse.builder()
                .tx_date(tx_date)
                .total_gateMove(totalGate)
                .total_gate_in(totalGateIN)
                .total_gate_out(totalGateOUT)
                .build();
    }

}
