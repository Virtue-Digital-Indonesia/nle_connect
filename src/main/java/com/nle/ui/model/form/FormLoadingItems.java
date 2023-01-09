package com.nle.ui.model.form;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FormLoadingItems {
    private String no;
    private String name;
    private String qty;
    private String price;
    private String subtotal;
}
