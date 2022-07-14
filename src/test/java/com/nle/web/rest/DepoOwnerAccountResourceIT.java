package com.nle.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.nle.IntegrationTest;
import com.nle.domain.DepoOwnerAccount;
import com.nle.repository.DepoOwnerAccountRepository;
import com.nle.service.criteria.DepoOwnerAccountCriteria;
import com.nle.service.dto.DepoOwnerAccountDTO;
import com.nle.service.mapper.DepoOwnerAccountMapper;
import java.time.Instant;
import java.time.LocalDateTime;
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
 * Integration tests for the {@link DepoOwnerAccountResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class DepoOwnerAccountResourceIT {

    private static final String DEFAULT_COMPANY_EMAIL = "5vc3b8@ikiUQp";
    private static final String UPDATED_COMPANY_EMAIL = "xezItPRYJec@SmylTWwuRnY'mzaEA0VfOGEsgqGrhSm";

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final String DEFAULT_PASSWORD = "AAAAAAAAAA";
    private static final String UPDATED_PASSWORD = "BBBBBBBBBB";

    private static final String DEFAULT_FULL_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FULL_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_ORGANIZATION_NAME = "AAAAAAAAAA";
    private static final String UPDATED_ORGANIZATION_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final LocalDateTime DEFAULT_CREATED_DATE = LocalDateTime.now();
    private static final LocalDateTime UPDATED_CREATED_DATE = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final LocalDateTime DEFAULT_LAST_MODIFIED_DATE = LocalDateTime.now();
    private static final LocalDateTime UPDATED_LAST_MODIFIED_DATE = LocalDateTime.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/depo-owner-accounts";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DepoOwnerAccountRepository depoOwnerAccountRepository;

    @Autowired
    private DepoOwnerAccountMapper depoOwnerAccountMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restDepoOwnerAccountMockMvc;

    private DepoOwnerAccount depoOwnerAccount;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DepoOwnerAccount createEntity(EntityManager em) {
        DepoOwnerAccount depoOwnerAccount = new DepoOwnerAccount()
            .companyEmail(DEFAULT_COMPANY_EMAIL)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .password(DEFAULT_PASSWORD)
            .fullName(DEFAULT_FULL_NAME)
            .organizationName(DEFAULT_ORGANIZATION_NAME)
            .createdBy(DEFAULT_CREATED_BY)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE);
        return depoOwnerAccount;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static DepoOwnerAccount createUpdatedEntity(EntityManager em) {
        DepoOwnerAccount depoOwnerAccount = new DepoOwnerAccount()
            .companyEmail(UPDATED_COMPANY_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .password(UPDATED_PASSWORD)
            .fullName(UPDATED_FULL_NAME)
            .organizationName(UPDATED_ORGANIZATION_NAME)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        return depoOwnerAccount;
    }

    @BeforeEach
    public void initTest() {
        depoOwnerAccount = createEntity(em);
    }

    @Test
    @Transactional
    void createDepoOwnerAccount() throws Exception {
        int databaseSizeBeforeCreate = depoOwnerAccountRepository.findAll().size();
        // Create the DepoOwnerAccount
        DepoOwnerAccountDTO depoOwnerAccountDTO = depoOwnerAccountMapper.toDto(depoOwnerAccount);
        restDepoOwnerAccountMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(depoOwnerAccountDTO))
            )
            .andExpect(status().isCreated());

        // Validate the DepoOwnerAccount in the database
        List<DepoOwnerAccount> depoOwnerAccountList = depoOwnerAccountRepository.findAll();
        assertThat(depoOwnerAccountList).hasSize(databaseSizeBeforeCreate + 1);
        DepoOwnerAccount testDepoOwnerAccount = depoOwnerAccountList.get(depoOwnerAccountList.size() - 1);
        assertThat(testDepoOwnerAccount.getCompanyEmail()).isEqualTo(DEFAULT_COMPANY_EMAIL);
        assertThat(testDepoOwnerAccount.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testDepoOwnerAccount.getPassword()).isEqualTo(DEFAULT_PASSWORD);
        assertThat(testDepoOwnerAccount.getFullName()).isEqualTo(DEFAULT_FULL_NAME);
        assertThat(testDepoOwnerAccount.getOrganizationName()).isEqualTo(DEFAULT_ORGANIZATION_NAME);
        assertThat(testDepoOwnerAccount.getCreatedBy()).isEqualTo(DEFAULT_CREATED_BY);
        assertThat(testDepoOwnerAccount.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testDepoOwnerAccount.getLastModifiedBy()).isEqualTo(DEFAULT_LAST_MODIFIED_BY);
        assertThat(testDepoOwnerAccount.getLastModifiedDate()).isEqualTo(DEFAULT_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void createDepoOwnerAccountWithExistingId() throws Exception {
        // Create the DepoOwnerAccount with an existing ID
        depoOwnerAccount.setId(1L);
        DepoOwnerAccountDTO depoOwnerAccountDTO = depoOwnerAccountMapper.toDto(depoOwnerAccount);

        int databaseSizeBeforeCreate = depoOwnerAccountRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restDepoOwnerAccountMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(depoOwnerAccountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DepoOwnerAccount in the database
        List<DepoOwnerAccount> depoOwnerAccountList = depoOwnerAccountRepository.findAll();
        assertThat(depoOwnerAccountList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkCompanyEmailIsRequired() throws Exception {
        int databaseSizeBeforeTest = depoOwnerAccountRepository.findAll().size();
        // set the field null
        depoOwnerAccount.setCompanyEmail(null);

        // Create the DepoOwnerAccount, which fails.
        DepoOwnerAccountDTO depoOwnerAccountDTO = depoOwnerAccountMapper.toDto(depoOwnerAccount);

        restDepoOwnerAccountMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(depoOwnerAccountDTO))
            )
            .andExpect(status().isBadRequest());

        List<DepoOwnerAccount> depoOwnerAccountList = depoOwnerAccountRepository.findAll();
        assertThat(depoOwnerAccountList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPhoneNumberIsRequired() throws Exception {
        int databaseSizeBeforeTest = depoOwnerAccountRepository.findAll().size();
        // set the field null
        depoOwnerAccount.setPhoneNumber(null);

        // Create the DepoOwnerAccount, which fails.
        DepoOwnerAccountDTO depoOwnerAccountDTO = depoOwnerAccountMapper.toDto(depoOwnerAccount);

        restDepoOwnerAccountMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(depoOwnerAccountDTO))
            )
            .andExpect(status().isBadRequest());

        List<DepoOwnerAccount> depoOwnerAccountList = depoOwnerAccountRepository.findAll();
        assertThat(depoOwnerAccountList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkPasswordIsRequired() throws Exception {
        int databaseSizeBeforeTest = depoOwnerAccountRepository.findAll().size();
        // set the field null
        depoOwnerAccount.setPassword(null);

        // Create the DepoOwnerAccount, which fails.
        DepoOwnerAccountDTO depoOwnerAccountDTO = depoOwnerAccountMapper.toDto(depoOwnerAccount);

        restDepoOwnerAccountMockMvc
            .perform(
                post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(depoOwnerAccountDTO))
            )
            .andExpect(status().isBadRequest());

        List<DepoOwnerAccount> depoOwnerAccountList = depoOwnerAccountRepository.findAll();
        assertThat(depoOwnerAccountList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccounts() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList
        restDepoOwnerAccountMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(depoOwnerAccount.getId().intValue())))
            .andExpect(jsonPath("$.[*].companyEmail").value(hasItem(DEFAULT_COMPANY_EMAIL)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].organizationName").value(hasItem(DEFAULT_ORGANIZATION_NAME)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));
    }

    @Test
    @Transactional
    void getDepoOwnerAccount() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get the depoOwnerAccount
        restDepoOwnerAccountMockMvc
            .perform(get(ENTITY_API_URL_ID, depoOwnerAccount.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(depoOwnerAccount.getId().intValue()))
            .andExpect(jsonPath("$.companyEmail").value(DEFAULT_COMPANY_EMAIL))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.password").value(DEFAULT_PASSWORD))
            .andExpect(jsonPath("$.fullName").value(DEFAULT_FULL_NAME))
            .andExpect(jsonPath("$.organizationName").value(DEFAULT_ORGANIZATION_NAME))
            .andExpect(jsonPath("$.createdBy").value(DEFAULT_CREATED_BY))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastModifiedBy").value(DEFAULT_LAST_MODIFIED_BY))
            .andExpect(jsonPath("$.lastModifiedDate").value(DEFAULT_LAST_MODIFIED_DATE.toString()));
    }

    @Test
    @Transactional
    void getDepoOwnerAccountsByIdFiltering() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        Long id = depoOwnerAccount.getId();

        defaultDepoOwnerAccountShouldBeFound("id.equals=" + id);
        defaultDepoOwnerAccountShouldNotBeFound("id.notEquals=" + id);

        defaultDepoOwnerAccountShouldBeFound("id.greaterThanOrEqual=" + id);
        defaultDepoOwnerAccountShouldNotBeFound("id.greaterThan=" + id);

        defaultDepoOwnerAccountShouldBeFound("id.lessThanOrEqual=" + id);
        defaultDepoOwnerAccountShouldNotBeFound("id.lessThan=" + id);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByCompanyEmailIsEqualToSomething() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where companyEmail equals to DEFAULT_COMPANY_EMAIL
        defaultDepoOwnerAccountShouldBeFound("companyEmail.equals=" + DEFAULT_COMPANY_EMAIL);

        // Get all the depoOwnerAccountList where companyEmail equals to UPDATED_COMPANY_EMAIL
        defaultDepoOwnerAccountShouldNotBeFound("companyEmail.equals=" + UPDATED_COMPANY_EMAIL);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByCompanyEmailIsNotEqualToSomething() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where companyEmail not equals to DEFAULT_COMPANY_EMAIL
        defaultDepoOwnerAccountShouldNotBeFound("companyEmail.notEquals=" + DEFAULT_COMPANY_EMAIL);

        // Get all the depoOwnerAccountList where companyEmail not equals to UPDATED_COMPANY_EMAIL
        defaultDepoOwnerAccountShouldBeFound("companyEmail.notEquals=" + UPDATED_COMPANY_EMAIL);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByCompanyEmailIsInShouldWork() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where companyEmail in DEFAULT_COMPANY_EMAIL or UPDATED_COMPANY_EMAIL
        defaultDepoOwnerAccountShouldBeFound("companyEmail.in=" + DEFAULT_COMPANY_EMAIL + "," + UPDATED_COMPANY_EMAIL);

        // Get all the depoOwnerAccountList where companyEmail equals to UPDATED_COMPANY_EMAIL
        defaultDepoOwnerAccountShouldNotBeFound("companyEmail.in=" + UPDATED_COMPANY_EMAIL);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByCompanyEmailIsNullOrNotNull() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where companyEmail is not null
        defaultDepoOwnerAccountShouldBeFound("companyEmail.specified=true");

        // Get all the depoOwnerAccountList where companyEmail is null
        defaultDepoOwnerAccountShouldNotBeFound("companyEmail.specified=false");
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByCompanyEmailContainsSomething() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where companyEmail contains DEFAULT_COMPANY_EMAIL
        defaultDepoOwnerAccountShouldBeFound("companyEmail.contains=" + DEFAULT_COMPANY_EMAIL);

        // Get all the depoOwnerAccountList where companyEmail contains UPDATED_COMPANY_EMAIL
        defaultDepoOwnerAccountShouldNotBeFound("companyEmail.contains=" + UPDATED_COMPANY_EMAIL);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByCompanyEmailNotContainsSomething() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where companyEmail does not contain DEFAULT_COMPANY_EMAIL
        defaultDepoOwnerAccountShouldNotBeFound("companyEmail.doesNotContain=" + DEFAULT_COMPANY_EMAIL);

        // Get all the depoOwnerAccountList where companyEmail does not contain UPDATED_COMPANY_EMAIL
        defaultDepoOwnerAccountShouldBeFound("companyEmail.doesNotContain=" + UPDATED_COMPANY_EMAIL);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByPhoneNumberIsEqualToSomething() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where phoneNumber equals to DEFAULT_PHONE_NUMBER
        defaultDepoOwnerAccountShouldBeFound("phoneNumber.equals=" + DEFAULT_PHONE_NUMBER);

        // Get all the depoOwnerAccountList where phoneNumber equals to UPDATED_PHONE_NUMBER
        defaultDepoOwnerAccountShouldNotBeFound("phoneNumber.equals=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByPhoneNumberIsNotEqualToSomething() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where phoneNumber not equals to DEFAULT_PHONE_NUMBER
        defaultDepoOwnerAccountShouldNotBeFound("phoneNumber.notEquals=" + DEFAULT_PHONE_NUMBER);

        // Get all the depoOwnerAccountList where phoneNumber not equals to UPDATED_PHONE_NUMBER
        defaultDepoOwnerAccountShouldBeFound("phoneNumber.notEquals=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByPhoneNumberIsInShouldWork() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where phoneNumber in DEFAULT_PHONE_NUMBER or UPDATED_PHONE_NUMBER
        defaultDepoOwnerAccountShouldBeFound("phoneNumber.in=" + DEFAULT_PHONE_NUMBER + "," + UPDATED_PHONE_NUMBER);

        // Get all the depoOwnerAccountList where phoneNumber equals to UPDATED_PHONE_NUMBER
        defaultDepoOwnerAccountShouldNotBeFound("phoneNumber.in=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByPhoneNumberIsNullOrNotNull() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where phoneNumber is not null
        defaultDepoOwnerAccountShouldBeFound("phoneNumber.specified=true");

        // Get all the depoOwnerAccountList where phoneNumber is null
        defaultDepoOwnerAccountShouldNotBeFound("phoneNumber.specified=false");
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByPhoneNumberContainsSomething() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where phoneNumber contains DEFAULT_PHONE_NUMBER
        defaultDepoOwnerAccountShouldBeFound("phoneNumber.contains=" + DEFAULT_PHONE_NUMBER);

        // Get all the depoOwnerAccountList where phoneNumber contains UPDATED_PHONE_NUMBER
        defaultDepoOwnerAccountShouldNotBeFound("phoneNumber.contains=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByPhoneNumberNotContainsSomething() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where phoneNumber does not contain DEFAULT_PHONE_NUMBER
        defaultDepoOwnerAccountShouldNotBeFound("phoneNumber.doesNotContain=" + DEFAULT_PHONE_NUMBER);

        // Get all the depoOwnerAccountList where phoneNumber does not contain UPDATED_PHONE_NUMBER
        defaultDepoOwnerAccountShouldBeFound("phoneNumber.doesNotContain=" + UPDATED_PHONE_NUMBER);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByPasswordIsEqualToSomething() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where password equals to DEFAULT_PASSWORD
        defaultDepoOwnerAccountShouldBeFound("password.equals=" + DEFAULT_PASSWORD);

        // Get all the depoOwnerAccountList where password equals to UPDATED_PASSWORD
        defaultDepoOwnerAccountShouldNotBeFound("password.equals=" + UPDATED_PASSWORD);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByPasswordIsNotEqualToSomething() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where password not equals to DEFAULT_PASSWORD
        defaultDepoOwnerAccountShouldNotBeFound("password.notEquals=" + DEFAULT_PASSWORD);

        // Get all the depoOwnerAccountList where password not equals to UPDATED_PASSWORD
        defaultDepoOwnerAccountShouldBeFound("password.notEquals=" + UPDATED_PASSWORD);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByPasswordIsInShouldWork() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where password in DEFAULT_PASSWORD or UPDATED_PASSWORD
        defaultDepoOwnerAccountShouldBeFound("password.in=" + DEFAULT_PASSWORD + "," + UPDATED_PASSWORD);

        // Get all the depoOwnerAccountList where password equals to UPDATED_PASSWORD
        defaultDepoOwnerAccountShouldNotBeFound("password.in=" + UPDATED_PASSWORD);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByPasswordIsNullOrNotNull() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where password is not null
        defaultDepoOwnerAccountShouldBeFound("password.specified=true");

        // Get all the depoOwnerAccountList where password is null
        defaultDepoOwnerAccountShouldNotBeFound("password.specified=false");
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByPasswordContainsSomething() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where password contains DEFAULT_PASSWORD
        defaultDepoOwnerAccountShouldBeFound("password.contains=" + DEFAULT_PASSWORD);

        // Get all the depoOwnerAccountList where password contains UPDATED_PASSWORD
        defaultDepoOwnerAccountShouldNotBeFound("password.contains=" + UPDATED_PASSWORD);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByPasswordNotContainsSomething() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where password does not contain DEFAULT_PASSWORD
        defaultDepoOwnerAccountShouldNotBeFound("password.doesNotContain=" + DEFAULT_PASSWORD);

        // Get all the depoOwnerAccountList where password does not contain UPDATED_PASSWORD
        defaultDepoOwnerAccountShouldBeFound("password.doesNotContain=" + UPDATED_PASSWORD);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByFullNameIsEqualToSomething() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where fullName equals to DEFAULT_FULL_NAME
        defaultDepoOwnerAccountShouldBeFound("fullName.equals=" + DEFAULT_FULL_NAME);

        // Get all the depoOwnerAccountList where fullName equals to UPDATED_FULL_NAME
        defaultDepoOwnerAccountShouldNotBeFound("fullName.equals=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByFullNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where fullName not equals to DEFAULT_FULL_NAME
        defaultDepoOwnerAccountShouldNotBeFound("fullName.notEquals=" + DEFAULT_FULL_NAME);

        // Get all the depoOwnerAccountList where fullName not equals to UPDATED_FULL_NAME
        defaultDepoOwnerAccountShouldBeFound("fullName.notEquals=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByFullNameIsInShouldWork() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where fullName in DEFAULT_FULL_NAME or UPDATED_FULL_NAME
        defaultDepoOwnerAccountShouldBeFound("fullName.in=" + DEFAULT_FULL_NAME + "," + UPDATED_FULL_NAME);

        // Get all the depoOwnerAccountList where fullName equals to UPDATED_FULL_NAME
        defaultDepoOwnerAccountShouldNotBeFound("fullName.in=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByFullNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where fullName is not null
        defaultDepoOwnerAccountShouldBeFound("fullName.specified=true");

        // Get all the depoOwnerAccountList where fullName is null
        defaultDepoOwnerAccountShouldNotBeFound("fullName.specified=false");
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByFullNameContainsSomething() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where fullName contains DEFAULT_FULL_NAME
        defaultDepoOwnerAccountShouldBeFound("fullName.contains=" + DEFAULT_FULL_NAME);

        // Get all the depoOwnerAccountList where fullName contains UPDATED_FULL_NAME
        defaultDepoOwnerAccountShouldNotBeFound("fullName.contains=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByFullNameNotContainsSomething() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where fullName does not contain DEFAULT_FULL_NAME
        defaultDepoOwnerAccountShouldNotBeFound("fullName.doesNotContain=" + DEFAULT_FULL_NAME);

        // Get all the depoOwnerAccountList where fullName does not contain UPDATED_FULL_NAME
        defaultDepoOwnerAccountShouldBeFound("fullName.doesNotContain=" + UPDATED_FULL_NAME);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByOrganizationNameIsEqualToSomething() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where organizationName equals to DEFAULT_ORGANIZATION_NAME
        defaultDepoOwnerAccountShouldBeFound("organizationName.equals=" + DEFAULT_ORGANIZATION_NAME);

        // Get all the depoOwnerAccountList where organizationName equals to UPDATED_ORGANIZATION_NAME
        defaultDepoOwnerAccountShouldNotBeFound("organizationName.equals=" + UPDATED_ORGANIZATION_NAME);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByOrganizationNameIsNotEqualToSomething() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where organizationName not equals to DEFAULT_ORGANIZATION_NAME
        defaultDepoOwnerAccountShouldNotBeFound("organizationName.notEquals=" + DEFAULT_ORGANIZATION_NAME);

        // Get all the depoOwnerAccountList where organizationName not equals to UPDATED_ORGANIZATION_NAME
        defaultDepoOwnerAccountShouldBeFound("organizationName.notEquals=" + UPDATED_ORGANIZATION_NAME);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByOrganizationNameIsInShouldWork() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where organizationName in DEFAULT_ORGANIZATION_NAME or UPDATED_ORGANIZATION_NAME
        defaultDepoOwnerAccountShouldBeFound("organizationName.in=" + DEFAULT_ORGANIZATION_NAME + "," + UPDATED_ORGANIZATION_NAME);

        // Get all the depoOwnerAccountList where organizationName equals to UPDATED_ORGANIZATION_NAME
        defaultDepoOwnerAccountShouldNotBeFound("organizationName.in=" + UPDATED_ORGANIZATION_NAME);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByOrganizationNameIsNullOrNotNull() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where organizationName is not null
        defaultDepoOwnerAccountShouldBeFound("organizationName.specified=true");

        // Get all the depoOwnerAccountList where organizationName is null
        defaultDepoOwnerAccountShouldNotBeFound("organizationName.specified=false");
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByOrganizationNameContainsSomething() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where organizationName contains DEFAULT_ORGANIZATION_NAME
        defaultDepoOwnerAccountShouldBeFound("organizationName.contains=" + DEFAULT_ORGANIZATION_NAME);

        // Get all the depoOwnerAccountList where organizationName contains UPDATED_ORGANIZATION_NAME
        defaultDepoOwnerAccountShouldNotBeFound("organizationName.contains=" + UPDATED_ORGANIZATION_NAME);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByOrganizationNameNotContainsSomething() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where organizationName does not contain DEFAULT_ORGANIZATION_NAME
        defaultDepoOwnerAccountShouldNotBeFound("organizationName.doesNotContain=" + DEFAULT_ORGANIZATION_NAME);

        // Get all the depoOwnerAccountList where organizationName does not contain UPDATED_ORGANIZATION_NAME
        defaultDepoOwnerAccountShouldBeFound("organizationName.doesNotContain=" + UPDATED_ORGANIZATION_NAME);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByCreatedByIsEqualToSomething() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where createdBy equals to DEFAULT_CREATED_BY
        defaultDepoOwnerAccountShouldBeFound("createdBy.equals=" + DEFAULT_CREATED_BY);

        // Get all the depoOwnerAccountList where createdBy equals to UPDATED_CREATED_BY
        defaultDepoOwnerAccountShouldNotBeFound("createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByCreatedByIsNotEqualToSomething() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where createdBy not equals to DEFAULT_CREATED_BY
        defaultDepoOwnerAccountShouldNotBeFound("createdBy.notEquals=" + DEFAULT_CREATED_BY);

        // Get all the depoOwnerAccountList where createdBy not equals to UPDATED_CREATED_BY
        defaultDepoOwnerAccountShouldBeFound("createdBy.notEquals=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByCreatedByIsInShouldWork() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where createdBy in DEFAULT_CREATED_BY or UPDATED_CREATED_BY
        defaultDepoOwnerAccountShouldBeFound("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY);

        // Get all the depoOwnerAccountList where createdBy equals to UPDATED_CREATED_BY
        defaultDepoOwnerAccountShouldNotBeFound("createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByCreatedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where createdBy is not null
        defaultDepoOwnerAccountShouldBeFound("createdBy.specified=true");

        // Get all the depoOwnerAccountList where createdBy is null
        defaultDepoOwnerAccountShouldNotBeFound("createdBy.specified=false");
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByCreatedByContainsSomething() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where createdBy contains DEFAULT_CREATED_BY
        defaultDepoOwnerAccountShouldBeFound("createdBy.contains=" + DEFAULT_CREATED_BY);

        // Get all the depoOwnerAccountList where createdBy contains UPDATED_CREATED_BY
        defaultDepoOwnerAccountShouldNotBeFound("createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByCreatedByNotContainsSomething() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where createdBy does not contain DEFAULT_CREATED_BY
        defaultDepoOwnerAccountShouldNotBeFound("createdBy.doesNotContain=" + DEFAULT_CREATED_BY);

        // Get all the depoOwnerAccountList where createdBy does not contain UPDATED_CREATED_BY
        defaultDepoOwnerAccountShouldBeFound("createdBy.doesNotContain=" + UPDATED_CREATED_BY);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByCreatedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where createdDate equals to DEFAULT_CREATED_DATE
        defaultDepoOwnerAccountShouldBeFound("createdDate.equals=" + DEFAULT_CREATED_DATE);

        // Get all the depoOwnerAccountList where createdDate equals to UPDATED_CREATED_DATE
        defaultDepoOwnerAccountShouldNotBeFound("createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByCreatedDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where createdDate not equals to DEFAULT_CREATED_DATE
        defaultDepoOwnerAccountShouldNotBeFound("createdDate.notEquals=" + DEFAULT_CREATED_DATE);

        // Get all the depoOwnerAccountList where createdDate not equals to UPDATED_CREATED_DATE
        defaultDepoOwnerAccountShouldBeFound("createdDate.notEquals=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByCreatedDateIsInShouldWork() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where createdDate in DEFAULT_CREATED_DATE or UPDATED_CREATED_DATE
        defaultDepoOwnerAccountShouldBeFound("createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE);

        // Get all the depoOwnerAccountList where createdDate equals to UPDATED_CREATED_DATE
        defaultDepoOwnerAccountShouldNotBeFound("createdDate.in=" + UPDATED_CREATED_DATE);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByCreatedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where createdDate is not null
        defaultDepoOwnerAccountShouldBeFound("createdDate.specified=true");

        // Get all the depoOwnerAccountList where createdDate is null
        defaultDepoOwnerAccountShouldNotBeFound("createdDate.specified=false");
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByLastModifiedByIsEqualToSomething() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where lastModifiedBy equals to DEFAULT_LAST_MODIFIED_BY
        defaultDepoOwnerAccountShouldBeFound("lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the depoOwnerAccountList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultDepoOwnerAccountShouldNotBeFound("lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByLastModifiedByIsNotEqualToSomething() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where lastModifiedBy not equals to DEFAULT_LAST_MODIFIED_BY
        defaultDepoOwnerAccountShouldNotBeFound("lastModifiedBy.notEquals=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the depoOwnerAccountList where lastModifiedBy not equals to UPDATED_LAST_MODIFIED_BY
        defaultDepoOwnerAccountShouldBeFound("lastModifiedBy.notEquals=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByLastModifiedByIsInShouldWork() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where lastModifiedBy in DEFAULT_LAST_MODIFIED_BY or UPDATED_LAST_MODIFIED_BY
        defaultDepoOwnerAccountShouldBeFound("lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY);

        // Get all the depoOwnerAccountList where lastModifiedBy equals to UPDATED_LAST_MODIFIED_BY
        defaultDepoOwnerAccountShouldNotBeFound("lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByLastModifiedByIsNullOrNotNull() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where lastModifiedBy is not null
        defaultDepoOwnerAccountShouldBeFound("lastModifiedBy.specified=true");

        // Get all the depoOwnerAccountList where lastModifiedBy is null
        defaultDepoOwnerAccountShouldNotBeFound("lastModifiedBy.specified=false");
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByLastModifiedByContainsSomething() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where lastModifiedBy contains DEFAULT_LAST_MODIFIED_BY
        defaultDepoOwnerAccountShouldBeFound("lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the depoOwnerAccountList where lastModifiedBy contains UPDATED_LAST_MODIFIED_BY
        defaultDepoOwnerAccountShouldNotBeFound("lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByLastModifiedByNotContainsSomething() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where lastModifiedBy does not contain DEFAULT_LAST_MODIFIED_BY
        defaultDepoOwnerAccountShouldNotBeFound("lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY);

        // Get all the depoOwnerAccountList where lastModifiedBy does not contain UPDATED_LAST_MODIFIED_BY
        defaultDepoOwnerAccountShouldBeFound("lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByLastModifiedDateIsEqualToSomething() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where lastModifiedDate equals to DEFAULT_LAST_MODIFIED_DATE
        defaultDepoOwnerAccountShouldBeFound("lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the depoOwnerAccountList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultDepoOwnerAccountShouldNotBeFound("lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByLastModifiedDateIsNotEqualToSomething() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where lastModifiedDate not equals to DEFAULT_LAST_MODIFIED_DATE
        defaultDepoOwnerAccountShouldNotBeFound("lastModifiedDate.notEquals=" + DEFAULT_LAST_MODIFIED_DATE);

        // Get all the depoOwnerAccountList where lastModifiedDate not equals to UPDATED_LAST_MODIFIED_DATE
        defaultDepoOwnerAccountShouldBeFound("lastModifiedDate.notEquals=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByLastModifiedDateIsInShouldWork() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where lastModifiedDate in DEFAULT_LAST_MODIFIED_DATE or UPDATED_LAST_MODIFIED_DATE
        defaultDepoOwnerAccountShouldBeFound("lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE);

        // Get all the depoOwnerAccountList where lastModifiedDate equals to UPDATED_LAST_MODIFIED_DATE
        defaultDepoOwnerAccountShouldNotBeFound("lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void getAllDepoOwnerAccountsByLastModifiedDateIsNullOrNotNull() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        // Get all the depoOwnerAccountList where lastModifiedDate is not null
        defaultDepoOwnerAccountShouldBeFound("lastModifiedDate.specified=true");

        // Get all the depoOwnerAccountList where lastModifiedDate is null
        defaultDepoOwnerAccountShouldNotBeFound("lastModifiedDate.specified=false");
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultDepoOwnerAccountShouldBeFound(String filter) throws Exception {
        restDepoOwnerAccountMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(depoOwnerAccount.getId().intValue())))
            .andExpect(jsonPath("$.[*].companyEmail").value(hasItem(DEFAULT_COMPANY_EMAIL)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].password").value(hasItem(DEFAULT_PASSWORD)))
            .andExpect(jsonPath("$.[*].fullName").value(hasItem(DEFAULT_FULL_NAME)))
            .andExpect(jsonPath("$.[*].organizationName").value(hasItem(DEFAULT_ORGANIZATION_NAME)))
            .andExpect(jsonPath("$.[*].createdBy").value(hasItem(DEFAULT_CREATED_BY)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastModifiedBy").value(hasItem(DEFAULT_LAST_MODIFIED_BY)))
            .andExpect(jsonPath("$.[*].lastModifiedDate").value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString())));

        // Check, that the count call also returns 1
        restDepoOwnerAccountMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("1"));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultDepoOwnerAccountShouldNotBeFound(String filter) throws Exception {
        restDepoOwnerAccountMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$").isEmpty());

        // Check, that the count call also returns 0
        restDepoOwnerAccountMockMvc
            .perform(get(ENTITY_API_URL + "/count?sort=id,desc&" + filter))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string("0"));
    }

    @Test
    @Transactional
    void getNonExistingDepoOwnerAccount() throws Exception {
        // Get the depoOwnerAccount
        restDepoOwnerAccountMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewDepoOwnerAccount() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        int databaseSizeBeforeUpdate = depoOwnerAccountRepository.findAll().size();

        // Update the depoOwnerAccount
        DepoOwnerAccount updatedDepoOwnerAccount = depoOwnerAccountRepository.findById(depoOwnerAccount.getId()).get();
        // Disconnect from session so that the updates on updatedDepoOwnerAccount are not directly saved in db
        em.detach(updatedDepoOwnerAccount);
        updatedDepoOwnerAccount
            .companyEmail(UPDATED_COMPANY_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .password(UPDATED_PASSWORD)
            .fullName(UPDATED_FULL_NAME)
            .organizationName(UPDATED_ORGANIZATION_NAME)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);
        DepoOwnerAccountDTO depoOwnerAccountDTO = depoOwnerAccountMapper.toDto(updatedDepoOwnerAccount);

        restDepoOwnerAccountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, depoOwnerAccountDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(depoOwnerAccountDTO))
            )
            .andExpect(status().isOk());

        // Validate the DepoOwnerAccount in the database
        List<DepoOwnerAccount> depoOwnerAccountList = depoOwnerAccountRepository.findAll();
        assertThat(depoOwnerAccountList).hasSize(databaseSizeBeforeUpdate);
        DepoOwnerAccount testDepoOwnerAccount = depoOwnerAccountList.get(depoOwnerAccountList.size() - 1);
        assertThat(testDepoOwnerAccount.getCompanyEmail()).isEqualTo(UPDATED_COMPANY_EMAIL);
        assertThat(testDepoOwnerAccount.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testDepoOwnerAccount.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testDepoOwnerAccount.getFullName()).isEqualTo(UPDATED_FULL_NAME);
        assertThat(testDepoOwnerAccount.getOrganizationName()).isEqualTo(UPDATED_ORGANIZATION_NAME);
        assertThat(testDepoOwnerAccount.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testDepoOwnerAccount.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testDepoOwnerAccount.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testDepoOwnerAccount.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void putNonExistingDepoOwnerAccount() throws Exception {
        int databaseSizeBeforeUpdate = depoOwnerAccountRepository.findAll().size();
        depoOwnerAccount.setId(count.incrementAndGet());

        // Create the DepoOwnerAccount
        DepoOwnerAccountDTO depoOwnerAccountDTO = depoOwnerAccountMapper.toDto(depoOwnerAccount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDepoOwnerAccountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, depoOwnerAccountDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(depoOwnerAccountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DepoOwnerAccount in the database
        List<DepoOwnerAccount> depoOwnerAccountList = depoOwnerAccountRepository.findAll();
        assertThat(depoOwnerAccountList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchDepoOwnerAccount() throws Exception {
        int databaseSizeBeforeUpdate = depoOwnerAccountRepository.findAll().size();
        depoOwnerAccount.setId(count.incrementAndGet());

        // Create the DepoOwnerAccount
        DepoOwnerAccountDTO depoOwnerAccountDTO = depoOwnerAccountMapper.toDto(depoOwnerAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDepoOwnerAccountMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(depoOwnerAccountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DepoOwnerAccount in the database
        List<DepoOwnerAccount> depoOwnerAccountList = depoOwnerAccountRepository.findAll();
        assertThat(depoOwnerAccountList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamDepoOwnerAccount() throws Exception {
        int databaseSizeBeforeUpdate = depoOwnerAccountRepository.findAll().size();
        depoOwnerAccount.setId(count.incrementAndGet());

        // Create the DepoOwnerAccount
        DepoOwnerAccountDTO depoOwnerAccountDTO = depoOwnerAccountMapper.toDto(depoOwnerAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDepoOwnerAccountMockMvc
            .perform(
                put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(depoOwnerAccountDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DepoOwnerAccount in the database
        List<DepoOwnerAccount> depoOwnerAccountList = depoOwnerAccountRepository.findAll();
        assertThat(depoOwnerAccountList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateDepoOwnerAccountWithPatch() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        int databaseSizeBeforeUpdate = depoOwnerAccountRepository.findAll().size();

        // Update the depoOwnerAccount using partial update
        DepoOwnerAccount partialUpdatedDepoOwnerAccount = new DepoOwnerAccount();
        partialUpdatedDepoOwnerAccount.setId(depoOwnerAccount.getId());

        partialUpdatedDepoOwnerAccount
            .organizationName(UPDATED_ORGANIZATION_NAME)
            .createdBy(UPDATED_CREATED_BY)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);

        restDepoOwnerAccountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDepoOwnerAccount.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDepoOwnerAccount))
            )
            .andExpect(status().isOk());

        // Validate the DepoOwnerAccount in the database
        List<DepoOwnerAccount> depoOwnerAccountList = depoOwnerAccountRepository.findAll();
        assertThat(depoOwnerAccountList).hasSize(databaseSizeBeforeUpdate);
        DepoOwnerAccount testDepoOwnerAccount = depoOwnerAccountList.get(depoOwnerAccountList.size() - 1);
        assertThat(testDepoOwnerAccount.getCompanyEmail()).isEqualTo(DEFAULT_COMPANY_EMAIL);
        assertThat(testDepoOwnerAccount.getPhoneNumber()).isEqualTo(DEFAULT_PHONE_NUMBER);
        assertThat(testDepoOwnerAccount.getPassword()).isEqualTo(DEFAULT_PASSWORD);
        assertThat(testDepoOwnerAccount.getFullName()).isEqualTo(DEFAULT_FULL_NAME);
        assertThat(testDepoOwnerAccount.getOrganizationName()).isEqualTo(UPDATED_ORGANIZATION_NAME);
        assertThat(testDepoOwnerAccount.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testDepoOwnerAccount.getCreatedDate()).isEqualTo(DEFAULT_CREATED_DATE);
        assertThat(testDepoOwnerAccount.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testDepoOwnerAccount.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void fullUpdateDepoOwnerAccountWithPatch() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        int databaseSizeBeforeUpdate = depoOwnerAccountRepository.findAll().size();

        // Update the depoOwnerAccount using partial update
        DepoOwnerAccount partialUpdatedDepoOwnerAccount = new DepoOwnerAccount();
        partialUpdatedDepoOwnerAccount.setId(depoOwnerAccount.getId());

        partialUpdatedDepoOwnerAccount
            .companyEmail(UPDATED_COMPANY_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .password(UPDATED_PASSWORD)
            .fullName(UPDATED_FULL_NAME)
            .organizationName(UPDATED_ORGANIZATION_NAME)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE);

        restDepoOwnerAccountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedDepoOwnerAccount.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedDepoOwnerAccount))
            )
            .andExpect(status().isOk());

        // Validate the DepoOwnerAccount in the database
        List<DepoOwnerAccount> depoOwnerAccountList = depoOwnerAccountRepository.findAll();
        assertThat(depoOwnerAccountList).hasSize(databaseSizeBeforeUpdate);
        DepoOwnerAccount testDepoOwnerAccount = depoOwnerAccountList.get(depoOwnerAccountList.size() - 1);
        assertThat(testDepoOwnerAccount.getCompanyEmail()).isEqualTo(UPDATED_COMPANY_EMAIL);
        assertThat(testDepoOwnerAccount.getPhoneNumber()).isEqualTo(UPDATED_PHONE_NUMBER);
        assertThat(testDepoOwnerAccount.getPassword()).isEqualTo(UPDATED_PASSWORD);
        assertThat(testDepoOwnerAccount.getFullName()).isEqualTo(UPDATED_FULL_NAME);
        assertThat(testDepoOwnerAccount.getOrganizationName()).isEqualTo(UPDATED_ORGANIZATION_NAME);
        assertThat(testDepoOwnerAccount.getCreatedBy()).isEqualTo(UPDATED_CREATED_BY);
        assertThat(testDepoOwnerAccount.getCreatedDate()).isEqualTo(UPDATED_CREATED_DATE);
        assertThat(testDepoOwnerAccount.getLastModifiedBy()).isEqualTo(UPDATED_LAST_MODIFIED_BY);
        assertThat(testDepoOwnerAccount.getLastModifiedDate()).isEqualTo(UPDATED_LAST_MODIFIED_DATE);
    }

    @Test
    @Transactional
    void patchNonExistingDepoOwnerAccount() throws Exception {
        int databaseSizeBeforeUpdate = depoOwnerAccountRepository.findAll().size();
        depoOwnerAccount.setId(count.incrementAndGet());

        // Create the DepoOwnerAccount
        DepoOwnerAccountDTO depoOwnerAccountDTO = depoOwnerAccountMapper.toDto(depoOwnerAccount);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restDepoOwnerAccountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, depoOwnerAccountDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(depoOwnerAccountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DepoOwnerAccount in the database
        List<DepoOwnerAccount> depoOwnerAccountList = depoOwnerAccountRepository.findAll();
        assertThat(depoOwnerAccountList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchDepoOwnerAccount() throws Exception {
        int databaseSizeBeforeUpdate = depoOwnerAccountRepository.findAll().size();
        depoOwnerAccount.setId(count.incrementAndGet());

        // Create the DepoOwnerAccount
        DepoOwnerAccountDTO depoOwnerAccountDTO = depoOwnerAccountMapper.toDto(depoOwnerAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDepoOwnerAccountMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(depoOwnerAccountDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the DepoOwnerAccount in the database
        List<DepoOwnerAccount> depoOwnerAccountList = depoOwnerAccountRepository.findAll();
        assertThat(depoOwnerAccountList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamDepoOwnerAccount() throws Exception {
        int databaseSizeBeforeUpdate = depoOwnerAccountRepository.findAll().size();
        depoOwnerAccount.setId(count.incrementAndGet());

        // Create the DepoOwnerAccount
        DepoOwnerAccountDTO depoOwnerAccountDTO = depoOwnerAccountMapper.toDto(depoOwnerAccount);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restDepoOwnerAccountMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(depoOwnerAccountDTO))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the DepoOwnerAccount in the database
        List<DepoOwnerAccount> depoOwnerAccountList = depoOwnerAccountRepository.findAll();
        assertThat(depoOwnerAccountList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteDepoOwnerAccount() throws Exception {
        // Initialize the database
        depoOwnerAccountRepository.saveAndFlush(depoOwnerAccount);

        int databaseSizeBeforeDelete = depoOwnerAccountRepository.findAll().size();

        // Delete the depoOwnerAccount
        restDepoOwnerAccountMockMvc
            .perform(delete(ENTITY_API_URL_ID, depoOwnerAccount.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<DepoOwnerAccount> depoOwnerAccountList = depoOwnerAccountRepository.findAll();
        assertThat(depoOwnerAccountList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
