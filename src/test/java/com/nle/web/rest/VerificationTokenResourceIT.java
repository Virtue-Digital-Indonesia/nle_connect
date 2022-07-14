package com.nle.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.nle.IntegrationTest;
import com.nle.domain.VerificationToken;
import com.nle.repository.VerificationTokenRepository;
import com.nle.service.dto.VerificationTokenDTO;
import com.nle.service.mapper.VerificationTokenMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link VerificationTokenResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class VerificationTokenResourceIT {

    private static final String DEFAULT_TOKEN = "AAAAAAAAAA";
    private static final String UPDATED_TOKEN = "BBBBBBBBBB";

    private static final Instant DEFAULT_EXPIRY_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_EXPIRY_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_TOKEN_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TOKEN_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/verification-tokens";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    @Autowired
    private VerificationTokenMapper verificationTokenMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVerificationTokenMockMvc;

    private VerificationToken verificationToken;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VerificationToken createEntity(EntityManager em) {
        VerificationToken verificationToken = new VerificationToken()
            .token(DEFAULT_TOKEN)
            .expiryDate(DEFAULT_EXPIRY_DATE)
            .tokenType(DEFAULT_TOKEN_TYPE)
            .createdBy(DEFAULT_CREATED_BY)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE);
        return verificationToken;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VerificationToken createUpdatedEntity(EntityManager em) {
        VerificationToken verificationToken = new VerificationToken()
            .token(UPDATED_TOKEN)
            .expiryDate(UPDATED_EXPIRY_DATE)
            .tokenType(UPDATED_TOKEN_TYPE)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        return verificationToken;
    }

    @BeforeEach
    public void initTest() {
        verificationToken = createEntity(em);
    }

    @Test
    @Transactional
    void createVerificationToken() throws Exception {
        int databaseSizeBeforeCreate = verificationTokenRepository.findAll().size();
        // Create the VerificationToken
        VerificationTokenDTO verificationTokenDTO = verificationTokenMapper.toDto(verificationToken);
        restVerificationTokenMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(verificationTokenDTO))
            )
            .andExpect(status().isCreated());

        // Validate the VerificationToken in the database
        List<VerificationToken> verificationTokenList = verificationTokenRepository.findAll();
        assertThat(verificationTokenList).hasSize(databaseSizeBeforeCreate + 1);
        VerificationToken testVerificationToken = verificationTokenList.get(verificationTokenList.size() - 1);
        assertThat(testVerificationToken.getToken()).isEqualTo(DEFAULT_TOKEN);
        assertThat(testVerificationToken.getExpiryDate()).isEqualTo(DEFAULT_EXPIRY_DATE);
        assertThat(testVerificationToken.getTokenType()).isEqualTo(DEFAULT_TOKEN_TYPE);
        assertThat(testVerificationToken.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testVerificationToken.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testVerificationToken.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testVerificationToken.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void createVerificationTokenWithExistingId() throws Exception {
        // Create the VerificationToken with an existing ID
        verificationToken.setId(1L);
        VerificationTokenDTO verificationTokenDTO = verificationTokenMapper.toDto(verificationToken);

        int databaseSizeBeforeCreate = verificationTokenRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVerificationTokenMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(verificationTokenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VerificationToken in the database
        List<VerificationToken> verificationTokenList = verificationTokenRepository.findAll();
        assertThat(verificationTokenList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTokenIsRequired() throws Exception {
        int databaseSizeBeforeTest = verificationTokenRepository.findAll().size();
        // set the field null
        verificationToken.setToken(null);

        // Create the VerificationToken, which fails.
        VerificationTokenDTO verificationTokenDTO = verificationTokenMapper.toDto(verificationToken);

        restVerificationTokenMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(verificationTokenDTO))
            )
            .andExpect(status().isBadRequest());

        List<VerificationToken> verificationTokenList = verificationTokenRepository.findAll();
        assertThat(verificationTokenList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkExpiryDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = verificationTokenRepository.findAll().size();
        // set the field null
        verificationToken.setExpiryDate(null);

        // Create the VerificationToken, which fails.
        VerificationTokenDTO verificationTokenDTO = verificationTokenMapper.toDto(verificationToken);

        restVerificationTokenMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(verificationTokenDTO))
            )
            .andExpect(status().isBadRequest());

        List<VerificationToken> verificationTokenList = verificationTokenRepository.findAll();
        assertThat(verificationTokenList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkTokenTypeIsRequired() throws Exception {
        int databaseSizeBeforeTest = verificationTokenRepository.findAll().size();
        // set the field null
        verificationToken.setTokenType(null);

        // Create the VerificationToken, which fails.
        VerificationTokenDTO verificationTokenDTO = verificationTokenMapper.toDto(verificationToken);

        restVerificationTokenMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(verificationTokenDTO))
            )
            .andExpect(status().isBadRequest());

        List<VerificationToken> verificationTokenList = verificationTokenRepository.findAll();
        assertThat(verificationTokenList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllVerificationTokens() throws Exception {
        // Initialize the database
        verificationTokenRepository.saveAndFlush(verificationToken);

        // Get all the verificationTokenList
        restVerificationTokenMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(verificationToken.getId().intValue())))
            .andExpect(jsonPath("$.[*].token").value(hasItem(DEFAULT_TOKEN)))
            .andExpect(jsonPath("$.[*].expiryDate").value(hasItem(DEFAULT_EXPIRY_DATE.toString())))
            .andExpect(jsonPath("$.[*].tokenType").value(hasItem(DEFAULT_TOKEN_TYPE)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));
    }

    @Test
    @Transactional
    void getVerificationToken() throws Exception {
        // Initialize the database
        verificationTokenRepository.saveAndFlush(verificationToken);

        // Get the verificationToken
        restVerificationTokenMockMvc
            .perform(get(ENTITY_API_URL_ID, verificationToken.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(verificationToken.getId().intValue()))
            .andExpect(jsonPath("$.token").value(DEFAULT_TOKEN))
            .andExpect(jsonPath("$.expiryDate").value(DEFAULT_EXPIRY_DATE.toString()))
            .andExpect(jsonPath("$.tokenType").value(DEFAULT_TOKEN_TYPE))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingVerificationToken() throws Exception {
        // Get the verificationToken
        restVerificationTokenMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewVerificationToken() throws Exception {
        // Initialize the database
        verificationTokenRepository.saveAndFlush(verificationToken);

        int databaseSizeBeforeUpdate = verificationTokenRepository.findAll().size();

        // Update the verificationToken
        VerificationToken updatedVerificationToken = verificationTokenRepository.findById(verificationToken.getId()).get();
        // Disconnect from session so that the updates on updatedVerificationToken are not directly saved in db
        em.detach(updatedVerificationToken);
        updatedVerificationToken
            .token(UPDATED_TOKEN)
            .expiryDate(UPDATED_EXPIRY_DATE)
            .tokenType(UPDATED_TOKEN_TYPE)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        VerificationTokenDTO verificationTokenDTO = verificationTokenMapper.toDto(updatedVerificationToken);

        restVerificationTokenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, verificationTokenDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(verificationTokenDTO))
            )
            .andExpect(status().isOk());

        // Validate the VerificationToken in the database
        List<VerificationToken> verificationTokenList = verificationTokenRepository.findAll();
        assertThat(verificationTokenList).hasSize(databaseSizeBeforeUpdate);
        VerificationToken testVerificationToken = verificationTokenList.get(verificationTokenList.size() - 1);
        assertThat(testVerificationToken.getToken()).isEqualTo(UPDATED_TOKEN);
        assertThat(testVerificationToken.getExpiryDate()).isEqualTo(UPDATED_EXPIRY_DATE);
        assertThat(testVerificationToken.getTokenType()).isEqualTo(UPDATED_TOKEN_TYPE);
        assertThat(testVerificationToken.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testVerificationToken.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testVerificationToken.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testVerificationToken.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingVerificationToken() throws Exception {
        int databaseSizeBeforeUpdate = verificationTokenRepository.findAll().size();
        verificationToken.setId(count.incrementAndGet());

        // Create the VerificationToken
        VerificationTokenDTO verificationTokenDTO = verificationTokenMapper.toDto(verificationToken);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVerificationTokenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, verificationTokenDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(verificationTokenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VerificationToken in the database
        List<VerificationToken> verificationTokenList = verificationTokenRepository.findAll();
        assertThat(verificationTokenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVerificationToken() throws Exception {
        int databaseSizeBeforeUpdate = verificationTokenRepository.findAll().size();
        verificationToken.setId(count.incrementAndGet());

        // Create the VerificationToken
        VerificationTokenDTO verificationTokenDTO = verificationTokenMapper.toDto(verificationToken);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVerificationTokenMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(verificationTokenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VerificationToken in the database
        List<VerificationToken> verificationTokenList = verificationTokenRepository.findAll();
        assertThat(verificationTokenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVerificationToken() throws Exception {
        int databaseSizeBeforeUpdate = verificationTokenRepository.findAll().size();
        verificationToken.setId(count.incrementAndGet());

        // Create the VerificationToken
        VerificationTokenDTO verificationTokenDTO = verificationTokenMapper.toDto(verificationToken);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVerificationTokenMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(verificationTokenDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the VerificationToken in the database
        List<VerificationToken> verificationTokenList = verificationTokenRepository.findAll();
        assertThat(verificationTokenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVerificationTokenWithPatch() throws Exception {
        // Initialize the database
        verificationTokenRepository.saveAndFlush(verificationToken);

        int databaseSizeBeforeUpdate = verificationTokenRepository.findAll().size();

        // Update the verificationToken using partial update
        VerificationToken partialUpdatedVerificationToken = new VerificationToken();
        partialUpdatedVerificationToken.setId(verificationToken.getId());

        partialUpdatedVerificationToken
            .token(UPDATED_TOKEN)
            .expiryDate(UPDATED_EXPIRY_DATE)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);

        restVerificationTokenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVerificationToken.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVerificationToken))
            )
            .andExpect(status().isOk());

        // Validate the VerificationToken in the database
        List<VerificationToken> verificationTokenList = verificationTokenRepository.findAll();
        assertThat(verificationTokenList).hasSize(databaseSizeBeforeUpdate);
        VerificationToken testVerificationToken = verificationTokenList.get(verificationTokenList.size() - 1);
        assertThat(testVerificationToken.getToken()).isEqualTo(UPDATED_TOKEN);
        assertThat(testVerificationToken.getExpiryDate()).isEqualTo(UPDATED_EXPIRY_DATE);
        assertThat(testVerificationToken.getTokenType()).isEqualTo(DEFAULT_TOKEN_TYPE);
        assertThat(testVerificationToken.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testVerificationToken.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testVerificationToken.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testVerificationToken.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void fullUpdateVerificationTokenWithPatch() throws Exception {
        // Initialize the database
        verificationTokenRepository.saveAndFlush(verificationToken);

        int databaseSizeBeforeUpdate = verificationTokenRepository.findAll().size();

        // Update the verificationToken using partial update
        VerificationToken partialUpdatedVerificationToken = new VerificationToken();
        partialUpdatedVerificationToken.setId(verificationToken.getId());

        partialUpdatedVerificationToken
            .token(UPDATED_TOKEN)
            .expiryDate(UPDATED_EXPIRY_DATE)
            .tokenType(UPDATED_TOKEN_TYPE)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);

        restVerificationTokenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVerificationToken.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedVerificationToken))
            )
            .andExpect(status().isOk());

        // Validate the VerificationToken in the database
        List<VerificationToken> verificationTokenList = verificationTokenRepository.findAll();
        assertThat(verificationTokenList).hasSize(databaseSizeBeforeUpdate);
        VerificationToken testVerificationToken = verificationTokenList.get(verificationTokenList.size() - 1);
        assertThat(testVerificationToken.getToken()).isEqualTo(UPDATED_TOKEN);
        assertThat(testVerificationToken.getExpiryDate()).isEqualTo(UPDATED_EXPIRY_DATE);
        assertThat(testVerificationToken.getTokenType()).isEqualTo(UPDATED_TOKEN_TYPE);
        assertThat(testVerificationToken.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testVerificationToken.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testVerificationToken.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testVerificationToken.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingVerificationToken() throws Exception {
        int databaseSizeBeforeUpdate = verificationTokenRepository.findAll().size();
        verificationToken.setId(count.incrementAndGet());

        // Create the VerificationToken
        VerificationTokenDTO verificationTokenDTO = verificationTokenMapper.toDto(verificationToken);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVerificationTokenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, verificationTokenDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(verificationTokenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VerificationToken in the database
        List<VerificationToken> verificationTokenList = verificationTokenRepository.findAll();
        assertThat(verificationTokenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVerificationToken() throws Exception {
        int databaseSizeBeforeUpdate = verificationTokenRepository.findAll().size();
        verificationToken.setId(count.incrementAndGet());

        // Create the VerificationToken
        VerificationTokenDTO verificationTokenDTO = verificationTokenMapper.toDto(verificationToken);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVerificationTokenMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(verificationTokenDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VerificationToken in the database
        List<VerificationToken> verificationTokenList = verificationTokenRepository.findAll();
        assertThat(verificationTokenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVerificationToken() throws Exception {
        int databaseSizeBeforeUpdate = verificationTokenRepository.findAll().size();
        verificationToken.setId(count.incrementAndGet());

        // Create the VerificationToken
        VerificationTokenDTO verificationTokenDTO = verificationTokenMapper.toDto(verificationToken);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVerificationTokenMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(verificationTokenDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the VerificationToken in the database
        List<VerificationToken> verificationTokenList = verificationTokenRepository.findAll();
        assertThat(verificationTokenList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVerificationToken() throws Exception {
        // Initialize the database
        verificationTokenRepository.saveAndFlush(verificationToken);

        int databaseSizeBeforeDelete = verificationTokenRepository.findAll().size();

        // Delete the verificationToken
        restVerificationTokenMockMvc
            .perform(delete(ENTITY_API_URL_ID, verificationToken.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<VerificationToken> verificationTokenList = verificationTokenRepository.findAll();
        assertThat(verificationTokenList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
