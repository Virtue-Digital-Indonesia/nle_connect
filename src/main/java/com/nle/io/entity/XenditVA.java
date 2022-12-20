package com.nle.io.entity;

import com.nle.io.entity.common.AbstractAuditingEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "xendit_va")
@Setter
@Getter
public class XenditVA extends AbstractAuditingEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "xendit_id")
    private String xendit_id;

    @Column(name = "phone_number")
    private String phone_number;

    @Column(name = "amount")
    private int amount;

    @Column(name = "bank_code")
    private String bank_code;

    @Column(name = "payment_status")
    private String payment_status;

    @Column(name = "expired_date")
    private String expired_date;
}
