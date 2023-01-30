package com.nle.ui.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class FleetRequest {
    private String code;
    private String fleet_manager_company;
    private String city;
    private String country;
}
