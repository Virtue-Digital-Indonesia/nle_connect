package com.nle.io.entity;

import com.nle.io.entity.common.AbstractAuditingEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * A Inventory
 */

@Entity
@Table(name="inventories")
@Getter
@Setter
public class Inventory extends AbstractAuditingEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

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

    @Column(name = "discharge_port")
    private String discharge_port;

    @Column(name = "date_manufacturer")
    private String date_manufacturer;

    @ManyToOne
    @JoinColumn(name = "depo_owner_account_id", referencedColumnName = "id")
    private DepoOwnerAccount depoOwnerAccount;

    @OneToOne
    @JoinColumn(name = "gate_in_id", referencedColumnName = "id")
    private GateMove gateInId;

    @OneToOne
    @JoinColumn(name = "gate_out_id", referencedColumnName = "id")
    private GateMove gateOutId;

}
