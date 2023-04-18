package com.nle.shared.dto.insw;

import com.nle.shared.dto.taxministry.TaxMinistryRequestDTO;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter
@Getter
public class InswSyncDataDTO {
    //penambahan struktur data sesuai dengan permintaan INSW
    private String activity;
    private String idPlatform = "PL035";
    private String carrier;
    private String clean;
    private String condition;
    private String containerNumber;
    private String customer;
    private String dateManufacturing;
    private String deliveryPort;
    private String dischargePort;
    private String depot;
    private String driverName;
    private String fleetManager;
    private String grade;
    private String isoCode;
    private Double maxGross;
    private String blNumber;
    private String blDate;
    private String doNumber;
    private String doDate;
    private Double payload;
    private String processType;
    private String remarks;
    private Double tare;
    private String transportNumber;
    private String txDate;
    private String vessel;
    private String voyage;
    private Long amount;
    private String statusFeedback;
    private Long depoId;
    private TaxMinistryRequestDTO gateMoveData;
}
