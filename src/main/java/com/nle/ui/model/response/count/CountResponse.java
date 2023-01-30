package com.nle.ui.model.response.count;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Data
@Builder
public class CountResponse {
    private Double total_moves;
    private List<CountListResponse> list_moves;
}
