package com.nle.ui.model.request;

import com.nle.constant.enums.ItemTypeEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class CreateItemRequest {

    private String item_code;
    private String sku;
    private String description;
    private int price;
    private ItemTypeEnum type;
    private Boolean status;
    private String fleetCode;
}
