package com.nle.ui.model.request.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class DepoFleetSearchRequest {
    private Long id;
    @JsonProperty("depo_fleet_id")
    private Long depoFleetId;
    private String name;
    private String code;
    private String city;
    private String country;
    @JsonProperty("fleet_manager_company")
    private String fleetManagerCompany;
    private  String globalSearch;

}
