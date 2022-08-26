package com.nle.shared.service.shippingline;

import com.nle.shared.dto.ShippingLineDTO;

import java.util.List;

public interface ShippingLineService {
    ShippingLineDTO findByCode(String code);

    List<ShippingLineDTO> findAll();
}
