package com.nle.ui.model.request.booking;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DetailUnloadingRequest {
    private Long itemId;
    private int price = -1;
    private String container_number;
}
