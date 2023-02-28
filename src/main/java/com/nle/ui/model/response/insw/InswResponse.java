package com.nle.ui.model.response.insw;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nle.ui.model.response.InswShippingResponse;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class InswResponse {
    private String shippingLine;
    private InswShippingResponse shippingFleet;
    private String noBL;
    private String dateBL;
    private String doReleaseDate;
    private String shipper;
    private String consignee;
    private String npwpConsignee;
    private String notifyParty;
    private String terminalOperator;
    private String vesselName;
    private String voyageCode;
    private String callSign;
    @JsonProperty(value = "PortOfLoading")
    private String portOfLoading;
    @JsonProperty(value = "portOfDischarge")
    private String portOfDischarge;
    @JsonProperty(value = "container")
    private List<ContainerResponse> container;
}
