package com.nle.ui.model.response.insw;

import com.nle.io.entity.Item;
import com.nle.ui.model.response.ItemResponse;
import com.nle.ui.model.response.ItemTypeResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

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
}
