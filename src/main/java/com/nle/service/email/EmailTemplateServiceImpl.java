package com.nle.service.email;

import com.nle.constant.EmailType;
import com.nle.entity.EmailTemplate;
import com.nle.repository.EmailTemplateRepository;
import com.nle.service.dto.EmailTemplateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmailTemplateServiceImpl implements EmailTemplateService {

    private final EmailTemplateRepository emailTemplateRepository;

    @Override
    public EmailTemplateDto findByType(EmailType emailType) {
        EmailTemplate emailTemplate = emailTemplateRepository.findByType(emailType);
        EmailTemplateDto emailTemplateDto = new EmailTemplateDto();
        BeanUtils.copyProperties(emailTemplate, emailTemplateDto);
        return emailTemplateDto;
    }
}
