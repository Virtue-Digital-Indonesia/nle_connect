package com.nle.io.repository.booking;


import com.nle.io.entity.booking.BookingDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingDetailRepository extends JpaRepository<BookingDetail, Long> {
    List<BookingDetail> getAllByBookingHeaderId(Long headerId);
}
