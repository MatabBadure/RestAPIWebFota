package com.hillrom.vest.service;
import static com.hillrom.vest.config.NotificationTypeConstants.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import com.hillrom.vest.domain.Clinic;
import com.hillrom.vest.domain.User;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.web.rest.dto.PatientStatsVO;

/**
 * Service for sending e-mails.
 * <p/>
 * <p>
 * We use the @Async annotation to send e-mails asynchronously.
 * </p>
 */
@Service
public class MailService {

    private final Logger log = LoggerFactory.getLogger(MailService.class);

    @Inject
    private Environment env;

    @Inject
    private JavaMailSenderImpl javaMailSender;

    @Inject
    private MessageSource messageSource;

    @Inject
    private SpringTemplateEngine templateEngine;

    /**
     * System default email address that sends the e-mails.
     */
    private String from;
    
    /**
     * Url to be sent in mail notification(stats for Clinic Admin/Hcp) Link
     */
    private String hcpOrClinicAdminDashboardUrl;
    private String careGiverDashboardUrl;
    
    private String patientDashboardUrl;
    
    @PostConstruct
    public void init() {
        this.from = env.getProperty("mail.from");
        this.hcpOrClinicAdminDashboardUrl = env.getProperty("spring.notification.hcpOrClinicAdminDashboardUrl");
        this.careGiverDashboardUrl = env.getProperty("spring.notification.careGiverDashboardUrl");
        this.patientDashboardUrl = env.getProperty("spring.notification.patientDashboardUrl");
    }

    @Async
    public void sendEmail(String[] to, String subject, String content, boolean isMultipart, boolean isHtml) {
        log.debug("Send e-mail[multipart '{}' and html '{}'] to '{}' with subject '{}' and content={}",
                isMultipart, isHtml, to, subject, content);

        // Prepare message using a Spring helper
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8);
            message.setTo(to);
            message.setFrom(from);
            message.setSubject(subject);
            message.setText(content, isHtml);
            javaMailSender.send(mimeMessage);
            log.debug("Sent e-mail to User '{}'", to);
        } catch (Exception e) {
            log.warn("E-mail could not be sent to user '{}', exception is: {}", to, e.getMessage());
        }
    }

    @Async
    public void sendActivationEmail(User user, String baseUrl) {
        log.debug("Sending activation e-mail to '{}'", user.getEmail());
        Locale locale = Locale.forLanguageTag(user.getLangKey());
        Context context = new Context(locale);
        context.setVariable("user", user);
        context.setVariable("baseUrl", baseUrl);
        String content = templateEngine.process("activationEmail", context);
        String subject = messageSource.getMessage("email.activation.title", null, locale);
        sendEmail(new String[]{user.getEmail()}, subject, content, false, true);
    }

    @Async
    public void sendPasswordResetMail(User user, String baseUrl) {
        log.debug("Sending password reset e-mail to '{}'", user.getEmail());
        Locale locale = Locale.forLanguageTag(user.getLangKey());
        Context context = new Context(locale);
        context.setVariable("user", user);
        context.setVariable("baseUrl", baseUrl);
        String content = templateEngine.process("passwordResetEmail", context);
        String subject = messageSource.getMessage("email.reset.title", null, locale);
        sendEmail(new String[]{user.getEmail()}, subject, content, false, true);
    }
    
    public void sendNotificationMailToPatient(User user,String notificationType){
       log.debug("Sending password reset e-mail to '{}'", user.getEmail());
       Context context = new Context();
       context.setVariable("user", user);
       context.setVariable("isMissedTherapyNotification", MISSED_THERAPY.equalsIgnoreCase(notificationType));
       context.setVariable("isHmrNonComplianceNotification", HMR_NON_COMPLIANCE.equalsIgnoreCase(notificationType));
       context.setVariable("isSettingsDeviatedNotification",SETTINGS_DEVIATION.equalsIgnoreCase(notificationType));
       context.setVariable("isHMRNonComplianceAndSettingsNotification", HMR_AND_SETTINGS_DEVIATION.equalsIgnoreCase(notificationType));
       context.setVariable("notificationUrl", patientDashboardUrl);
       String content = "";
       String subject = "";
	   content = templateEngine.process("therapyNotification", context);
       subject = messageSource.getMessage("email.therapynotification.title", null, null);
       sendEmail(new String[]{user.getEmail()}, subject, content, false, true);
    }
    
    public void sendJobFailureNotification(String jobName,String stackTrace){
    	String recipients = env.getProperty("mail.to");
        log.debug("Sending password reset e-mail to '{}'", recipients);
        Context context = new Context();
        context.setVariable("environment", env.getProperty("spring.profiles.active"));
        context.setVariable("jobName", jobName);
        context.setVariable("stackTrace", stackTrace);
        String content = "";
        String subject = "";
        content = templateEngine.process("jobFailureNotification", context);
        subject = messageSource.getMessage("email.jobfailure.subject", null, null);
        sendEmail(recipients.split(","), subject, content, false, true);
     }
    
    public void sendNotificationMailToHCPAndClinicAdmin(User user,Map<String,Map<String,Integer>> statistics){
    	log.debug("Sending password reset e-mail to '{}'", user.getEmail());
        Context context = new Context();
        context.setVariable("user", user);
        context.setVariable("clinicStatisticsMap",statistics);
        context.setVariable("today", DateUtil.convertLocalDateToStringFromat(org.joda.time.LocalDate.now(), "MMM dd,yyyy"));
        context.setVariable("notificationUrl", hcpOrClinicAdminDashboardUrl);
        String content = "";
        String subject = "";

        content = templateEngine.process("statisticsNotification", context);
        subject = messageSource.getMessage("email.statisticsnotification.subject", null, null);
        
        sendEmail(new String[]{user.getEmail()}, subject, content, false, true);
    }
    
    public void sendNotificationCareGiver(String email, String careGiverName,  List<PatientStatsVO> statistics){
    	log.debug("Sending password reset e-mail to '{}'", email);
        Context context = new Context();
        context.setVariable("cgName", careGiverName);
        context.setVariable("patientsStatisticsList",statistics);
        context.setVariable("today", DateUtil.convertLocalDateToStringFromat(org.joda.time.LocalDate.now(), "MMM dd,yyyy"));
        context.setVariable("notificationUrl", careGiverDashboardUrl);
        String content = "";
        String subject = "";

        content = templateEngine.process("careGiverStatisticsNotification", context);
        subject = messageSource.getMessage("email.statisticsnotification.subject", null, null);
        
        sendEmail(new String[]{careGiverDashboardUrl}, subject, content, false, true);
    }
}
