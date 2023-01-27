package com.nle.io.entity.booking;

import com.nle.constant.enums.BookingStatusEnum;
import com.nle.constant.enums.ItemTypeEnum;
import com.nle.constant.enums.PaymentMethodEnum;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.io.entity.XenditVA;
import com.nle.io.entity.common.AbstractAuditingEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "booking_header")
@Setter
@Getter
public class BookingHeader extends AbstractAuditingEntity implements Serializable {
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

    @Column(name = "booking_status")
    @Enumerated(EnumType.STRING)
    private BookingStatusEnum booking_status;

    @Column(name = "disbursement_status")
    private boolean disbursement_status;

    @Column(name = "disbursement_date")
    private LocalDateTime disbursement_date;

    @Column(name = "tx_date_formatted")
    private LocalDateTime txDateFormatted;

    @ManyToOne
    @JoinColumn(name = "depo_owner_account_id", referencedColumnName = "id")
    private DepoOwnerAccount depoOwnerAccount;

    @OneToMany(mappedBy = "bookingHeader", fetch = FetchType.LAZY)
    private Set<BookingDetailUnloading> bookingDetailUnloadings;

    @OneToMany(mappedBy = "bookingHeader", fetch = FetchType.LAZY)
    private Set<BookingDetailLoading> bookingDetailLoadings;

    @OneToMany
    @JoinColumn(name = "booking_header_id", referencedColumnName = "id")
    private List<XenditVA> xenditVAS;

}
