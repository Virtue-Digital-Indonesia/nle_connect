package com.nle.io.repository;

import com.nle.io.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Media entity.
 */
@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

}
