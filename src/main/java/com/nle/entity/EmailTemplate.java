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
@Table(name = "email_template")
@Getter
@Setter
public class EmailTemplate extends AbstractAuditingEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long emailTemplateId;

    @Size(max = 50)
    @NotNull
    @Column(name = "template_name")
    private String templateName;

    @NotNull
    @Column(name = "template_content")
    private String templateContent;

    @Size(max = 100)
    @NotNull
    @Column(name = "sender_name")
    private String senderName;

    @Size(max = 100)
    @NotNull
    @Column(name = "send_from")
    private String sendFrom;

    @Size(max = 100)
    @NotNull
    @Column(name = "subject")
    private String subject;

    @Column(name = "template_type")
    @Enumerated(EnumType.STRING)
    private EmailType type;

}
