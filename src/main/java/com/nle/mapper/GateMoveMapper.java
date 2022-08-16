package com.nle.mapper;

import com.nle.entity.GateMove;
import com.nle.service.dto.ftp.MoveDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link GateMove} and its DTO {@link MoveDTO}.
 */
@Mapper(componentModel = "spring")
public interface GateMoveMapper extends EntityMapper<MoveDTO, GateMove> {
}
