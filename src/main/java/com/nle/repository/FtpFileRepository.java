package com.nle.repository;

import com.nle.entity.FtpFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data SQL repository for the FtpFile entity.
 */
@Repository
public interface FtpFileRepository extends JpaRepository<FtpFile, Long> {
    Optional<FtpFile> findByFileName(String fileName);
}
