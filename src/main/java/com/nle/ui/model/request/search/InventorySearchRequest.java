package com.nle.ui.model.request.search;

import com.nle.ui.model.response.InventoryResponse;
import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class InventorySearchRequest extends InventoryResponse {

    private String globalSearch;
}
