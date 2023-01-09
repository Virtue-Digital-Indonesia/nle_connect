package com.nle.ui.model.form;

import fr.opensagres.xdocreport.document.images.IImageProvider;
import fr.opensagres.xdocreport.template.annotations.FieldMetadata;
import fr.opensagres.xdocreport.template.annotations.ImageMetadata;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FormBonDTO {
    private String depoName;
    private String txDate;
    private String id;
    private String address;
    private String billLading;
    private String container;
    private String item;
    private String fleet;
    private String consignee;
    private String npwp;
    private String npwpAddress;

    private String deliveryNo;
    private String qty;
    private String shipper;

    private IImageProvider qrCode;

    @FieldMetadata(images = { @ImageMetadata(name = "qrCode") })
    public IImageProvider getQrCode() {
        return qrCode;
    }
}
