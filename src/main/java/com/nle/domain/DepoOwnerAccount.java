package com.nle.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A DepoOwnerAccount.
 */
@Entity
@Table(name = "depo_owner_account")
public class DepoOwnerAccount extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Pattern(regexp = "^[_A-Za-z\\d-+]+(.[_A-Za-z\\d-]+)*@[A-Za-z\\d-]+(.[A-Za-z\\d]+)*(.[A-Za-z]{2,})$")
    @Column(name = "company_email", nullable = false, unique = true)
    private String companyEmail;

    @NotNull
    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "organization_name")
    private String organizationName;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public DepoOwnerAccount id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCompanyEmail() {
        return this.companyEmail;
    }

    public DepoOwnerAccount companyEmail(String companyEmail) {
        this.setCompanyEmail(companyEmail);
        return this;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public DepoOwnerAccount phoneNumber(String phoneNumber) {
        this.setPhoneNumber(phoneNumber);
        return this;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return this.password;
    }

    public DepoOwnerAccount password(String password) {
        this.setPassword(password);
        return this;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return this.fullName;
    }

    public DepoOwnerAccount fullName(String fullName) {
        this.setFullName(fullName);
        return this;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getOrganizationName() {
        return this.organizationName;
    }

    public DepoOwnerAccount organizationName(String organizationName) {
        this.setOrganizationName(organizationName);
        return this;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public DepoOwnerAccount createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getCreatedDate() {
        return this.createdDate;
    }

    public DepoOwnerAccount createdDate(LocalDateTime createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    public DepoOwnerAccount lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public LocalDateTime getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public DepoOwnerAccount lastModifiedDate(LocalDateTime lastModifiedDate) {
        this.setLastModifiedDate(lastModifiedDate);
        return this;
    }

    public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DepoOwnerAccount)) {
            return false;
        }
        return id != null && id.equals(((DepoOwnerAccount) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "DepoOwnerAccount{" +
            "id=" + getId() +
            ", companyEmail='" + getCompanyEmail() + "'" +
            ", phoneNumber='" + getPhoneNumber() + "'" +
            ", password='" + getPassword() + "'" +
            ", fullName='" + getFullName() + "'" +
            ", organizationName='" + getOrganizationName() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
