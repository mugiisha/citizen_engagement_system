package org.amir.ces.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.amir.ces.model.Ticket;
import org.amir.ces.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        try {
            mailSender.send(message);
            log.info("Simple email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send simple email to: {}", to, e);
        }
    }


    public void sendTicketCreatedEmail(Ticket ticket) {
        String subject = "Ticket Submission Confirmation";
        Context context = new Context();
        context.setVariable("ticketId", ticket.getReferenceNumber());
        context.setVariable("tags", ticket.getTag().getName());
        context.setVariable("assignedAgency", ticket.getAssignedAgency().getName());
        context.setVariable("submittedDate", ticket.getCreatedAt());

        String htmlContent = templateEngine.process("ticket-created", context);
        sendHtmlEmail(ticket.getIssuerEmail(), subject, htmlContent);
    }

    public void sendIncomingTicket(Ticket ticket, List<String> managerEmails) {
        String subject = "New Ticket";
        Context context = new Context();
        context.setVariable("ticket", ticket);
        String htmlContent = templateEngine.process("ticket-created-manager", context);

        for (String email : managerEmails) {
            sendHtmlEmail(email, subject, htmlContent);
        }
    }

    public void sendTicketUpdate(Ticket ticket, String email) {
        String subject = "Ticket Updated";
        Context context = new Context();
        context.setVariable("ticketId", ticket.getReferenceNumber());
        String htmlContent = templateEngine.process("ticket-response", context);

        sendHtmlEmail(email, subject, htmlContent);

    }


    public void sendInvitationEmail(User user, String password) {
        String subject = "Welcome to Join Citizen Engagement System";
        Context context = new Context();
        context.setVariable("role", user.getRole());
        context.setVariable("agencyName", user.getAgency().getName());
        context.setVariable("userEmail", user.getEmail());
        context.setVariable("password", password);

        String htmlContent = templateEngine.process("invitation-email", context);
        sendHtmlEmail(user.getEmail(), subject, htmlContent);
    }


    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("HTML email sent to: {}", to);
        } catch (MessagingException e) {
            log.error("Failed to send HTML email to: {}", to, e);
        }
    }
}
