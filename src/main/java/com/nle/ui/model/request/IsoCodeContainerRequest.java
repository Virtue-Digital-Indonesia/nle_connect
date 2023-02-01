package com.nle.ui.model.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IsoCodeContainerRequest {

    private String iso_code;

    private String iso_description;

    private int iso_length;

    private float iso_height;

    private String iso_group;
}
