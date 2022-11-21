package com.nle.io.entity.booking;

import com.nle.io.entity.Item;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "booking_detail_loading")
@Setter
@Getter
public class BookingDetailLoading {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_header_id", referencedColumnName = "id")
    private BookingHeader bookingHeader;

    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item;

    @Column(name = "price")
    private int price;

    @Column(name = "quantity")
    private int quantity;
}
