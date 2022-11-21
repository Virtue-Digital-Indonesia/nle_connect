package com.nle.ui.model.response.booking;

import com.nle.ui.model.response.ItemResponse;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DetailLoadingResponse extends ItemResponse {
    private int quantity;
}
