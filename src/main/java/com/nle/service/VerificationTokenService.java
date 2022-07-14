package com.nle.service;

import com.nle.constant.VerificationType;
import com.nle.domain.DepoOwnerAccount;
import com.nle.domain.VerificationToken;
import com.nle.service.dto.VerificationTokenDTO;

import java.util.List;
import java.util.Optional;

/**
 * Service Interface for managing {@link com.nle.domain.VerificationToken}.
 */
public interface VerificationTokenService {
    /**
     * Save a verificationToken.
     *
     * @param verificationTokenDTO the entity to save.
     * @return the persisted entity.
     */
    VerificationTokenDTO save(VerificationTokenDTO verificationTokenDTO);

    /**
     * Updates a verificationToken.
     *
     * @param verificationTokenDTO the entity to update.
     * @return the persisted entity.
     */
    VerificationTokenDTO update(VerificationTokenDTO verificationTokenDTO);

    /**
     * Partially updates a verificationToken.
     *
     * @param verificationTokenDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<VerificationTokenDTO> partialUpdate(VerificationTokenDTO verificationTokenDTO);

    /**
     * Get all the verificationTokens.
     *
     * @return the list of entities.
     */
    List<VerificationTokenDTO> findAll();

    /**
     * Get the "id" verificationToken.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<VerificationTokenDTO> findOne(Long id);

    /**
     * Delete the "id" verificationToken.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);

    VerificationToken createVerificationToken(DepoOwnerAccount depoOwnerAccount, VerificationType type);
}
