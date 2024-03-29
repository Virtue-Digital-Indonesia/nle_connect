package com.nle.io.repository.booking;

import com.nle.constant.enums.PaymentStatusEnum;
import com.nle.io.entity.booking.BookingDetailUnloading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface BookingDetailUnloadingRepository extends JpaRepository<BookingDetailUnloading, Long> {
    @Query(value = "SELECT bdu FROM BookingDetailUnloading bdu WHERE bdu.bookingHeader.id = :headerId")
    List<BookingDetailUnloading> getAllByBookingHeaderId(Long headerId);
    @Transactional
    @Query(value = "SELECT bdu FROM BookingDetailUnloading bdu WHERE bdu.container_number = :noContainer " +
            "AND bdu.item.id = :idItem " +
            "AND bdu.paymentStatus = 'UNPAID' " +
            "AND bdu.bookingHeader.depoOwnerAccount.id = :depoId " +
            "AND bdu.bookingHeader.bill_landing = :noBl " +
            "AND bdu.bookingHeader.booking_status IN ('WAITING' , 'SUCCESS')")
    List<BookingDetailUnloading> getValidateContainer(@Param("noBl") String noBl,
                                                      @Param("noContainer") String noContainer,
                                                      @Param("idItem") Long idItem,
                                                      @Param("depoId") Long depoId);
    @Modifying
    @Query(value = "UPDATE BookingDetailUnloading bdu SET bdu.paymentStatus =:status WHERE bdu.bookingHeader.id =:bookingHeaderId")
    void updatePaymentStatus(@Param("bookingHeaderId") Long id,@Param("status") PaymentStatusEnum status);
}
