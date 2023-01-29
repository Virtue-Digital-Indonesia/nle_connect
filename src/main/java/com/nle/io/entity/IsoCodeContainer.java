package com.nle.io.entity;

import com.nle.io.entity.common.AbstractAuditingEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "iso_container_codes")
@Setter
@Getter
public class IsoCodeContainer extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "iso_code", length = 4)
    private String iso_code;

    @Column(name = "iso_description")
    private String iso_description;

    @Column(name = "iso_length")
    private int iso_length;

    @Column(name = "iso_height")
    private float iso_height;

    @Column(name = "iso_group")
    private String iso_group;
}