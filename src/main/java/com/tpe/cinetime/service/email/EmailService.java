package com.tpe.cinetime.service.email;

import com.tpe.cinetime.config.MailProperties;
import com.tpe.cinetime.constants.EmailConstants;
import com.tpe.cinetime.constants.messages.ErrorMessages;
import com.tpe.cinetime.exception.EmailSendException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.hibernate.type.LocalTimeType.FORMATTER;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;
    private final MailProperties mailProperties;
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    //Password Reset Email
    @Async("emailTaskExecutor")
    public void sendPasswordResetEmail(String toEmail, String resetToken) {

        String body = EmailConstants.PASSWORD_RESET_BODY_HTML.formatted(
                resetToken,
                mailProperties.getTokenValidityMinutes()
        );

        sendHtmlEmail(toEmail, EmailConstants.PASSWORD_RESET_SUBJECT, body);
    }

    //Password changed confirmation email
    @Async("emailTaskExecutor")
    public void sendPasswordChangedEmail(String toEmail) {

        String changedAt = LocalDateTime.now().format(FORMATTER);
        String body = EmailConstants.PASSWORD_CHANGED_BODY_HTML.formatted(changedAt);

        sendHtmlEmail(toEmail, EmailConstants.PASSWORD_CHANGED_SUBJECT, body);
    }

    //Helpers
    private void sendHtmlEmail(String toEmail, String subject, String htmlBody) {

        log.info("[EmailService] Sending HTML email to {}", toEmail);

        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(mailProperties.getFrom());
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);

            javaMailSender.send(message);
            log.info("[EmailService] Email sent → {} | Subject: {}", toEmail, subject);

        } catch (MessagingException | MailException e) {
            log.error("[EmailService] Failed to send email → {} | Reason: {}", toEmail, e.getMessage());
            throw new EmailSendException(ErrorMessages.EMAIL_SEND_FAILED + toEmail, e);
        }
    }

}


