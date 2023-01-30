package com.nle.ui.model.response.booking;

import com.nle.ui.model.response.ItemResponse;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DetailUnloadingResponse  extends ItemResponse {
    private String container_number;
}
