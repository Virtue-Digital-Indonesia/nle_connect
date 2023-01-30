package com.nle.io.entity;

import com.nle.constant.enums.AccountStatus;
import com.nle.io.entity.common.AbstractAuditingEntity;
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
import java.io.Serializable;

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

    @Column(name = "android_id")
    private String androidId;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "organization_code")
    private String organizationCode;

    @Column(name = "gate_name")
    private String gateName;

    @Column(name = "account_status")
    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

}
