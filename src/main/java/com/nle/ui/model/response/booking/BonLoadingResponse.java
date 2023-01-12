package com.nle.ui.model.response.booking;

import com.nle.ui.model.response.BonResponse;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BonLoadingResponse extends BonResponse {
    private String bon_no;
}
