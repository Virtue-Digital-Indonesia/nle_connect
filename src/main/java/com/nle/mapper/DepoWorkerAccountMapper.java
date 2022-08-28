package com.nle.mapper;

import com.nle.io.entity.DepoWorkerAccount;
import com.nle.shared.dto.DepoWorkerAccountDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link DepoWorkerAccount} and its DTO {@link DepoWorkerAccountDTO}.
 */
@Mapper(componentModel = "spring")
public interface DepoWorkerAccountMapper extends EntityMapper<DepoWorkerAccountDTO, DepoWorkerAccount> {
}
