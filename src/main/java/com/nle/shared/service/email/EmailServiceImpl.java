package com.nle.shared.service.email;

import com.nle.config.prop.AppProperties;
import com.nle.constant.enums.EmailType;
import com.nle.io.entity.DepoOwnerAccount;
import com.nle.shared.dto.EmailDTO;
import com.nle.shared.dto.EmailTemplateDto;
import com.nle.shared.dto.ftp.FtpMoveDTOError;
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
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final Configuration config;
    private final EmailTemplateService emailTemplateService;
    private final AppProperties appProperties;
    private final JavaMailSender javaMailSender;

    private String ALT_IMAGE1 = "https://lh3.googleusercontent.com/R7NwGjIivj-zym3WwCghI2y-amUScEiSRG9VWKduER-x9eoXRuhkHDtVklo49JG6YKSxm3M9XPplKlxRbi13iDiA-mr2dNjnNF_yPRMOahiv6zK-i6Zt1m_iudTa1ptdc8ud-npcalnRR-snsaelMMMJKjSIfmATE4eWAA3m8zKYOhe_V0Ybk7KzqkMthzXOiNHK-1amC3UCftnORtqj5tZ9fx81Mz9HWpKJWcNYz0PMMljutbDDEEM0_N25Lj2znY9ZvJ-b-qNju9xPq9mXq0RaJmslRAydi4WfxJolRF1aUvCfxTTEFDQ5QKSUW3PeWbURtCdLqQJTnNu7T9MXWyEO0l2iJoBuPjHEpyQFcAprIP0tz-uWshmXPb1goqIStSveRKJD1yRTm8LKT1ekdEEKZJ_zeRGGyUaQFHpR8mwwuXlniDVr5MeiFAW9GBdyUM9UCBwgSQ5MovDLhj6vH9cj9YQknSiu71zUoskq0kWlecupZYdGVkptSUuiYR_tk7E6vSYH8nCDN9rVdU9f0NOGoPTdJqzcyqna2BWnNBcKZDskg4TQ9R4OTenLuetpbM7V7ma2c04HnRhxzJtHur4IH-dsXOOsARYdpS73LMQccBD-YdcHW8AqserGmM42Ze3Fo48dxywm_tB9pXUqur5ArikhEgo49W_8Jux49GIzf5nVDhefMjGXRkYasUGt1esFcY3XTxASPaBm0KLfdB5_oaO3Vcz3TABaxOdUkfJUlwPFT8GK8DZitXEzkw=w1216-h273-no?authuser=0";
    private String ALT_IMAGE2 = "https://lh3.googleusercontent.com/XKrvxht3-WGL8BuY9q2pDvl0jZi4ZWlG8fbdDPO5eMZE8Or8AEJFZhFLK8MGNrXu3-ng-QS6oH_jljbWcseZy62CWp0mu4i1H4vvRRxgml4cp7_vwGqXlY2W8CrqR1dGkoJpGewNgnVYMw1KYzd2pWKdmQH-EjKDYNR0wwxZc-ucQoQA6Q_0xx1DYiJo3-y-FlpEatQEVt_RuUw5WQz-3iL9NklO_qXvc7z3jK_fZoJm20Q199iUOhG1UmerKWOl4w65jLiOmhyal7AHthbaC8N0jZ4wpVCdr84ZDLgYSLNbEXic8FZYxDc4lID1PvVd1FNjDVwhLLp-eRxxfWL6DdWtOWuxBwuIdCIOA1Wflrd0zzF1wFSH32TJ22T1KbOc1-1yJH2rAxbNqFHZaHMRDeqW1pS971CDj9BhaoU-E3udZ69ZB9uYF4SxlshBigH_fbCHdR5Uhi-e4RlWK8W2y2UYfRLq6sHVbjP6qBncEQT0wjS6dyukuyUofkvhSzmwXYyFzd0sfQdiekZOxNRIwmJL_Rb558EiTDF38udITh7itnzEthjfI82Al0Ku27jh3bsavSLTC1PhyWIOLz3py22-g7q9GZFx1-ysgWNQ8vctXwczcB1UERqTXpAMqGqy9aBJJZcMfaDbn07i9980yqc87nxa50bAL99qbDMuD25F30204BesZGIVYYr9ubNyvZl78Ni5UIVsDW97qH2T1VO05sfKf9N_CMHdqF9RmDfjYeqClnPhi5hY84gDYA=w26-h21-no?authuser=0";

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
    public void sendFTPSynErrorEmail(DepoOwnerAccount depoOwnerAccount, List<FtpMoveDTOError> errors) {
        Map<String, String> params = new HashMap<>();
        params.put("fullName", depoOwnerAccount.getFullName());
        String tableErrors = buildTableErrors(errors);
        params.put("tableErrors", tableErrors);
        // get email template content from DB
        EmailTemplateDto activeEmailTemplate = emailTemplateService.findByType(EmailType.FTP_SYNC_ERROR);
        EmailDTO emailDTO = buildEmailDTO(activeEmailTemplate, params, depoOwnerAccount.getCompanyEmail());
        sendSimpleEmail(emailDTO);
    }

    // TODO disable because there is no email for worker invitation process

    /**
     * @Override public void sendDepoWorkerApproveEmail(String workerFullName, String depoOwnerFullName, String email) {
     * Map<String, String> params = new HashMap<>();
     * params.put("workerFullName", workerFullName);
     * params.put("depoOwnerFullName", depoOwnerFullName);
     * // get email template content from DB
     * EmailTemplateDto activeEmailTemplate = emailTemplateService.findByType(EmailType.APPROVE_DEPO_WORKER);
     * EmailDTO emailDTO = buildEmailDTO(activeEmailTemplate, params, email);
     * sendSimpleEmail(emailDTO);
     * }
     **/

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

    @Override
    public void sendResetPassword(DepoOwnerAccount depoOwnerAccount, String token) {
        Map<String, String> params = new HashMap<>();
        params.put("fullname", depoOwnerAccount.getFullName());
        params.put("activeUrl", "https://nle-connect.id/reset-password?token=" + token);
        // get email template content from DB
        EmailTemplateDto activeEmailTemplate = emailTemplateService.findByType(EmailType.RESET_PASSWORD);
        EmailDTO emailDTO = buildEmailDTO(activeEmailTemplate, params, depoOwnerAccount.getCompanyEmail());
        sendSimpleEmail(emailDTO);
    };

    private EmailDTO buildEmailDTO(EmailTemplateDto activeEmailTemplate, Map<String, String> params, String email) {

        //set Image
        params.put("IMAGE_TITLE", "https://api.nle-connect.id/product-nle-connect-uppercase.png");
        params.put("MAIL_IMAGE", "https://api.nle-connect.id/mail.png");
        params.put("PHONE_IMAGE", "https://api.nle-connect.id/phone.png");
        params.put("WEB_IMAGE", "https://api.nle-connect.id/web.png");
        params.put("FACEBOOK_IMAGE", "https://api.nle-connect.id/facebook.png");
        params.put("INSTAGRAM_IMAGE", "https://api.nle-connect.id/template-instagram.png");
        params.put("LINKEDIN_IMAGE", "https://api.nle-connect.id/template-linkedin.png");

        //build emailDTO
        EmailDTO emailDTO = new EmailDTO();
        emailDTO.setFrom(activeEmailTemplate.getSendFrom());
        emailDTO.setTo(email);
        emailDTO.setSubject(activeEmailTemplate.getSubject());
        emailDTO.setTemplateName(activeEmailTemplate.getTemplateName());
        emailDTO.setTemplateContent(activeEmailTemplate.getTemplateContent());
        emailDTO.setModel(params);
        return emailDTO;
    }

    private String buildTableErrors(List<FtpMoveDTOError> errors) {
        StringBuilder errorTable = new StringBuilder();
        errorTable.append("<table>");
        errorTable.append("<th>");
        errorTable.append("<tr>");
        errorTable.append("<td>");
        errorTable.append("Record");
        errorTable.append("</td>");
        errorTable.append("<td>");
        errorTable.append("Errors");
        errorTable.append("</td>");
        errorTable.append("</tr>");
        errorTable.append("</th>");
        errors.forEach(ftpMoveDTOError -> {
            errorTable.append("<tr>");
            errorTable.append("<td>");
            errorTable.append(ftpMoveDTOError.getMoveDTO().toString());
            errorTable.append("</td>");
            errorTable.append("<td>");
            errorTable.append(ftpMoveDTOError.getErrorMessage());
            errorTable.append("</td>");
            errorTable.append("</tr>");
        });
        errorTable.append("</table>");
        return errorTable.toString();
    }
}
