package com.nle.controller.dto;

import com.nle.constant.ContainerCondition;
import com.nle.constant.ContainerGrade;
import com.nle.constant.GateMoveSource;
import com.nle.constant.GateMoveType;
import com.nle.constant.ProcessType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;


@Getter
@Setter
@ToString
public class GateMoveCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private LocalDateTime txDate;

    private String longitude;

    private String latitude;

    private ProcessType processType;

    private GateMoveType gateMoveType;

    private String depot;

    private String shippingLine;

    private String containerNumber;

    private String isoCode;

    private ContainerCondition condition;

    private Boolean clean;

    private ContainerGrade grade;

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

    private GateMoveSource gateMoveSource;
}
