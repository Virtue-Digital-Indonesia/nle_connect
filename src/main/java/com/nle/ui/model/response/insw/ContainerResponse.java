package com.nle.ui.model.response.insw;

import com.nle.ui.model.response.ItemResponse;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ContainerResponse {
    private String depot;
    private String noContainer;
    private String size;
    private String type;
    private double quantity;
    private String quantityUnit;
    private ItemResponse itemResponse;
    private boolean activeStatus;
}
