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
            "WHERE oh.phone_number = :#{#request.phone_number} " +
            "AND (:#{#request.bill_landing} IS NULL OR LOWER(oh.bill_landing) LIKE LOWER(CONCAT('%', :#{#request.bill_landing} ,'%'))) " +
            "AND (:#{#request.full_name} IS NULL OR LOWER(oh.full_name) LIKE LOWER(CONCAT('%', :#{#request.full_name}, '%'))) " +
            "AND (:#{#request.consignee} IS NULL OR LOWER(oh.consignee) LIKE LOWER(CONCAT('%', :#{#request.consignee}, '%'))) " +
            "AND (:#{#request.booking_type} IS NULL OR UPPER(oh.booking_type) LIKE UPPER(:#{#request.booking_type})) " +
            "AND (:#{#request.booking_status} IS NULL OR UPPER(oh.booking_status) LIKE UPPER(CONCAT('%', :#{#request.booking_status}, '%'))) " +
            "AND (:#{#request.tx_date} IS NULL OR oh.tx_date LIKE CONCAT('%', :#{#request.tx_date}, '%')) " +
            "AND (:#{#request.globalSearch} IS NULL " +
            "OR LOWER(oh.bill_landing) LIKE LOWER(CONCAT('%', :#{#request.globalSearch}, '%')) " +
            "OR LOWER(oh.full_name) LIKE LOWER(CONCAT('%', :#{#request.globalSearch}, '%')) " +
            "OR LOWER(oh.consignee) LIKE LOWER(CONCAT('%', :#{#request.globalSearch}, '%')) " +
            "OR UPPER(oh.booking_type) LIKE UPPER(:#{#request.globalSearch}) " +
            "OR UPPER(oh.booking_status) LIKE UPPER(CONCAT('%', :#{#request.globalSearch}, '%')) " +
            "OR oh.tx_date LIKE CONCAT('%', :#{#request.globalSearch}, '%')" +
            ")";

    @Query(value = "SELECT * FROM booking_header WHERE booking_header.phone_number = :phoneNumber AND booking_header.order_status != 'DONE'", nativeQuery = true)
    Page<BookingHeader> getOrderByPhoneNumber(@Param("phoneNumber") String phoneNumber, Pageable pageable);

    @Query(value = SEARCH_BOOKING_QUERY)
    Page<BookingHeader> searchBooking(@Param("request")BookingSearchRequest request, Pageable pageable);

}
