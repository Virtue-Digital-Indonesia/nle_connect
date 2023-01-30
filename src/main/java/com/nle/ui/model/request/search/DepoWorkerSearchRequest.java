package com.nle.ui.model.request.search;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class DepoWorkerSearchRequest {

    private String androidId;
    private String fullName;
    private String gateName;
    private String accountStatus;
    private String globalSearch;
}
