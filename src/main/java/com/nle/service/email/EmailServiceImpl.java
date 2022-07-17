package com.nle.service.email;

import com.nle.config.prop.AppProperties;
import com.nle.constant.EmailType;
import com.nle.entity.DepoOwnerAccount;
import com.nle.service.dto.EmailDTO;
import com.nle.service.dto.EmailTemplateDto;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final Configuration config;
    private final EmailTemplateService emailTemplateService;
    private final AppProperties appProperties;
    private final JavaMailSender javaMailSender;

    @Override
    public void sendDepoOwnerActiveEmail(DepoOwnerAccount depoOwnerAccount, String token) {
        Map<String, String> params = new HashMap<>();
        params.put("fullName", depoOwnerAccount.getFullName());
        params.put("activeUrl", appProperties.getUrl().getActiveUrl() + token);
        // get email template content from DB
        EmailTemplateDto activeEmailTemplate = emailTemplateService.findByType(EmailType.ACTIVE_DEPO_OWNER);
        EmailDTO emailDTO = buildEmailDTO(activeEmailTemplate, params, depoOwnerAccount.getCompanyEmail());
        sendSimpleEmail(emailDTO);
    }

    @Override
    public void sendDepoWorkerInvitationEmail(String workerEmail, String activationCode) {
        Map<String, String> params = new HashMap<>();
        params.put("workerEmail", workerEmail);
        params.put("activationCode", activationCode);
        // get email template content from DB
        EmailTemplateDto activeEmailTemplate = emailTemplateService.findByType(EmailType.INVITE_DEPO_WORKER);
        EmailDTO emailDTO = buildEmailDTO(activeEmailTemplate, params, workerEmail);
        sendSimpleEmail(emailDTO);
    }

    @Override
    public void sendDepoWorkerApproveEmail(String workerFullName, String depoOwnerFullName, String email) {
        Map<String, String> params = new HashMap<>();
        params.put("workerFullName", workerFullName);
        params.put("depoOwnerFullName", depoOwnerFullName);
        // get email template content from DB
        EmailTemplateDto activeEmailTemplate = emailTemplateService.findByType(EmailType.APPROVE_DEPO_WORKER);
        EmailDTO emailDTO = buildEmailDTO(activeEmailTemplate, params, email);
        sendSimpleEmail(emailDTO);
    }

    @Override
    @Async
    public void sendSimpleEmail(EmailDTO emailDTO) {
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            // set mediaType
            MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                StandardCharsets.UTF_8.name());
            Template template = new Template(emailDTO.getTemplateName(), emailDTO.getTemplateContent(), config);
            String html = FreeMarkerTemplateUtils.processTemplateIntoString(template, emailDTO.getModel());
            helper.setTo(emailDTO.getTo());
            helper.setText(html, true);
            helper.setSubject(emailDTO.getSubject());
            helper.setFrom(emailDTO.getFrom());
            javaMailSender.send(message);
            log.info("Mail sent to : " + emailDTO.getTo());
        } catch (MessagingException | IOException | TemplateException e) {
            log.error("Mail Sending failure : " + e.getMessage(), e);
        }
    }

    private EmailDTO buildEmailDTO(EmailTemplateDto activeEmailTemplate, Map<String, String> params, String email) {
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setFrom(activeEmailTemplate.getSendFrom());
        emailDTO.setTo(email);
        emailDTO.setSubject(activeEmailTemplate.getSubject());
        emailDTO.setTemplateName(activeEmailTemplate.getTemplateName());
        emailDTO.setTemplateContent(activeEmailTemplate.getTemplateContent());
        emailDTO.setModel(params);
        return emailDTO;
    }
}
