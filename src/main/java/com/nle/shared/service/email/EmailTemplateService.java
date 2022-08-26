package com.nle.shared.service.email;

import com.nle.constant.enums.EmailType;
import com.nle.shared.dto.EmailTemplateDto;

public interface EmailTemplateService {
    EmailTemplateDto findByType(EmailType emailType);
}
