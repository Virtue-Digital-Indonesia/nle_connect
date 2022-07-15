package com.nle.repository;

import com.nle.constant.EmailType;
import com.nle.entity.EmailTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, Long> {
    EmailTemplate findByType(EmailType type);
}
