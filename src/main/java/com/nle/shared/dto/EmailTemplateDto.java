package com.nle.shared.dto;

import com.nle.constant.enums.EmailType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailTemplateDto {
    private Long id;
    private EmailType type;
    private String templateName;
    private String templateContent;
    private String senderName;
    private String sendFrom;
    private String subject;
}
