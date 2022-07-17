package com.nle.mapper;

import com.nle.entity.GateMove;
import com.nle.service.dto.GateMoveDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link GateMove} and its DTO {@link GateMoveDTO}.
 */
@Mapper(componentModel = "spring")
public interface GateMoveMapper extends EntityMapper<GateMoveDTO, GateMove> {
}
