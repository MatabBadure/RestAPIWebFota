package com.hillrom.vest.service;

import com.hillrom.vest.config.NotificationTypeConstants;
import com.hillrom.vest.domain.User;

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

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.mail.internet.MimeMessage;

import java.util.Locale;

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

    @PostConstruct
    public void init() {
        this.from = env.getProperty("mail.from");
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
    
    public void sendNotificationMail(User user,String notificationType){
       log.debug("Sending password reset e-mail to '{}'", user.getEmail());
       Context context = new Context();
       context.setVariable("user", user);
       String content = "";
       String subject = "";
       if(NotificationTypeConstants.MISSED_THERAPY.equalsIgnoreCase(notificationType)){
    	   content = templateEngine.process("missedTherapyNotification", context);
           subject = messageSource.getMessage("email.therapynotification.title", null, null);
       }else if(NotificationTypeConstants.HMR_NON_COMPLIANCE.equalsIgnoreCase(notificationType)){
    	   content = templateEngine.process("hmrComplianceNotification", context);
           subject = messageSource.getMessage("email.hmrnotification.title", null, null);
       }else {
    	   content = templateEngine.process("settingsNotification", context);
           subject = messageSource.getMessage("email.settingsnotification.title", null, null);
       }
       sendEmail(new String[]{user.getEmail()}, subject, content, false, true);
    }
    
    public void sendJobFailureNotification(String jobName,String stackTrace){
    	String recipients = env.getProperty("mail.to");
        log.debug("Sending password reset e-mail to '{}'", recipients);
        Context context = new Context();
        context.setVariable("jobName", jobName);
        context.setVariable("stackTrace", stackTrace);
        String content = "";
        String subject = "";
        content = templateEngine.process("jobFailureNotification", context);
        subject = messageSource.getMessage("email.jobfailure.subject", null, null);
        sendEmail(recipients.split(","), subject, content, false, true);
     }
}
