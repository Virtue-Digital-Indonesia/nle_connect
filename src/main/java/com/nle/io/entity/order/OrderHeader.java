package com.nle.io.entity.order;

import com.nle.constant.enums.BookingStatusEnum;
import com.nle.constant.enums.ItemTypeEnum;
import com.nle.constant.enums.PaymentMethodEnum;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.entity.common.AbstractAuditingEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "order_header")
@Setter
@Getter
public class OrderHeader extends AbstractAuditingEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "tx_date")
    private String tx_date;

    @Column(name = "booking_type")
    @Enumerated(EnumType.STRING)
    private ItemTypeEnum booking_type;

    @Column(name = "full_name")
    private String full_name;

    @Column(name = "phone_number")
    private String phone_number;

    @Column(name = "email")
    private String email;

    @Column(name = "bill_landing")
    private String bill_landing;

    @Column(name = "consignee")
    private String consignee;

    @Column(name = "npwp")
    private String npwp;

    @Column(name = "npwp_address")
    private String npwp_address;

    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private PaymentMethodEnum payment_method;

    @Column(name = "order_status")
    @Enumerated(EnumType.STRING)
    private BookingStatusEnum order_status;

    @Column(name = "tx_date_formatted")
    private LocalDateTime txDateFormatted;

    @ManyToOne
    @JoinColumn(name = "depo_owner_account_id", referencedColumnName = "id")
    private DepoOwnerAccount depoOwnerAccount;

    @OneToMany(mappedBy = "orderHeader", fetch = FetchType.LAZY)
    private Set<OrderDetail> orderDetails;
}
