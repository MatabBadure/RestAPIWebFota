package com.hillrom.vest.service;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_AND_SETTINGS_DEVIATION;
import static com.hillrom.vest.config.NotificationTypeConstants.HMR_NON_COMPLIANCE;
import static com.hillrom.vest.config.NotificationTypeConstants.MISSED_THERAPY;
import static com.hillrom.vest.config.NotificationTypeConstants.SETTINGS_DEVIATION;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang.CharEncoding;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import com.hillrom.vest.domain.User;
import com.hillrom.vest.repository.UserRepository;
import com.hillrom.vest.service.util.DateUtil;
import com.hillrom.vest.web.rest.dto.CareGiverStatsNotificationVO;
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
    private UserRepository userRepository;

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
    private String baseUrl;
    
    private Integer accountActivationReminderInterval;
    
    @PostConstruct
    public void init() {
        this.from = env.getProperty("mail.from");
        this.hcpOrClinicAdminDashboardUrl = env.getProperty("spring.notification.hcpOrClinicAdminDashboardUrl");
        this.careGiverDashboardUrl = env.getProperty("spring.notification.careGiverDashboardUrl");
        this.patientDashboardUrl = env.getProperty("spring.notification.patientDashboardUrl");
        this.baseUrl = env.getProperty("spring.notification.baseUrl");
        this.accountActivationReminderInterval =
        		Integer.parseInt(env.getProperty("spring.notification.accountActivationReminderInterval"));
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
        Locale locale = getLocale(user);
        Context context = new Context(locale);
        context.setVariable("user", userNameFormatting(user));
        context.setVariable("baseUrl", baseUrl);
        String content = templateEngine.process("activationEmail", context);
        String subject = messageSource.getMessage("email.activation.title", null, locale);
        sendEmail(new String[]{user.getEmail()}, subject, content, false, true);
    }

    @Async
    public void reSendActivationEmail(User user, String baseUrl) {
        log.debug("Resending activation e-mail to '{}'", user.getEmail());
        Locale locale = getLocale(user);
        Context context = new Context(locale);
        context.setVariable("user", userNameFormatting(user));
        context.setVariable("baseUrl", baseUrl);
        String content = templateEngine.process("activationEmail", context);
        String subject = messageSource.getMessage("email.reactivation.title", null, locale);
        sendEmail(new String[]{user.getEmail()}, subject, content, false, true);
    }

    @Async
    public void sendPasswordResetMail(User user, String baseUrl) {
        log.debug("Sending password reset e-mail to '{}'", user.getEmail());
        Locale locale = getLocale(user);
        Context context = new Context(locale);
        context.setVariable("user", user);
        context.setVariable("baseUrl", baseUrl);
        String content = templateEngine.process("passwordResetEmail", context);
        String subject = messageSource.getMessage("email.reset.title", null, locale);
        sendEmail(new String[]{user.getEmail()}, subject, content, false, true);
    }
    
    public void sendNotificationMailToPatient(User user,String notificationType,int missedTherapyCount){
       log.debug("Sending password reset e-mail to '{}'", user.getEmail());
       Context context = new Context();
       context.setVariable("user", user);
       context.setVariable("missedTherapyCount", missedTherapyCount);
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
        context.setVariable("today", DateUtil.convertLocalDateToStringFromat(org.joda.time.LocalDate.now().minusDays(1), "MMM dd,yyyy"));
        context.setVariable("notificationUrl", hcpOrClinicAdminDashboardUrl);
        String content = "";
        String subject = "";

        content = templateEngine.process("statisticsNotification", context);
        subject = messageSource.getMessage("email.statisticsnotification.subject", null, null);
        
        sendEmail(new String[]{user.getEmail()}, subject, content, false, true);
    }
    
    public void sendNotificationCareGiver(CareGiverStatsNotificationVO careGiverStatsNotificationVO,  List<PatientStatsVO> statistics){
    	log.debug("Sending care giver statistics e-mail to '{}'", careGiverStatsNotificationVO.getCGEmail());
        Context context = new Context();
        context.setVariable("careGiverStatsNotificationVO", careGiverStatsNotificationVO);
        context.setVariable("patientsStatisticsList",statistics);
        context.setVariable("isMultiplePatients",statistics.size()>1?true:false);
        log.debug("statistics patient size {}", statistics.size());
        
        context.setVariable("today", DateUtil.convertLocalDateToStringFromat(org.joda.time.LocalDate.now().minusDays(1), "MMM dd,yyyy"));
        context.setVariable("notificationUrl", careGiverDashboardUrl);
        String content = "";
        String subject = "";

        content = templateEngine.process("careGiverStatisticsNotification", context);
        subject = messageSource.getMessage("email.statisticsnotification.subject", null, null);
        
        sendEmail(new String[]{careGiverStatsNotificationVO.getCGEmail()}, subject, content, false, true);
    }
    
    @Async
    public void sendActivationReminderEmail(User user) {
        log.debug("Sending activation Reminder e-mail to '{}'", user.getEmail());
        Locale locale = getLocale(user);
        Context context = new Context(locale);
        context.setVariable("user", userNameFormatting(user));
        context.setVariable("notificationUrl", baseUrl);
        String content = templateEngine.process("activationReminderEmail", context);
        String subject = messageSource.getMessage("email.reactivation.title", null, locale);
        sendEmail(new String[]{user.getEmail()}, subject, content, false, true);
    }
    
    @Scheduled(cron="0 0 * * * *")
	@Async
	public void activationReminderEmail(){
    	try{
			DateTime currectTime =  new DateTime();
			for(int interval = accountActivationReminderInterval; interval < 48; interval += accountActivationReminderInterval)
				getUsersActivationReminderEmail(currectTime.minusHours(interval).minusHours(1),currectTime.minusHours(interval));
	    }catch(Exception ex){
			StringWriter writer = new StringWriter();
			PrintWriter printWriter = new PrintWriter( writer );
			ex.printStackTrace( printWriter );
			sendJobFailureNotification("activationReminderEmail",writer.toString());
		}
	}
    
	private void getUsersActivationReminderEmail(DateTime fromTime,DateTime toTime){
	    List<User> users = userRepository.findAllByActivatedIsFalseAndActivationLinkSentDateBetweeen(fromTime, toTime);
	    log.debug("No of users ", users);
	    for(User user : users){
	    	sendActivationReminderEmail(user);
	    }
	}
    
	private User userNameFormatting(User user){
		user.setFirstName(userNameStringFormatting(user.getFirstName()));
		user.setLastName(userNameStringFormatting(user.getLastName()));
		return user;
	}
	
	private String userNameStringFormatting(String userName){
		return userName.substring(0, 1).toUpperCase() + userName.substring(1).toLowerCase();
	}

	/**
	 *  Returns default locale if user preference is null
	 * @param user
	 * @return
	 */
	private Locale getLocale(User user) {
		String langKey =  user.getLangKey() ;
        if(Objects.isNull(langKey)){
        	langKey = "en";
        }
		return Locale.forLanguageTag(langKey);
	}
	
	public void sendStatusOnDataIngestionRequest(String rawData,String status,boolean isFailed,String stackTrace){
    	String recipients = env.getProperty("mail.to");
        log.debug("Sending Ingestion Request Status '{}'", recipients);
        Context context = new Context();
        context.setVariable("environment", env.getProperty("spring.profiles.active"));
        context.setVariable("today", DateUtil.convertLocalDateToStringFromat(org.joda.time.LocalDate.now(), "MMM dd,yyyy"));
        context.setVariable("rawData", rawData);
        context.setVariable("status", status);
        context.setVariable("stackTrace", stackTrace);
        context.setVariable("isFailed", isFailed);
        String content = "";
        String subject = "";
        content = templateEngine.process("ingestionProcessStatus", context);
        subject = messageSource.getMessage("email.ingestionprocessstatus.subject", new String[]{status}, null);
        sendEmail(recipients.split(","), subject, content, false, true);
     }
}
