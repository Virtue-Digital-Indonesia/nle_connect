package com.nle.service;

import com.nle.service.dto.DepoOwnerAccountDTO;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link com.nle.domain.DepoOwnerAccount}.
 */
public interface DepoOwnerAccountService {
    /**
     * Save a depoOwnerAccount.
     *
     * @param depoOwnerAccountDTO the entity to save.
     * @return the persisted entity.
     */
    DepoOwnerAccountDTO save(DepoOwnerAccountDTO depoOwnerAccountDTO);

    /**
     * Updates a depoOwnerAccount.
     *
     * @param depoOwnerAccountDTO the entity to update.
     * @return the persisted entity.
     */
    DepoOwnerAccountDTO update(DepoOwnerAccountDTO depoOwnerAccountDTO);

    /**
     * Partially updates a depoOwnerAccount.
     *
     * @param depoOwnerAccountDTO the entity to update partially.
     * @return the persisted entity.
     */
    Optional<DepoOwnerAccountDTO> partialUpdate(DepoOwnerAccountDTO depoOwnerAccountDTO);

    /**
     * Get all the depoOwnerAccounts.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<DepoOwnerAccountDTO> findAll(Pageable pageable);

    /**
     * Get the "id" depoOwnerAccount.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<DepoOwnerAccountDTO> findOne(Long id);

    /**
     * Delete the "id" depoOwnerAccount.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
