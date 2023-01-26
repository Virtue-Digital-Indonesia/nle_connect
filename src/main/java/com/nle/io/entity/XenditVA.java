package com.nle.io.entity;

import com.nle.constant.enums.XenditEnum;
import com.nle.io.entity.booking.BookingHeader;
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

    @OneToOne
    @JoinColumn(name = "booking_header_id", referencedColumnName = "id")
    private BookingHeader booking_header_id;

    @Column(name = "invoice_id")
    private String invoice_id;

    @Column(name = "disbursement_id")
    private String disbursement_id;

    @Column(name = "payment_id")
    private String payment_id;

    @Column(name = "phone_number")
    private String phone_number;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "bank_code")
    private String bank_code;

    @Column(name = "expiry_date")
    private String expiry_date;

    @Column(name = "account_number")
    private String account_number;

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    private XenditEnum payment_status;
}
