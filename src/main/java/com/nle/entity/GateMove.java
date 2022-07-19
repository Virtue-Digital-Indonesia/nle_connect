package com.nle.entity;

import com.nle.constant.ContainerCondition;
import com.nle.constant.ContainerGrade;
import com.nle.constant.GateMoveType;
import com.nle.constant.ProcessType;
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
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Column(name = "date_time")
    private LocalDateTime dateTime;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "link_to_google_map")
    private String linkToGoogleMap;

    @Column(name = "process")
    @Enumerated(EnumType.STRING)
    private ProcessType process;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private GateMoveType type;

    @Column(name = "depot")
    private String depot;

    @Column(name = "shipping_line")
    private String shippingLine;

    @Column(name = "container_number")
    private String containerNumber;

    @Column(name = "iso_code")
    private String isoCode;

    @Column(name = "container_size")
    private String size;

    @Column(name = "container_type")
    private String containerType;

    @Column(name = "container_condition")
    @Enumerated(EnumType.STRING)
    private ContainerCondition containerCondition;

    @Column(name = "clean")
    private Boolean CLEAN;

    @Column(name = "cleaning")
    private String cleaning;

    @Column(name = "grade")
    private ContainerGrade grade;

    @Column(name = "damage_by")
    private String damageBy;

    @Column(name = "cost")
    private Double cost;

    @Column(name = "order_number")
    private String orderNumber;

    @Column(name = "customer")
    private String customer;

    @Column(name = "vessel")
    private String vessel;

    @Column(name = "voyage")
    private String voyage;

    @Column(name = "discharge_port")
    private String dischargePort;

    @Column(name = "trucker_name")
    private String truckerName;

    @Column(name = "truck_no")
    private String truckNo;

    @Column(name = "tare")
    private String tare;

    @Column(name = "payload")
    private String PAYLOAD;

    @Column(name = "date_manufactured")
    private LocalDate dateManufactured;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "photos")
    private String photos;

}
