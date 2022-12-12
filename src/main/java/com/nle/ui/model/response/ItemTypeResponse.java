package com.nle.ui.model.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ItemTypeResponse {
    private Long id;
    private String itemCode;
    private String itemType;
    private int itemSize;
}
