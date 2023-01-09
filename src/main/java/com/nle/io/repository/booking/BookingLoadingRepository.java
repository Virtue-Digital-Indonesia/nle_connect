package com.nle.io.repository.booking;

import com.nle.io.entity.booking.BookingDetailLoading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingLoadingRepository extends JpaRepository<BookingDetailLoading, Long> {
    @Query(value = "SELECT bdl FROM BookingDetailLoading bdl WHERE bdl.bookingHeader.id = :headerId")
    List<BookingDetailLoading> getAllByBookingHeaderId(Long headerId);
}
