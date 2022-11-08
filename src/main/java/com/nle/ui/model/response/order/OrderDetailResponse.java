package com.nle.ui.model.response.order;

import com.nle.constant.enums.ItemTypeEnum;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class OrderDetailResponse {
    private Long id;
    private String item_name;
    private String sku;
    private String description;
    private int price;
    private ItemTypeEnum type;
    private Boolean status;
}
