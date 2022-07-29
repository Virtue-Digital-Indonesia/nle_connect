package com.nle.entity;

import com.nle.constant.GateMoveSource;
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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * A GateMove.
 */
@Entity
@Table(name = "gate_move")
@Getter
@Setter
@ToString
public class GateMove extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "tx_date")
    private String txDate;

    @Column(name = "process_type")
    private String processType;

    @Column(name = "gate_move_type")
    private String gateMoveType;

    @Column(name = "depot")
    private String depot;

    @Column(name = "fleet_manager")
    private String fleetManager;

    @Column(name = "container_number")
    private String containerNumber;

    @Column(name = "iso_code")
    private String isoCode;

    @Column(name = "container_condition")
    private String condition;

    @Column(name = "date_manufacturer")
    private String dateManufacturer;

    @Column(name = "clean")
    private Boolean clean;

    @Column(name = "grade")
    private String grade;

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "customer")
    private String customer;

    @Column(name = "vessel")
    private String vessel;

    @Column(name = "voyage")
    private String voyage;

    @Column(name = "discarge_port")
    private String discargePort;

    @Column(name = "delivery_port")
    private String deliveryPort;

    @Column(name = "carrier")
    private String carrier;

    @Column(name = "transport_number")
    private String transportNumber;

    @Column(name = "driver_name")
    private String driverName;

    @Column(name = "tare")
    private Double tare;

    @Column(name = "payload")
    private Double payload;

    @Column(name = "max_gross")
    private Double maxGross;

    @Column(name = "remarks")
    private String remarks;

    // mobile only
    @Column(name = "longitude")
    private String longitude;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "link_to_google_map")
    private String linkToGoogleMap;

    @Column(name = "shipping_line")
    private String shippingLine;

    @Column(name = "damage_by")
    private String damageBy;

    @Column(name = "cost")
    private Double cost;

    @Column(name = "trucker_name")
    private String truckerName;

    @Column(name = "truck_no")
    private String truckNo;

    @Column(name = "status")
    private String status;

    @Column(name = "nle_id")
    private String nleId;

    @Column(name = "data_source")
    @Enumerated(EnumType.STRING)
    private GateMoveSource gateMoveSource;

    @OneToMany(mappedBy = "gateMove")
    private Set<Media> media = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "depo_owner_account_id", referencedColumnName = "id")
    private DepoOwnerAccount depoOwnerAccount;

}
