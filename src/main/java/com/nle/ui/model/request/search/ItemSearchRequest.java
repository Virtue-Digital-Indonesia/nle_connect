package com.nle.ui.model.request.search;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ItemSearchRequest {
    @JsonProperty("item_name")
    private String itemName;
    private String sku;
    private String description;
    private Integer price;
    private String type;
    private String fleetCode;
    private Boolean status;
    private String globalSearch;
}
