package com.nle.io.repository.booking;

import com.nle.constant.enums.BookingStatusEnum;
import com.nle.io.entity.booking.BookingHeader;
import com.nle.ui.model.request.search.BookingSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingHeaderRepository extends JpaRepository<BookingHeader, Long> {

    static final String SEARCH_BOOKING_QUERY = "SELECT oh FROM BookingHeader oh " +
            "WHERE (:#{#request.bill_landing} IS NULL OR LOWER(oh.bill_landing) LIKE LOWER(CONCAT('%', :#{#request.bill_landing} ,'%'))) " +
            "AND (:#{#request.full_name} IS NULL OR LOWER(oh.full_name) LIKE LOWER(CONCAT('%', :#{#request.full_name}, '%'))) " +
            "AND (:#{#request.consignee} IS NULL OR LOWER(oh.consignee) LIKE LOWER(CONCAT('%', :#{#request.consignee}, '%'))) " +
            "AND (:#{#request.booking_type} IS NULL OR UPPER(oh.booking_type) LIKE UPPER(:#{#request.booking_type})) " +
            "AND (:#{#request.booking_status} IS NULL OR UPPER(oh.booking_status) LIKE UPPER(CONCAT('%', :#{#request.booking_status}, '%'))) " +
            "AND (:#{#request.email} IS NULL OR LOWER(oh.email) LIKE LOWER(CONCAT('%',:#{#request.email},'%'))) " +
            "AND (:#{#request.npwp} IS NULL OR LOWER(oh.npwp) LIKE LOWER(CONCAT('%',:#{#request.npwp},'%'))) " +
            "AND (:#{#request.npwp_address} IS NULL OR LOWER(oh.npwp_address) LIKE LOWER(CONCAT('%',:#{#request.npwp_address},'%'))) " +
            "AND (:#{#request.payment_method} IS NULL OR LOWER(oh.payment_method) LIKE LOWER(CONCAT('%',:#{#request.payment_method},'%'))) " +
            "AND (:#{#request.tx_date} IS NULL OR oh.tx_date LIKE CONCAT('%', :#{#request.tx_date}, '%')) " +
            "AND (:#{#request.from} IS NULL OR oh.tx_date >= :#{#request.from}) " +
            "AND (:#{#request.to} IS NULL OR oh.tx_date <= CONCAT(:#{#request.to}, 'T24:00:01')) " +
            "AND (:#{#request.globalSearch} IS NULL " +
            "OR LOWER(oh.bill_landing) LIKE LOWER(CONCAT('%', :#{#request.globalSearch}, '%')) " +
            "OR LOWER(oh.full_name) LIKE LOWER(CONCAT('%', :#{#request.globalSearch}, '%')) " +
            "OR LOWER(oh.consignee) LIKE LOWER(CONCAT('%', :#{#request.globalSearch}, '%')) " +
            "OR UPPER(oh.booking_type) LIKE UPPER(:#{#request.globalSearch}) " +
            "OR UPPER(oh.booking_status) LIKE UPPER(CONCAT('%', :#{#request.globalSearch}, '%')) " +
            "OR oh.tx_date LIKE CONCAT('%', :#{#request.globalSearch}, '%')" +
            ") ";

    @Query(value = "SELECT bh FROM BookingHeader bh WHERE bh.bookingCustomer.phone_number = :phoneNumber AND bh.booking_status != 'DONE'")
    Page<BookingHeader> getOrderByPhoneNumber(@Param("phoneNumber") String phoneNumber, Pageable pageable);

    @Query(value = SEARCH_BOOKING_QUERY+ "AND oh.bookingCustomer.phone_number = :#{#request.phone_number} ")
    Page<BookingHeader> searchBooking(@Param("request")BookingSearchRequest request, Pageable pageable);

    @Query(value = SEARCH_BOOKING_QUERY +
            "AND (:#{#request.phone_number} IS NULL OR (oh.phone_number = :#{#request.phone_number})) " +
            "AND oh.depoOwnerAccount.companyEmail = :companyEmail ")
    Page<BookingHeader> searchOrder(@Param("request")BookingSearchRequest request,@Param("companyEmail") String companyEmail, Pageable pageable);

    @Query(value = "SELECT bh FROM BookingHeader bh WHERE bh.depoOwnerAccount.companyEmail = :companyEmail")
    Page<BookingHeader> getOrderDepo(@Param("companyEmail") String companyEmail, Pageable pageable);

    @Modifying
    @Query(value = "update BookingHeader bh set bh.booking_status =:status where bh.id =:id")
    void cancelStatus(@Param("status") BookingStatusEnum statusEnum, @Param("id") Long bookingId);

}
