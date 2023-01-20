package com.nle.io.repository;

import com.nle.io.entity.BankDepo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankDepoRepository extends JpaRepository<BankDepo, Long> {

    @Query(value = "SELECT bd FROM BankDepo bd WHERE bd.depoOwnerAccount.companyEmail = :companyEmail AND bd.default_bank = true")
    Optional<BankDepo> findDefaultDepoByCompanyEmail (String companyEmail);

    @Query(value = "SELECT bd FROM BankDepo bd WHERE bd.depoOwnerAccount.companyEmail = :companyEmail")
    List<BankDepo> getAllBankDepoByCompanyEmail (String companyEmail);
}
