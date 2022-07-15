package com.nle.entity;

import com.nle.constant.EmailType;
import com.nle.entity.common.AbstractAuditingEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Entity
@Table(name = "EMAIL_TEMPLATE")
@Getter
@Setter
public class EmailTemplate extends AbstractAuditingEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long emailTemplateId;

    @Size(max = 50)
    @NotNull
    @Column(name = "TEMPLATE_NAME")
    private String templateName;

    @NotNull
    @Column(name = "TEMPLATE_CONTENT")
    private String templateContent;

    @Size(max = 100)
    @NotNull
    @Column(name = "SENDER_NAME")
    private String senderName;

    @Size(max = 100)
    @NotNull
    @Column(name = "SEND_FROM")
    private String sendFrom;

    @Size(max = 100)
    @NotNull
    @Column(name = "SUBJECT")
    private String subject;

    @Column(name = "TEMPLATE_TYPE")
    @Enumerated(EnumType.STRING)
    private EmailType type;

}
