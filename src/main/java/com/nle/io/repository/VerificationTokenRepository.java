package com.nle.io.repository;

import com.nle.constant.VerificationType;
import com.nle.io.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data SQL repository for the VerificationToken entity.
 */
@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);

    Optional<VerificationToken> findByDepoOwnerAccount_CompanyEmailAndTokenType(String email, VerificationType verificationType);
}
