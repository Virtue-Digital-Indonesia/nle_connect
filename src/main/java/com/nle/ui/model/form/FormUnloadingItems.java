package com.nle.ui.model.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FormUnloadingItems {
    private String no;
    private String name;
    private String container;
    private String price;
    private String subtotal;
}
