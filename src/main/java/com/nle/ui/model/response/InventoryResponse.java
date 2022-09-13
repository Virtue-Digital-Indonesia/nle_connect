package com.nle.ui.model.response;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
@ToString
public class InventoryResponse {

    private String depot;
    private String fleet_manager;
    private String container_number;
    private String iso_code;
    private String condition;
    private Boolean clean;
    private String grade;
    private String damage_by;
    private String discharge_port;
    private String date_manufacturer;
}
