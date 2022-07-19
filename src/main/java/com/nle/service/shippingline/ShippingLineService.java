package com.nle.service.shippingline;

import com.nle.service.dto.ShippingLineDTO;

import java.util.List;

public interface ShippingLineService {
    ShippingLineDTO findByCode(String code);

    List<ShippingLineDTO> findAll();
}
