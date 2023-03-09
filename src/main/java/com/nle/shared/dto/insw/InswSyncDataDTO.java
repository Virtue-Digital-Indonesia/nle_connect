package com.nle.shared.dto.insw;

import com.nle.shared.dto.taxministry.TaxMinistryRequestDTO;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class InswSyncDataDTO {
    private String activity;
    private String blDate;
    private String blNumber;
    private String doNumber;
    private String doDate;

    //TODO diperbaiki struktur data sesuai dengan permintaan INSW
    private Long depoId;
    private TaxMinistryRequestDTO taxMinistryRequestDTO;
}
