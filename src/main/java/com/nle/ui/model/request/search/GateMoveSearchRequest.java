package com.nle.ui.model.request.search;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
public class GateMoveSearchRequest {

    private String process_type;
    private String depot;
    private String fleet_manager;
    private String container_number;
    private String iso_code;
    private String condition;
    private String grade;
    private String order_number;
    private String customer;
    private String carrier;
    private String transport_number;
    private String date_manufacturer;
    private String gateMoveType;
    private String status;
    private String source;


    private Double tare;
    private Double payload;
    private Double max_gross;

    private String from;
    private String to;
    private String globalSearch;
}
