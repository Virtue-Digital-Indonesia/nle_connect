package com.nle.ui.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class XenditResponse {

     private String id;

     @JsonProperty("external_id")
     private String externalId;

     @JsonProperty("owner_id")
     private String ownerId;

     @JsonProperty("bank_code")
     private String bankCode;

     @JsonProperty("merchant_code")
     private String merchantCode;

     @JsonProperty("account_number")
     private String accountNumber;
     private String name;
     private String currency;
     private Long amount;

     @JsonProperty("is_single_use")
     private Boolean isSingleUse;

     @JsonProperty("is_closed")
     private Boolean isClosed;

     @JsonProperty("expiration_date")
     private String expirationDate;
     private String status;

     private String invoice_url;
}
