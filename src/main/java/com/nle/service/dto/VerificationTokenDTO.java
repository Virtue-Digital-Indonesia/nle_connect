package com.nle.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link com.nle.domain.VerificationToken} entity.
 */
public class VerificationTokenDTO implements Serializable {

    private Long id;

    @NotNull
    private String token;

    @NotNull
    private Instant expiryDate;

    @NotNull
    private String tokenType;

    private String createdBy;

    private Instant createdDate;

    private String lastModifiedBy;

    private Instant lastModifiedDate;

    private DepoOwnerAccountDTO depoOwnerAccount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Instant getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Instant expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public DepoOwnerAccountDTO getDepoOwnerAccount() {
        return depoOwnerAccount;
    }

    public void setDepoOwnerAccount(DepoOwnerAccountDTO depoOwnerAccount) {
        this.depoOwnerAccount = depoOwnerAccount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VerificationTokenDTO)) {
            return false;
        }

        VerificationTokenDTO verificationTokenDTO = (VerificationTokenDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, verificationTokenDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "VerificationTokenDTO{" +
            "id=" + getId() +
            ", token='" + getToken() + "'" +
            ", expiryDate='" + getExpiryDate() + "'" +
            ", tokenType='" + getTokenType() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", depoOwnerAccount=" + getDepoOwnerAccount() +
            "}";
    }
}
