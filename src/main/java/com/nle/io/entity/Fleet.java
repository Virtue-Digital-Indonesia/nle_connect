package com.nle.io.entity;

import com.nle.io.entity.common.AbstractAuditingEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="fleets")
@Setter
@Getter
public class Fleet extends AbstractAuditingEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "fleet_code", length = 4)
    private String code;

    @Column(name = "fleet_name")
    private String fleet_manager_company;

    @Column(name = "fleet_city")
    private String city;

    @Column(name = "fleet_country")
    private String country;
}
