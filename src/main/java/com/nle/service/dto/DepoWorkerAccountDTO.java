package com.nle.service.dto;

import com.nle.constant.AccountStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;


@Getter
@Setter
@ToString
public class DepoWorkerAccountDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String fullName;
    private String organizationCode;
    private String gateName;
    private AccountStatus accountStatus;
}
