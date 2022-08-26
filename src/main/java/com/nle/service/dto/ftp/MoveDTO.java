package com.nle.service.dto.ftp;


import com.nle.constant.enums.GateMoveSource;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Data
@ToString
public class MoveDTO {
    @NotNull(message = "tx_date can not be null")
    private String tx_date;

    private String longitude;

    private String latitude;

    @NotNull(message = "process_type can not be null")
    private String process_type;

    @NotNull(message = "depot can not be null")
    private String depot;

    @NotNull(message = "fleet_manager can not be null")
    private String fleet_manager;

    @NotNull(message = "container_number can not be null")
    private String container_number;

    @NotNull(message = "iso_code can not be null")
    private String iso_code;

    @NotNull(message = "condition can not be null")
    private String condition;

    @NotNull(message = "clean can not be null")
    private String clean;

    @NotNull(message = "grade can not be null")
    private String grade;

    private String damageBy;

    private Long amount;

    @NotNull(message = "order_number can not be null")
    private String order_number;

    @NotNull(message = "customer can not be null")
    private String customer;

    private String vessel;
    private String voyage;
    private String discarge_port;

    @NotNull(message = "carrier can not be null")
    private String carrier;
    private String transport_number;

    @NotNull(message = "tare can not be null")
    private Double tare;

    @NotNull(message = "payload can not be null")
    private Double payload;

    @NotNull(message = "date_manufacturer can not be null")
    private String date_manufacturer;

    private String remarks;

    private String delivery_port;
    private String driver_name;

    @NotNull(message = "Max Gross can not be null")
    private Double max_gross;

    private String status;
    private GateMoveSource source;
}
