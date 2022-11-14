package com.nle.io.repository.booking;

import com.nle.io.entity.booking.BookingHeader;
import com.nle.ui.model.request.search.BookingSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingHeaderRepository extends JpaRepository<BookingHeader, Long> {

    static final String SEARCH_BOOKING_QUERY = "SELECT oh FROM BookingHeader oh " +
            "WHERE oh.phone_number = :#{#request.phone_number}";

    @Query(value = "SELECT * FROM booking_header WHERE booking_header.phone_number = :phoneNumber AND booking_header.order_status != 'DONE'", nativeQuery = true)
    Page<BookingHeader> getOrderByPhoneNumber(@Param("phoneNumber") String phoneNumber, Pageable pageable);

    @Query(value = SEARCH_BOOKING_QUERY)
    Page<BookingHeader> searchBooking(@Param("request")BookingSearchRequest request, Pageable pageable);

}
