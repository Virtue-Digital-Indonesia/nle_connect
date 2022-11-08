package com.nle.io.entity.order;

import com.nle.io.entity.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "order_detail")
@Setter
@Getter
public class OrderDetail {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_header_id", referencedColumnName = "id")
    private OrderHeader orderHeader;

    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item;

    @Column(name = "price")
    private int price;

}
