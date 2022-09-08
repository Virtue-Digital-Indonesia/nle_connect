package com.nle.ui.model.response.count;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@Data
@SuperBuilder
@NoArgsConstructor
public class CountWithFleetManagerResponse extends CountListResponse{
    private String fleet_manager;
}
