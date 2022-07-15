package com.nle.service.email;

import com.nle.constant.EmailType;
import com.nle.service.dto.EmailTemplateDto;

public interface EmailTemplateService {
    EmailTemplateDto findByType(EmailType emailType);
}
