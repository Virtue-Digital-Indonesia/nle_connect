package com.nle.io.repository;

import org.springframework.stereotype.Repository;

import com.nle.io.entity.OtpLog;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface OtpLogRepository extends JpaRepository<OtpLog, Long> {

}
