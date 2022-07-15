package com.nle.entity;

import com.nle.constant.VerificationType;
import com.nle.entity.common.AbstractAuditingEntity;

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

    @ManyToOne(optional = true)
    private DepoOwnerAccount depoOwnerAccount;

    @ManyToOne(optional = true)
    private DepoWorkerAccount depoWorkerAccount;

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

    public VerificationType getTokenType() {
        return tokenType;
    }

    public void setTokenType(VerificationType tokenType) {
        this.tokenType = tokenType;
    }

    public DepoOwnerAccount getDepoOwnerAccount() {
        return depoOwnerAccount;
    }

    public void setDepoOwnerAccount(DepoOwnerAccount depoOwnerAccount) {
        this.depoOwnerAccount = depoOwnerAccount;
    }

    public DepoWorkerAccount getDepoWorkerAccount() {
        return depoWorkerAccount;
    }

    public void setDepoWorkerAccount(DepoWorkerAccount depoWorkerAccount) {
        this.depoWorkerAccount = depoWorkerAccount;
    }
}
