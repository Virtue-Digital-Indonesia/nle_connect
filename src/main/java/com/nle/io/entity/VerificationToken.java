package com.nle.io.entity;

import com.nle.constant.VerificationType;
import com.nle.io.entity.common.AbstractAuditingEntity;
import lombok.Getter;
import lombok.Setter;

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
@Getter
@Setter
public class VerificationToken extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "active_status", nullable = false, unique = true)
    private String activeStatus;

    @NotNull
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    @NotNull
    @Column(name = "token_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private VerificationType tokenType;

    @ManyToOne
    private DepoOwnerAccount depoOwnerAccount;

    @ManyToOne
    private DepoWorkerAccount depoWorkerAccount;

}
