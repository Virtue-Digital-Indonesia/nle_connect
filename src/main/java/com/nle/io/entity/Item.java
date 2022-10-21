package com.nle.io.entity;

import com.nle.constant.enums.ItemTypeEnum;
import com.nle.io.entity.common.AbstractAuditingEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "items")
@Setter
@Getter
public class Item extends AbstractAuditingEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Column(name = "item_name")
    private String item_name;

    @Column(name = "item_sku")
    private String sku;

    @Column(name = "item_descripion")
    private String description;

    @Column(name = "sales_price")
    private int price;

    @Column(name = "item_type")
    private ItemTypeEnum type;

    @Column(name = "item_status")
    private Boolean status;


    @ManyToOne
    @JoinColumn(name = "depo_owner_account_id", referencedColumnName = "id")
    private DepoOwnerAccount depoOwnerAccount;

    @ManyToOne
    @JoinColumn(name = "fleet_id", referencedColumnName = "id")
    private Fleet fleet;

}
