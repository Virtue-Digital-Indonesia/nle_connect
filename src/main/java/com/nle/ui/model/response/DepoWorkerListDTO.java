package com.nle.ui.model.response;

import com.nle.constant.AccountStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.LocalDateTime;


@Getter
@Setter
@ToString
public class DepoWorkerListDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private Long id;
    private String androidId;
    private String fullName;
    private LocalDateTime createdDate;
    private AccountStatus accountStatus;
}
