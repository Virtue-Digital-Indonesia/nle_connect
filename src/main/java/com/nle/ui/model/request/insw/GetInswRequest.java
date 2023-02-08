package com.nle.ui.model.request.insw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GetInswRequest {
    @JsonProperty(value = "depo_id")
    private Long depoId;
}
