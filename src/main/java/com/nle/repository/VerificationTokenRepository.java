package com.nle.repository;

import com.nle.domain.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the VerificationToken entity.
 */
@SuppressWarnings("unused")
@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
}
