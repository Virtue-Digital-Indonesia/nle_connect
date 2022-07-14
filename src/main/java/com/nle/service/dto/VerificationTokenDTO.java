package com.nle.service.dto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * A DTO for the {@link com.nle.domain.VerificationToken} entity.
 */
public class VerificationTokenDTO implements Serializable {

    private Long id;

    @NotNull
    private String token;

    @NotNull
    private LocalDateTime expiryDate;

    @NotNull
    private String tokenType;

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

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    @Override
    public String toString() {
        return "VerificationTokenDTO{" +
            "id=" + id +
            ", token='" + token + '\'' +
            ", expiryDate=" + expiryDate +
            ", tokenType='" + tokenType + '\'' +
            '}';
    }
}
