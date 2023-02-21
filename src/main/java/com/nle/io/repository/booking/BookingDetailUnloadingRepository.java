package com.nle.io.repository.booking;

import com.nle.io.entity.booking.BookingDetailUnloading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingDetailUnloadingRepository extends JpaRepository<BookingDetailUnloading, Long> {
    @Query(value = "SELECT bdu FROM BookingDetailUnloading bdu WHERE bdu.bookingHeader.id = :headerId")
    List<BookingDetailUnloading> getAllByBookingHeaderId(Long headerId);
    @Query(value = "SELECT bdu FROM BookingDetailUnloading bdu WHERE bdu.container_number = :noContainer " +
            "AND bdu.item.id = :idItem " +
            "AND bdu.paymentStatus = 'UNPAID' " +
            "AND bdu.bookingHeader.depoOwnerAccount.id = :depoId " +
            "AND bdu.bookingHeader.booking_status = 'WAITING'")
    List<BookingDetailUnloading> getValidateContainer(@Param("noContainer") String noContainer,
                                                      @Param("idItem") Long idItem,
                                                      @Param("depoId") Long depoId);
}
