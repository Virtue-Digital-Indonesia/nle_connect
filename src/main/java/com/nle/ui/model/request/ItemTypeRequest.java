package com.nle.ui.model.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ItemTypeRequest {
    private String itemCode;
    private String itemType;
    private int itemSize;
}
