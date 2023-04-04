package com.nle.io.repository;

import com.nle.io.entity.BookingCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookingCustomerRepository extends JpaRepository<BookingCustomer, Long> {

    @Query(value = "SELECT bc FROM BookingCustomer bc WHERE bc.phone_number = :phone_number")
    Optional<BookingCustomer> findByPhoneNumber(String phone_number);

    Optional<BookingCustomer> findByEmail(String userName);
}
