package com.nle.ui.model.response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ItemTypeResponse {
    private Long id;
    private String item_code;
    private String item_type;
    private String item_size;
}
