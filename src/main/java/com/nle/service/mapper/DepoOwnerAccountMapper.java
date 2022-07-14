package com.nle.service.mapper;

import com.nle.domain.DepoOwnerAccount;
import com.nle.service.dto.DepoOwnerAccountDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link DepoOwnerAccount} and its DTO {@link DepoOwnerAccountDTO}.
 */
@Mapper(componentModel = "spring")
public interface DepoOwnerAccountMapper extends EntityMapper<DepoOwnerAccountDTO, DepoOwnerAccount> {
}
