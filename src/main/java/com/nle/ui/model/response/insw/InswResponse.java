package com.nle.ui.model.response.insw;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class InswResponse {
    public String shippingLine;
    public String noBL;
    public String dateBL;
    public String doReleaseDate;
    public String shipper;
    public String consignee;
    public String npwpConsignee;
    public String notifyParty;
    public String terminalOperator;
    public String vesselName;
    public String voyageCode;
    public String callSign;
    @JsonProperty("PortOfLoading")
    public String portOfLoading;
    public String portOfDischarge;
    public List<ContainerResponse> container;
}
