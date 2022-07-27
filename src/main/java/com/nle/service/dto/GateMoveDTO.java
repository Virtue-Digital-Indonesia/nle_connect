package com.nle.service.dto;

import com.nle.controller.dto.GateMoveCreateDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;


@Getter
@Setter
@ToString
public class GateMoveDTO extends GateMoveCreateDTO implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long id;
}
