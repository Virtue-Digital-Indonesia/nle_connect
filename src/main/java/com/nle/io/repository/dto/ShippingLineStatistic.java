package com.nle.io.repository.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShippingLineStatistic {
    private String fleetManager;
    private Long count;
}
