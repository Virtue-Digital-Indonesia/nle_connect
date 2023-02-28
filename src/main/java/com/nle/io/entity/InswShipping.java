package com.nle.io.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Table(name = "insw_shipping")
@Setter
@Getter
public class InswShipping implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotNull
    @Column(name = "shipping_code", nullable = false, unique = true, length = 4)
    private String code;

    @NotNull
    @Column(name = "shipping_description")
    private String description;

    @NotNull
    @Column(name = "code_box")
    private String code_box;

}
