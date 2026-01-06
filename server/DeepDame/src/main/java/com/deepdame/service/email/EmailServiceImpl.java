package com.deepdame.service.email;

import com.deepdame.properties.EmailSenderProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    public static AtomicInteger emailSent = new AtomicInteger(0);
    private final Integer maxPerDay = 450;
    private final Queue<EmailSendRequest> emailQueue = new ConcurrentLinkedQueue<>();

    private final JavaMailSender javaMailSender;
    private final EmailSenderProperties emailSenderProperties;
    private final TemplateEngine templateEngine;

    public void sendEmail(@Email String to, String subject, Context context, String template) {
        if (emailSent.get() < maxPerDay) {
            // processing the template
            String htmlContent = templateEngine.process(template, context);

            // sending the email
            MimeMessage message = javaMailSender.createMimeMessage();
            try {
                MimeMessageHelper helper = new MimeMessageHelper(message, true, "utf-8");
                helper.setFrom(emailSenderProperties.sendingEmail());
                helper.setTo(to);
                helper.setSubject(subject);
                helper.setText(htmlContent, true);

                // sending the email
                javaMailSender.send(message);
                emailSent.incrementAndGet();
                log.info("Email sent to {}", to);
            } catch (MailException e) {
                log.error(e.getMessage());
                // in case the email fails to send, we'll send it later
                emailQueue.add(EmailSendRequest.builder().email(to).subject(subject).context(context).template(template).build());
            } catch (MessagingException e) {
                log.error(e.getMessage());
            }
        } else {
            // in case we exceeded the limit, we'll send the rest of the emails later
            emailQueue.add(EmailSendRequest.builder().email(to).subject(subject).context(context).template(template).build());
        }
    }

    @Async
    public void passwordForgottenEmail(String email, Integer validationCode) {
        Context context = new Context();
        context.setVariable("validationCode", validationCode);
        sendEmail(email, "DEEPDAME account password recovery", context, "email/password-forgotten.html");
    }
}
