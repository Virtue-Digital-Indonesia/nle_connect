package com.nle.ui.model.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class IsoCodeContainerResponse {

    private Long id;

    private String iso_code;

    private String iso_description;

    private int iso_length;

    private float iso_height;

    private String iso_group;
}
