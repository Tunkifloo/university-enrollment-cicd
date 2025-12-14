package com.university.emailservice.service;

import com.university.emailservice.dto.EmailMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailSenderServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailSenderService emailSenderService;

    private EmailMessage emailMessage;

    @BeforeEach
    void setUp() {
        emailMessage = EmailMessage.builder()
                .to("test@example.com")
                .subject("Test Subject")
                .body("Test Body")
                .userName("Test User")
                .build();

        ReflectionTestUtils.setField(emailSenderService, "simulationMode", true);
        ReflectionTestUtils.setField(emailSenderService, "emailEnabled", true);
        ReflectionTestUtils.setField(emailSenderService, "fromEmail", "noreply@test.com");
    }

    @Test
    void sendEmail_WhenEmailDisabled_DoesNotSendEmail() {
        ReflectionTestUtils.setField(emailSenderService, "emailEnabled", false);

        emailSenderService.sendEmail(emailMessage);

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_InSimulationMode_DoesNotSendRealEmail() {
        ReflectionTestUtils.setField(emailSenderService, "simulationMode", true);

        emailSenderService.sendEmail(emailMessage);

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_NotInSimulationMode_SendsRealEmail() {
        ReflectionTestUtils.setField(emailSenderService, "simulationMode", false);
        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailSenderService.sendEmail(emailMessage);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_WithException_FallsBackToSimulation() {
        ReflectionTestUtils.setField(emailSenderService, "simulationMode", false);
        doThrow(new RuntimeException("Mail server error")).when(mailSender).send(any(SimpleMailMessage.class));

        emailSenderService.sendEmail(emailMessage);

        verify(mailSender).send(any(SimpleMailMessage.class));
    }
}