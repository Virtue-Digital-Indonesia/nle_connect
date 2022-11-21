package com.nle.io.repository.booking;


import com.nle.io.entity.booking.BookingDetailUnloading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingDetailUnloadingRepository extends JpaRepository<BookingDetailUnloading, Long> {
    List<BookingDetailUnloading> getAllByBookingHeaderId(Long headerId);
}
