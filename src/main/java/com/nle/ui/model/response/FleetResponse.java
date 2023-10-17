package com.nle.ui.model.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class FleetResponse {
    private Long id;
    private String code;
    private String fleet_manager_company;
}
