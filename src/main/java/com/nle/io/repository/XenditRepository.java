package com.nle.io.repository;

import com.nle.constant.enums.XenditEnum;
import com.nle.io.entity.XenditVA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface XenditRepository extends JpaRepository<XenditVA, Long> {

    @Query(value = "SELECT xa FROM XenditVA xa WHERE xa.xendit_id = :xendit_id")
    Optional<XenditVA> getVaWithXenditId(String xendit_id);

    @Query(value = "SELECT xa FROM XenditVA xa WHERE xa.invoice_id = :invoice_id AND xa.payment_status = 'PENDING'")
    Optional<XenditVA> findWithInvoiceId(String invoice_id);

    @Query(value = "SELECT xa FROM XenditVA xa WHERE xa.phone_number = :phone AND xa.bank_code = :bank AND xa.payment_status = 'PENDING'")
    Optional<XenditVA> getVaWithPhoneAndBankAndPendingPayment(String phone, String bank);

    @Query(value = "SELECT xa FROM XenditVA xa WHERE xa.phone_number = :phone AND xa.payment_status = 'PENDING'")
    Optional<XenditVA> getVaWithPhoneAndPendingPayment(String phone);

    @Query(value = "SELECT xa FROM XenditVA xa WHERE xa.booking_header_id.id = :booking_id AND xa.payment_status != 'EXPIRED'")
    Optional<XenditVA> findWithBookingID(Long booking_id);

    @Query(value = "SELECT xa FROM XenditVA xa WHERE xa.phone_number = :phone AND xa.payment_status != 'EXPIRED'")
    List<XenditVA> findWithPhone(String phone);

    @Query(value = "SELECT xa FROM XenditVA xa WHERE xa.booking_header_id.id = :bookingId")
    Optional<XenditVA> getVaWithBooking(Long bookingId);
    @Modifying
    @Query(value = "update XenditVA xa set xa.payment_status =:payment_status where xa.id =:id")
    int updateCancelOrder(@Param("payment_status") XenditEnum paymentStatus, @Param("id") Long idXendit);

    @Query(value = "SELECT xa FROM XenditVA xa WHERE xa.disbursement_id = :disbursement_id")
    Optional<XenditVA> findByDisbursement_id(String disbursement_id);

}
