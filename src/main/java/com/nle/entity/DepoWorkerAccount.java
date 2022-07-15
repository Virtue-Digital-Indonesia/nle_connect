package com.nle.entity;

import com.nle.constant.AccountStatus;
import com.nle.entity.common.AbstractAuditingEntity;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

import static com.nle.constant.AppConstant.Pattern.EMAIL_PATTERN;

/**
 * A DepoWorkerAccount.
 */
@Entity
@Table(name = "depo_worker_account")
@Getter
@Setter
@ToString
public class DepoWorkerAccount extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "organization_code")
    private String organizationCode;

    @Column(name = "invitation_code")
    private String invitationCode;

    @Column(name = "gate_name")
    private String gateName;

    @NotNull
    @Pattern(regexp = EMAIL_PATTERN)
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "account_status")
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

}
