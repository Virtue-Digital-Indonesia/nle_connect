package com.nle.io.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "depo_fleet")
@Setter
@Getter
public class DepoFleet implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "depo_owner_account_id", referencedColumnName = "id")
    private DepoOwnerAccount depoOwnerAccount;

    @ManyToOne
    @JoinColumn(name = "fleet_id", referencedColumnName = "id")
    private Fleet fleet;

    @Column(name = "custom_name")
    private String name;

    @Column(name = "deleted",columnDefinition = "boolean default false")
    private Boolean deleted;
}
