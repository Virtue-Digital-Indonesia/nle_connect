package com.nle.domain;

import java.io.Serializable;
import java.time.Instant;
import javax.persistence.*;
import javax.validation.constraints.*;

/**
 * A VerificationToken.
 */
@Entity
@Table(name = "verification_token")
public class VerificationToken implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @NotNull
    @Column(name = "expiry_date", nullable = false)
    private Instant expiryDate;

    @NotNull
    @Column(name = "token_type", nullable = false)
    private String tokenType;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private Instant createdDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @Column(name = "last_modified_date")
    private Instant lastModifiedDate;

    @ManyToOne
    private DepoOwnerAccount depoOwnerAccount;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public VerificationToken id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return this.token;
    }

    public VerificationToken token(String token) {
        this.setToken(token);
        return this;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getExpiryDate() {
        return this.expiryDate;
    }

    public VerificationToken expiryDate(Instant expiryDate) {
        this.setExpiryDate(expiryDate);
        return this;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getTokenType() {
        return this.tokenType;
    }

    public VerificationToken tokenType(String tokenType) {
        this.setTokenType(tokenType);
        return this;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public VerificationToken createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public VerificationToken createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    public VerificationToken lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public VerificationToken lastModifiedDate(Instant lastModifiedDate) {
        this.setLastModifiedDate(lastModifiedDate);
        return this;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public DepoOwnerAccount getDepoOwnerAccount() {
        return this.depoOwnerAccount;
    }

    public void setDepoOwnerAccount(DepoOwnerAccount depoOwnerAccount) {
        this.depoOwnerAccount = depoOwnerAccount;
    }

    public VerificationToken depoOwnerAccount(DepoOwnerAccount depoOwnerAccount) {
        this.setDepoOwnerAccount(depoOwnerAccount);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VerificationToken)) {
            return false;
        }
        return id != null && id.equals(((VerificationToken) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VerificationToken{" +
            "id=" + getId() +
            ", token='" + getToken() + "'" +
            ", expiryDate='" + getExpiryDate() + "'" +
            ", tokenType='" + getTokenType() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            "}";
    }
}
