package com.nle.ui.model.response.insw;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ContainerResponse {
    public String depot;
    public String noContainer;
    public String size;
    public String type;
    public double quantity;
    public String quantityUnit;
}
