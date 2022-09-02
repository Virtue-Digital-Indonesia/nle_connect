package com.nle.ui.model.response.count;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Data
@Builder
public class CountListResponse {
    private Double total_gate_in;
    private Double total_gate_out;
    private Double total_gateMove;
}
