package com.nle.service.dto.taxministry;

import lombok.Data;

@Data
public class TaxMinistryRequestDTO {

    private Long id;
    private String carrier;
    // convert from boolean to string
    private String clean;
    private String condition;
    private String containerNumber;
    private String customer;
    // map from dateManufacturer
    private String dateManufacturing;
    private String deliveryPort;
    private String depot;
    private String discargePort;
    private String driveName;
    private String fleetManager;
    private String grade;
    private String isoCode;
    private Double maxGross;
    private String orderNumber;
    private Double payload;
    private String processType;
    private String remark;
    private Double tare;
    private String transportNumber;
    private String txDate;
    private String vessel;
    private String voyage;
    private String amount;
}
