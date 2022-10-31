package com.nle.ui.model.response;

import com.nle.constant.enums.ItemTypeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ItemResponse {

    private String item_name;
    private String sku;
    private String description;
    private int price;
    private ItemTypeEnum type;
    private Boolean status;
    private Boolean deleted;
    private DepoFleetResponse fleet;

}
