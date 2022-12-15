package com.nle.ui.model.response.count;

import java.util.List;

import com.nle.io.repository.dto.ShippingLineStatistic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class TotalMoves {
    private String tx_date;
    private Long total;
    private List<ShippingLineStatistic> fleet_count;
}
