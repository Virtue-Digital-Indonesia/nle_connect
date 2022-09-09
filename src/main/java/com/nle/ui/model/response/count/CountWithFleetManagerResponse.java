package com.nle.ui.model.response.count;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Data
@SuperBuilder
@NoArgsConstructor
public class CountWithFleetManagerResponse extends CountListResponse{
    private String fleet_manager;

    public static CountWithFleetManagerResponse factory (String fleet_manager, String tx_date, Double totalGate, Double totalGateIN, Double totalGateOUT) {
        return CountWithFleetManagerResponse
                .builder()
                .fleet_manager(fleet_manager)
                .tx_date(tx_date)
                .total_gateMove(totalGate)
                .total_gate_in(totalGateIN)
                .total_gate_out(totalGateOUT)
                .build();
    }
}
