package com.nle.service.mapper;

import com.nle.domain.DepoOwnerAccount;
import com.nle.domain.VerificationToken;
import com.nle.service.dto.DepoOwnerAccountDTO;
import com.nle.service.dto.VerificationTokenDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link VerificationToken} and its DTO {@link VerificationTokenDTO}.
 */
@Mapper(componentModel = "spring")
public interface VerificationTokenMapper extends EntityMapper<VerificationTokenDTO, VerificationToken> {
    @Mapping(target = "depoOwnerAccount", source = "depoOwnerAccount", qualifiedByName = "depoOwnerAccountId")
    VerificationTokenDTO toDto(VerificationToken s);

    @Named("depoOwnerAccountId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    DepoOwnerAccountDTO toDtoDepoOwnerAccountId(DepoOwnerAccount depoOwnerAccount);
}
