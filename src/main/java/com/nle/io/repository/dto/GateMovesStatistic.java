package com.nle.io.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GateMovesStatistic {
    private String depot;
    private Long gate_in;
    private Long gate_out;
    private Long gate_moves;
}
