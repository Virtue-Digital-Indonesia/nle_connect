package com.nle.io.repository;

import com.nle.constant.EmailType;
import com.nle.io.entity.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {
    EmailTemplate findByType(EmailType type);
}
