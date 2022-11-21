package com.nle.io.repository.booking;

import com.nle.io.entity.booking.BookingDetailLoading;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingLoadingRepository extends JpaRepository<BookingDetailLoading, Long> {
}
