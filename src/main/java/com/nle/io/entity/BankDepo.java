package com.nle.io.entity;

import com.nle.io.entity.common.AbstractAuditingEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "bank_depo")
@Setter
@Getter
public class BankDepo extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "depo_owner_account_id", referencedColumnName = "id")
    private DepoOwnerAccount depoOwnerAccount;

    @Column(name = "bank_code")
    private String bank_code;

    @Column(name = "account_holder_name")
    private String account_holder_name;

    @Column(name = "account_number")
    private String account_number;

    @Column(name = "description_bank")
    private String description_bank;

    @Column(name = "default_bank",columnDefinition = "boolean default false")
    private Boolean default_bank;
}
