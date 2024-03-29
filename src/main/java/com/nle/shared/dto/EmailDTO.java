package com.nle.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailDTO {
    private String from;
    private String to;
    private String subject;
    private String templateName;
    private String templateContent;
    private String pathToAttachment;
    private Map<String, String> model;
}
