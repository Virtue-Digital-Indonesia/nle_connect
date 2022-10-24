package com.nle.ui.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DepoFleetRegisterRequest {
    private String name;
    private String fleet_code;
}
