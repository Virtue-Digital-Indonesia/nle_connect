package com.nle.ui.model.response;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DepoFleetResponse extends FleetResponse{
    private Long depo_fleet_id;
    private String name;
    private String itemInfo;
}
