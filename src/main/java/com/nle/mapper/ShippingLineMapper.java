package com.nle.mapper;

import com.nle.io.entity.ShippingLine;
import com.nle.shared.dto.ShippingLineDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link ShippingLine} and its DTO {@link ShippingLineDTO}.
 */
@Mapper(componentModel = "spring")
public interface ShippingLineMapper extends EntityMapper<ShippingLineDTO, ShippingLine> {
}
