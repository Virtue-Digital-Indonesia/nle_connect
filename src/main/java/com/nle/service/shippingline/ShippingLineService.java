package com.nle.service.shippingline;

import com.nle.service.dto.ShippingLineDTO;

public interface ShippingLineService {
    ShippingLineDTO findByCode(String code);
}
