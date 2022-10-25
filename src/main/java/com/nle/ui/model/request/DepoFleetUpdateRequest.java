package com.nle.ui.model.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DepoFleetUpdateRequest extends DepoFleetRegisterRequest{
    private Long id;
}
