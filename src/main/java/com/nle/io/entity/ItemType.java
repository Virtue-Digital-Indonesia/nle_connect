package com.nle.io.entity;

import com.nle.io.entity.common.AbstractAuditingEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "item_type_name")
@Setter
@Getter
public class ItemType extends AbstractAuditingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "item_code", length = 4)
    private String itemCode;

    @Column(name = "item_type")
    private String itemType;

    @Column(name = "item_size")
    private int itemSize;
}
