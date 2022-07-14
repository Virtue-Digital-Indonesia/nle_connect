package com.nle.web.rest;

import com.nle.repository.DepoOwnerAccountRepository;
import com.nle.service.DepoOwnerAccountQueryService;
import com.nle.service.DepoOwnerAccountService;
import com.nle.service.criteria.DepoOwnerAccountCriteria;
import com.nle.service.dto.DepoOwnerAccountDTO;
import com.nle.web.rest.errors.BadRequestAlertException;
import com.nle.web.rest.vm.DepoOwnerAccountCreateDTO;
import io.swagger.v3.oas.annotations.Operation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * REST controller for managing {@link com.nle.domain.DepoOwnerAccount}.
 */
@RestController
@RequestMapping("/api")
public class DepoOwnerAccountResource {

    private final Logger log = LoggerFactory.getLogger(DepoOwnerAccountResource.class);

    private static final String ENTITY_NAME = "depoOwnerAccount";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final DepoOwnerAccountService depoOwnerAccountService;

    private final DepoOwnerAccountRepository depoOwnerAccountRepository;

    private final DepoOwnerAccountQueryService depoOwnerAccountQueryService;

    public DepoOwnerAccountResource(
        DepoOwnerAccountService depoOwnerAccountService,
        DepoOwnerAccountRepository depoOwnerAccountRepository,
        DepoOwnerAccountQueryService depoOwnerAccountQueryService
    ) {
        this.depoOwnerAccountService = depoOwnerAccountService;
        this.depoOwnerAccountRepository = depoOwnerAccountRepository;
        this.depoOwnerAccountQueryService = depoOwnerAccountQueryService;
    }

    /**
     * {@code POST  /depo-owner-accounts} : Create a new depoOwnerAccount.
     *
     * @param depoOwnerAccountDTO the depoOwnerAccountDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new depoOwnerAccountDTO, or with status {@code 400 (Bad Request)} if the depoOwnerAccount has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Operation(description = "Register new Depo owner account", operationId = "createDepoOwnerAccount", summary = "Register new Depo owner account")
    @PostMapping("/depo-owner-accounts")
    public ResponseEntity<DepoOwnerAccountDTO> createDepoOwnerAccount(@Valid @RequestBody DepoOwnerAccountCreateDTO depoOwnerAccountDTO)
        throws URISyntaxException {
        log.debug("REST request to save DepoOwnerAccount : {}", depoOwnerAccountDTO);
        DepoOwnerAccountDTO ownerAccountDTO = new DepoOwnerAccountDTO();
        BeanUtils.copyProperties(depoOwnerAccountDTO, ownerAccountDTO);
        DepoOwnerAccountDTO result = depoOwnerAccountService.save(ownerAccountDTO);
        return ResponseEntity
            .created(new URI("/api/depo-owner-accounts/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /depo-owner-accounts/:id} : Updates an existing depoOwnerAccount.
     *
     * @param id the id of the depoOwnerAccountDTO to save.
     * @param depoOwnerAccountDTO the depoOwnerAccountDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated depoOwnerAccountDTO,
     * or with status {@code 400 (Bad Request)} if the depoOwnerAccountDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the depoOwnerAccountDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Operation(description = "Update Depo owner account", operationId = "updateDepoOwnerAccount", summary = "Update Depo owner account")
    @PutMapping("/depo-owner-accounts/{id}")
    public ResponseEntity<DepoOwnerAccountDTO> updateDepoOwnerAccount(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody DepoOwnerAccountDTO depoOwnerAccountDTO
    ) throws URISyntaxException {
        log.debug("REST request to update DepoOwnerAccount : {}, {}", id, depoOwnerAccountDTO);
        if (depoOwnerAccountDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "id null");
        }
        if (!Objects.equals(id, depoOwnerAccountDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "id invalid");
        }

        if (!depoOwnerAccountRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        DepoOwnerAccountDTO result = depoOwnerAccountService.update(depoOwnerAccountDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, depoOwnerAccountDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /depo-owner-accounts} : get all the depoOwnerAccounts.
     *
     * @param pageable the pagination information.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of depoOwnerAccounts in body.
     */
    @Operation(description = "Get list of Depo owner account", operationId = "getAllDepoOwnerAccounts", summary = "Get list of Depo owner account")
    @GetMapping("/depo-owner-accounts")
    public ResponseEntity<List<DepoOwnerAccountDTO>> getAllDepoOwnerAccounts(
        DepoOwnerAccountCriteria criteria,
        @org.springdoc.api.annotations.ParameterObject Pageable pageable
    ) {
        log.debug("REST request to get DepoOwnerAccounts by criteria: {}", criteria);
        Page<DepoOwnerAccountDTO> page = depoOwnerAccountQueryService.findByCriteria(criteria, pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /depo-owner-accounts/:id} : get the "id" depoOwnerAccount.
     *
     * @param id the id of the depoOwnerAccountDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the depoOwnerAccountDTO, or with status {@code 404 (Not Found)}.
     */
    @Operation(description = "Get Depo owner account by Id", operationId = "getDepoOwnerAccount", summary = "Get Depo owner account by Id")
    @GetMapping("/depo-owner-accounts/{id}")
    public ResponseEntity<DepoOwnerAccountDTO> getDepoOwnerAccount(@PathVariable Long id) {
        log.debug("REST request to get DepoOwnerAccount : {}", id);
        Optional<DepoOwnerAccountDTO> depoOwnerAccountDTO = depoOwnerAccountService.findOne(id);
        return ResponseUtil.wrapOrNotFound(depoOwnerAccountDTO);
    }

    /**
     * {@code DELETE  /depo-owner-accounts/:id} : delete the "id" depoOwnerAccount.
     *
     * @param id the id of the depoOwnerAccountDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @Operation(description = "Delete Depo owner account by Id", operationId = "getDepoOwnerAccount", summary = "Delete Depo owner account by Id")
    @DeleteMapping("/depo-owner-accounts/{id}")
    public ResponseEntity<Void> deleteDepoOwnerAccount(@PathVariable Long id) {
        log.debug("REST request to delete DepoOwnerAccount : {}", id);
        depoOwnerAccountService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
