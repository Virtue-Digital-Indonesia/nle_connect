package com.nle.ui.model.request.order;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class OrderDetailRequest {
    private Long itemId;
    private int price = -1;
}
