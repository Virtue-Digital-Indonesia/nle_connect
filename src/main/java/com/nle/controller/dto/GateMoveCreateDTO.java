package com.nle.controller.dto;

import com.nle.constant.ContainerCondition;
import com.nle.constant.ContainerGrade;
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

    private LocalDateTime dateTime;

    private String longitude;

    private String latitude;

    private ProcessType process;

    private GateMoveType type;

    private String depot;

    private String shippingLine;

    private String containerNumber;

    private String isoCode;

    private ContainerCondition containerCondition;

    private Boolean clean;

    private ContainerGrade grade;

    private String damageBy;

    private Double cost;

    private String orderNumber;

    private String customer;

    private String vessel;

    private String voyage;

    private String dischargePort;

    private String truckerName;

    private String truckNo;

    private String tare;

    private String payload;

    private String dateManufactured;

    private String remarks;

    private String photos;
}
