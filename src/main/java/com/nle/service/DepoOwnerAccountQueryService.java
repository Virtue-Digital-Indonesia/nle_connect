package com.nle.service;

import com.nle.domain.*; // for static metamodels
import com.nle.domain.DepoOwnerAccount;
import com.nle.repository.DepoOwnerAccountRepository;
import com.nle.service.criteria.DepoOwnerAccountCriteria;
import com.nle.service.dto.DepoOwnerAccountDTO;
import com.nle.service.mapper.DepoOwnerAccountMapper;
import java.util.List;
import javax.persistence.criteria.JoinType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link DepoOwnerAccount} entities in the database.
 * The main input is a {@link DepoOwnerAccountCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link List} of {@link DepoOwnerAccountDTO} or a {@link Page} of {@link DepoOwnerAccountDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class DepoOwnerAccountQueryService extends QueryService<DepoOwnerAccount> {

    private final Logger log = LoggerFactory.getLogger(DepoOwnerAccountQueryService.class);

    private final DepoOwnerAccountRepository depoOwnerAccountRepository;

    private final DepoOwnerAccountMapper depoOwnerAccountMapper;

    public DepoOwnerAccountQueryService(
        DepoOwnerAccountRepository depoOwnerAccountRepository,
        DepoOwnerAccountMapper depoOwnerAccountMapper
    ) {
        this.depoOwnerAccountRepository = depoOwnerAccountRepository;
        this.depoOwnerAccountMapper = depoOwnerAccountMapper;
    }

    /**
     * Return a {@link List} of {@link DepoOwnerAccountDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public List<DepoOwnerAccountDTO> findByCriteria(DepoOwnerAccountCriteria criteria) {
        log.debug("find by criteria : {}", criteria);
        final Specification<DepoOwnerAccount> specification = createSpecification(criteria);
        return depoOwnerAccountMapper.toDto(depoOwnerAccountRepository.findAll(specification));
    }

    /**
     * Return a {@link Page} of {@link DepoOwnerAccountDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<DepoOwnerAccountDTO> findByCriteria(DepoOwnerAccountCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<DepoOwnerAccount> specification = createSpecification(criteria);
        return depoOwnerAccountRepository.findAll(specification, page).map(depoOwnerAccountMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(DepoOwnerAccountCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<DepoOwnerAccount> specification = createSpecification(criteria);
        return depoOwnerAccountRepository.count(specification);
    }

    /**
     * Function to convert {@link DepoOwnerAccountCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<DepoOwnerAccount> createSpecification(DepoOwnerAccountCriteria criteria) {
        Specification<DepoOwnerAccount> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), DepoOwnerAccount_.id));
            }
            if (criteria.getCompanyEmail() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCompanyEmail(), DepoOwnerAccount_.companyEmail));
            }
            if (criteria.getPhoneNumber() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPhoneNumber(), DepoOwnerAccount_.phoneNumber));
            }
            if (criteria.getPassword() != null) {
                specification = specification.and(buildStringSpecification(criteria.getPassword(), DepoOwnerAccount_.password));
            }
            if (criteria.getFullName() != null) {
                specification = specification.and(buildStringSpecification(criteria.getFullName(), DepoOwnerAccount_.fullName));
            }
            if (criteria.getOrganizationName() != null) {
                specification =
                    specification.and(buildStringSpecification(criteria.getOrganizationName(), DepoOwnerAccount_.organizationName));
            }
            if (criteria.getCreatedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCreatedBy(), DepoOwnerAccount_.createdBy));
            }
            if (criteria.getCreatedDate() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getCreatedDate(), DepoOwnerAccount_.createdDate));
            }
            if (criteria.getLastModifiedBy() != null) {
                specification = specification.and(buildStringSpecification(criteria.getLastModifiedBy(), DepoOwnerAccount_.lastModifiedBy));
            }
            if (criteria.getLastModifiedDate() != null) {
                specification =
                    specification.and(buildRangeSpecification(criteria.getLastModifiedDate(), DepoOwnerAccount_.lastModifiedDate));
            }
        }
        return specification;
    }
}
