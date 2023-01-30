package com.nle.io.entity.booking;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "booking_otp")
@Setter
@Getter
@ToString
public class BookingOTP {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "otp")
    private String OTP;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "expired_time")
    private LocalDateTime expiredTime;

    @ManyToOne
    @JoinColumn(name = "booking_header_id", referencedColumnName = "id")
    private BookingHeader bookingHeader;
}
