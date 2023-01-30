package com.nle.io.entity;

import com.nle.constant.enums.GateMoveSource;
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
    private String tx_date;

    // mobile only
    @Column(name = "longitude")
    private String longitude;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "process_type")
    private String process_type;

    @Column(name = "depot")
    private String depot;

    @Column(name = "fleet_manager")
    private String fleet_manager;

    @Column(name = "container_number")
    private String container_number;

    @Column(name = "iso_code")
    private String iso_code;

    @Column(name = "container_condition")
    private String condition;

    @Column(name = "clean")
    private Boolean clean;

    @Column(name = "grade")
    private String grade;

    @Column(name = "damage_by")
    private String damage_by;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "order_number")
    private String order_number;

    @Column(name = "customer")
    private String customer;

    @Column(name = "vessel")
    private String vessel;

    @Column(name = "voyage")
    private String voyage;

    @Column(name = "discarge_port")
    private String discharge_port;

    @Column(name = "carrier")
    private String carrier;

    @Column(name = "transport_number")
    private String transport_number;

    @Column(name = "tare")
    private Double tare;

    @Column(name = "payload")
    private Double payload;

    @Column(name = "date_manufacturer")
    private String date_manufacturer;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "delivery_port")
    private String delivery_port;

    @Column(name = "driver_name")
    private String driver_name;

    @Column(name = "max_gross")
    private Double max_gross;

    @Column(name = "link_to_google_map")
    private String linkToGoogleMap;

    @Column(name = "gate_move_type")
    private String gateMoveType;

    @Column(name = "status")
    private String status;

    @Column(name = "nle_id")
    private String nleId;

    @Column(name = "tx_date_formatted")
    private LocalDateTime txDateFormatted;

    @Column(name = "sync_to_tax_ministry_date")
    private LocalDateTime syncToTaxMinistryDate;

    @Column(name = "data_source")
    @Enumerated(EnumType.STRING)
    private GateMoveSource source;

    @OneToMany(mappedBy = "gateMove")
    private Set<Media> media = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "depo_owner_account_id", referencedColumnName = "id")
    private DepoOwnerAccount depoOwnerAccount;

}
