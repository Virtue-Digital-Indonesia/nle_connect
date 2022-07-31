package com.nle.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nle.controller.dto.GateMoveCreateDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;


@Getter
@Setter
@ToString
public class GateMoveDTO extends GateMoveCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;

    private String fleetManager;

    private String deliveryPort;

    private String carrier;

    @JsonProperty("transportId")
    private String transportNumber;

    private String driverName;

    private Double maxGross;

    private String linkToGoogleMap;

    private String status;

    private String nleId;
}
