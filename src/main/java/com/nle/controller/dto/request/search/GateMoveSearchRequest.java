package com.nle.controller.dto.request.search;

import com.nle.constant.GateMoveSource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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
    private String globalSearch;
}
