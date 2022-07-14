package com.nle.service.criteria;

import org.springdoc.api.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.Filter;
import tech.jhipster.service.filter.LongFilter;
import tech.jhipster.service.filter.StringFilter;

import java.io.Serializable;
import java.util.Objects;

/**
 * Criteria class for the {@link com.nle.domain.DepoOwnerAccount} entity. This class is used
 * in {@link com.nle.web.rest.DepoOwnerAccountResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /depo-owner-accounts?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
public class DepoOwnerAccountCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter companyEmail;

    private StringFilter phoneNumber;

    private StringFilter password;

    private StringFilter fullName;

    private StringFilter organizationName;

    private StringFilter createdBy;

    private StringFilter lastModifiedBy;

    private Boolean distinct;

    public DepoOwnerAccountCriteria() {
    }

    public DepoOwnerAccountCriteria(DepoOwnerAccountCriteria other) {
        this.id = other.id == null ? null : other.id.copy();
        this.companyEmail = other.companyEmail == null ? null : other.companyEmail.copy();
        this.phoneNumber = other.phoneNumber == null ? null : other.phoneNumber.copy();
        this.password = other.password == null ? null : other.password.copy();
        this.fullName = other.fullName == null ? null : other.fullName.copy();
        this.organizationName = other.organizationName == null ? null : other.organizationName.copy();
        this.createdBy = other.createdBy == null ? null : other.createdBy.copy();
        this.lastModifiedBy = other.lastModifiedBy == null ? null : other.lastModifiedBy.copy();
        this.distinct = other.distinct;
    }

    @Override
    public DepoOwnerAccountCriteria copy() {
        return new DepoOwnerAccountCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public LongFilter id() {
        if (id == null) {
            id = new LongFilter();
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getCompanyEmail() {
        return companyEmail;
    }

    public StringFilter companyEmail() {
        if (companyEmail == null) {
            companyEmail = new StringFilter();
        }
        return companyEmail;
    }

    public void setCompanyEmail(StringFilter companyEmail) {
        this.companyEmail = companyEmail;
    }

    public StringFilter getPhoneNumber() {
        return phoneNumber;
    }

    public StringFilter phoneNumber() {
        if (phoneNumber == null) {
            phoneNumber = new StringFilter();
        }
        return phoneNumber;
    }

    public void setPhoneNumber(StringFilter phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public StringFilter getPassword() {
        return password;
    }

    public StringFilter password() {
        if (password == null) {
            password = new StringFilter();
        }
        return password;
    }

    public void setPassword(StringFilter password) {
        this.password = password;
    }

    public StringFilter getFullName() {
        return fullName;
    }

    public StringFilter fullName() {
        if (fullName == null) {
            fullName = new StringFilter();
        }
        return fullName;
    }

    public void setFullName(StringFilter fullName) {
        this.fullName = fullName;
    }

    public StringFilter getOrganizationName() {
        return organizationName;
    }

    public StringFilter organizationName() {
        if (organizationName == null) {
            organizationName = new StringFilter();
        }
        return organizationName;
    }

    public void setOrganizationName(StringFilter organizationName) {
        this.organizationName = organizationName;
    }

    public StringFilter getCreatedBy() {
        return createdBy;
    }

    public StringFilter createdBy() {
        if (createdBy == null) {
            createdBy = new StringFilter();
        }
        return createdBy;
    }

    public void setCreatedBy(StringFilter createdBy) {
        this.createdBy = createdBy;
    }


    public StringFilter getLastModifiedBy() {
        return lastModifiedBy;
    }

    public StringFilter lastModifiedBy() {
        if (lastModifiedBy == null) {
            lastModifiedBy = new StringFilter();
        }
        return lastModifiedBy;
    }

    public void setLastModifiedBy(StringFilter lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final DepoOwnerAccountCriteria that = (DepoOwnerAccountCriteria) o;
        return (
            Objects.equals(id, that.id) &&
                Objects.equals(companyEmail, that.companyEmail) &&
                Objects.equals(phoneNumber, that.phoneNumber) &&
                Objects.equals(password, that.password) &&
                Objects.equals(fullName, that.fullName) &&
                Objects.equals(organizationName, that.organizationName) &&
                Objects.equals(createdBy, that.createdBy) &&
                Objects.equals(lastModifiedBy, that.lastModifiedBy) &&
                Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            companyEmail,
            phoneNumber,
            password,
            fullName,
            organizationName,
            createdBy,
            lastModifiedBy,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DepoOwnerAccountCriteria{" +
            (id != null ? "id=" + id + ", " : "") +
            (companyEmail != null ? "companyEmail=" + companyEmail + ", " : "") +
            (phoneNumber != null ? "phoneNumber=" + phoneNumber + ", " : "") +
            (password != null ? "password=" + password + ", " : "") +
            (fullName != null ? "fullName=" + fullName + ", " : "") +
            (organizationName != null ? "organizationName=" + organizationName + ", " : "") +
            (createdBy != null ? "createdBy=" + createdBy + ", " : "") +
            (lastModifiedBy != null ? "lastModifiedBy=" + lastModifiedBy + ", " : "") +
            (distinct != null ? "distinct=" + distinct + ", " : "") +
            "}";
    }
}
