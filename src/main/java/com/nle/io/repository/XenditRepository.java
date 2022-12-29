package com.nle.io.repository;

import com.nle.io.entity.XenditVA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface XenditRepository extends JpaRepository<XenditVA, Long> {

    @Query(value = "SELECT xa FROM XenditVA xa WHERE xa.phone_number = :phone AND xa.bank_code = :bank")
    Optional<XenditVA> getVaWithPhoneAndBank(String phone, String bank);

    @Query(value = "SELECT xa FROM XenditVA xa WHERE xa.xendit_id = :xendit_id")
    Optional<XenditVA> getVaWithXenditId(String xendit_id);

    Optional<XenditVA> findWithInvoiceId(String xendit_id);
}
