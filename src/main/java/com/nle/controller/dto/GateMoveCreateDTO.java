package com.nle.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nle.constant.GateMoveSource;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;


@Getter
@Setter
@ToString
public class GateMoveCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("dateAndTime")
    private String txDate;

    private String longitude;

    private String latitude;

    @JsonProperty("process")
    private String processType;

    @JsonProperty("type")
    private String gateMoveType;

    private String depot;

    private String shippingLine;

    @JsonProperty("containerNo")
    private String containerNumber;

    private String isoCode;

    private String condition;

    private Boolean clean;

    private String grade;

    private String damageBy;

    private Double cost;

    private String orderNumber;

    private String customer;

    private String vessel;

    private String voyage;

    private String discargePort;

    private String truckerName;

    private String truckNo;

    private Double tare;

    private Double payload;

    private String dateManufacturer;

    private String remarks;

    private GateMoveSource source;
}
