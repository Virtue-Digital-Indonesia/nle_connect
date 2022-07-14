package com.nle.service.mapper;

import com.nle.domain.VerificationToken;
import com.nle.service.dto.VerificationTokenDTO;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity {@link VerificationToken} and its DTO {@link VerificationTokenDTO}.
 */
@Mapper(componentModel = "spring")
public interface VerificationTokenMapper extends EntityMapper<VerificationTokenDTO, VerificationToken> {
}
