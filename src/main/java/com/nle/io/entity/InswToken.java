package com.nle.io.entity;

import com.nle.io.entity.common.AbstractAuditingEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "insw_token")
@Getter
@Setter
public class InswToken extends AbstractAuditingEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @NotNull
    @Column(name = "refresh_token", nullable = false, unique = true)
    private String refreshToken;

    @Column(name = "token_type")
    private String tokenType;
    @NotNull
    @Column(name = "access_token", nullable = false, unique = true)
    private String accessToken;

    @Column(name = "active_status")
    private String activeStatus;

    @NotNull
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;
}
