package com.nle.config.openfeign;

import com.nle.service.dto.taxministry.TaxMinistryRequestDTO;
import com.nle.service.dto.taxministry.TaxMinistryResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "taxMinistryServiceClient", url = "${app.url.tax-ministry}", configuration = TaxMinistryClientConfiguration.class)
public interface TaxMinistryServiceClient {
    @PostMapping
    TaxMinistryResponseDTO syncDataToTaxMinistry(TaxMinistryRequestDTO taxMinistryRequestDTO);
}
