package com.nle.web.rest;

import com.nle.repository.VerificationTokenRepository;
import com.nle.service.VerificationTokenService;
import com.nle.service.dto.VerificationTokenDTO;
import com.nle.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.nle.domain.VerificationToken}.
 */
@RestController
@RequestMapping("/api")
public class VerificationTokenResource {

    private final Logger log = LoggerFactory.getLogger(VerificationTokenResource.class);

    private static final String ENTITY_NAME = "verificationToken";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final VerificationTokenService verificationTokenService;

    private final VerificationTokenRepository verificationTokenRepository;

    public VerificationTokenResource(
        VerificationTokenService verificationTokenService,
        VerificationTokenRepository verificationTokenRepository
    ) {
        this.verificationTokenService = verificationTokenService;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    /**
     * {@code POST  /verification-tokens} : Create a new verificationToken.
     *
     * @param verificationTokenDTO the verificationTokenDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new verificationTokenDTO, or with status {@code 400 (Bad Request)} if the verificationToken has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/verification-tokens")
    public ResponseEntity<VerificationTokenDTO> createVerificationToken(@Valid @RequestBody VerificationTokenDTO verificationTokenDTO)
        throws URISyntaxException {
        log.debug("REST request to save VerificationToken : {}", verificationTokenDTO);
        if (verificationTokenDTO.getId() != null) {
            throw new BadRequestAlertException("A new verificationToken cannot already have an ID", ENTITY_NAME, "idexists");
        }
        VerificationTokenDTO result = verificationTokenService.save(verificationTokenDTO);
        return ResponseEntity
            .created(new URI("/api/verification-tokens/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, false, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /verification-tokens/:id} : Updates an existing verificationToken.
     *
     * @param id the id of the verificationTokenDTO to save.
     * @param verificationTokenDTO the verificationTokenDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated verificationTokenDTO,
     * or with status {@code 400 (Bad Request)} if the verificationTokenDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the verificationTokenDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/verification-tokens/{id}")
    public ResponseEntity<VerificationTokenDTO> updateVerificationToken(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody VerificationTokenDTO verificationTokenDTO
    ) throws URISyntaxException {
        log.debug("REST request to update VerificationToken : {}, {}", id, verificationTokenDTO);
        if (verificationTokenDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, verificationTokenDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!verificationTokenRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        VerificationTokenDTO result = verificationTokenService.update(verificationTokenDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, verificationTokenDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /verification-tokens/:id} : Partial updates given fields of an existing verificationToken, field will ignore if it is null
     *
     * @param id the id of the verificationTokenDTO to save.
     * @param verificationTokenDTO the verificationTokenDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated verificationTokenDTO,
     * or with status {@code 400 (Bad Request)} if the verificationTokenDTO is not valid,
     * or with status {@code 404 (Not Found)} if the verificationTokenDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the verificationTokenDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/verification-tokens/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<VerificationTokenDTO> partialUpdateVerificationToken(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody VerificationTokenDTO verificationTokenDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update VerificationToken partially : {}, {}", id, verificationTokenDTO);
        if (verificationTokenDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, verificationTokenDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!verificationTokenRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<VerificationTokenDTO> result = verificationTokenService.partialUpdate(verificationTokenDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, false, ENTITY_NAME, verificationTokenDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /verification-tokens} : get all the verificationTokens.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of verificationTokens in body.
     */
    @GetMapping("/verification-tokens")
    public List<VerificationTokenDTO> getAllVerificationTokens() {
        log.debug("REST request to get all VerificationTokens");
        return verificationTokenService.findAll();
    }

    /**
     * {@code GET  /verification-tokens/:id} : get the "id" verificationToken.
     *
     * @param id the id of the verificationTokenDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the verificationTokenDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/verification-tokens/{id}")
    public ResponseEntity<VerificationTokenDTO> getVerificationToken(@PathVariable Long id) {
        log.debug("REST request to get VerificationToken : {}", id);
        Optional<VerificationTokenDTO> verificationTokenDTO = verificationTokenService.findOne(id);
        return ResponseUtil.wrapOrNotFound(verificationTokenDTO);
    }

    /**
     * {@code DELETE  /verification-tokens/:id} : delete the "id" verificationToken.
     *
     * @param id the id of the verificationTokenDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/verification-tokens/{id}")
    public ResponseEntity<Void> deleteVerificationToken(@PathVariable Long id) {
        log.debug("REST request to delete VerificationToken : {}", id);
        verificationTokenService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, false, ENTITY_NAME, id.toString()))
            .build();
    }
}
