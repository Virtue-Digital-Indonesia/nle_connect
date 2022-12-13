package com.nle.ui.model.response.count;

import java.util.List;

import com.nle.io.repository.dto.ShippingLineStatistic;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CountMoves {
    private String tx_date;
    private Long total;
    private List<ShippingLineStatistic> fleet_count;
}
