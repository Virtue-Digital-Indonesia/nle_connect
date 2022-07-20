package com.nle.repository;

import com.nle.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Media entity.
 */
@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {

}
