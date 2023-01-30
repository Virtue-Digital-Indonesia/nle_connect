package com.nle.ui.model.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BankDepoRequest {

    private String bank_code;
    private String account_holder_name;
    private String account_number;
    private String description_bank;
    private Boolean default_bank;
}
