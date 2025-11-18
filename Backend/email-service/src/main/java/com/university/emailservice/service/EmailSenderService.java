package com.university.emailservice.service;

import com.university.emailservice.dto.EmailMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class EmailSenderService {

    private final JavaMailSender mailSender;

    @Value("${email.simulation-mode:true}")
    private boolean simulationMode;

    @Value("${email.enabled:true}")
    private boolean emailEnabled;

    @Value("${smtp.from:noreply@university.com}")
    private String fromEmail;

    public EmailSenderService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(EmailMessage message) {
        if (!emailEnabled) {
            log.info("Email deshabilitado. No se envió email a: {}", message.getTo());
            return;
        }

        if (simulationMode) {
            simulateEmailSending(message);
        } else {
            sendRealEmail(message);
        }
    }

    private void simulateEmailSending(EmailMessage message) {
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        log.info("═══════════════════════════════════════════════════");
        log.info("📧 SIMULACIÓN DE ENVÍO DE EMAIL");
        log.info("═══════════════════════════════════════════════════");
        log.info("⏰ Timestamp: {}", timestamp);
        log.info("📨 Para: {}", message.getTo());
        log.info("👤 Usuario: {}", message.getUserName());
        log.info("📋 Asunto: {}", message.getSubject());
        log.info("📝 Mensaje:");
        log.info("---------------------------------------------------");
        log.info("{}", message.getBody());
        log.info("---------------------------------------------------");
        log.info("Email simulado enviado exitosamente");
        log.info("═══════════════════════════════════════════════════\n");
    }

    private void sendRealEmail(EmailMessage message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(fromEmail);
            mailMessage.setTo(message.getTo());
            mailMessage.setSubject(message.getSubject());
            mailMessage.setText(message.getBody());

            mailSender.send(mailMessage);

            log.info("✅ Email REAL enviado exitosamente a: {}", message.getTo());
        } catch (Exception e) {
            log.error("🚨 Error enviando email real a {}: {}", message.getTo(), e.getMessage());
            // Fallback a simulación
            simulateEmailSending(message);
        }
    }
}