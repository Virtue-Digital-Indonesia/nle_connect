package com.nle.ui.model.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ItemTypeRequest {
    private String item_code;
    private String item_type;
    private String item_size;
}
