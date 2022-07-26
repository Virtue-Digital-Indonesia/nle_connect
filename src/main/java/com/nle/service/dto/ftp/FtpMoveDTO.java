package com.nle.service.dto.ftp;


import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class FtpMoveDTO {
    private String tx_date;
    private String process_type;
    private String depot;
    private String fleet_manager;
    private String container_number;
    private String iso_code;
    private String condition;
    private String date_manufacturer;
    private String clean;
    private String grade;
    private String order_number;
    private String customer;
    private String vessel;
    private String voyage;
    private String discarge_port;
    private String delivery_port;
    private String carrier;
    private String transport_number;
    private String driver_name;
    private String tare;
    private String payload;
    private String max_gross;
    private String remark;
}
