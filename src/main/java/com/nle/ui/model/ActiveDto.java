package com.nle.ui.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActiveDto implements Serializable {
    private String activeStatus;
    private String message;
}
