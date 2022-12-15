package com.nle.ui.model.response.count;

import java.util.List;

import com.nle.io.repository.dto.GateMovesStatistic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CountMovesByDepotResponse {
    private String tx_date;
    List<GateMovesStatistic> gate_moves_count;
}
