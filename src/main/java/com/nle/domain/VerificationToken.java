package com.nle.domain;

import com.nle.constant.VerificationType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A VerificationToken.
 */
@Entity
@Table(name = "verification_token")
public class VerificationToken extends AbstractAuditingEntity implements Serializable {

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
    private LocalDateTime expiryDate;

    @NotNull
    @Column(name = "token_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private VerificationType tokenType;

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

    public LocalDateTime getExpiryDate() {
        return this.expiryDate;
    }

    public VerificationToken expiryDate(LocalDateTime expiryDate) {
        this.setExpiryDate(expiryDate);
        return this;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public VerificationType getTokenType() {
        return this.tokenType;
    }

    public VerificationToken tokenType(VerificationType tokenType) {
        this.setTokenType(tokenType);
        return this;
    }

    public void setTokenType(VerificationType tokenType) {
        this.tokenType = tokenType;
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
