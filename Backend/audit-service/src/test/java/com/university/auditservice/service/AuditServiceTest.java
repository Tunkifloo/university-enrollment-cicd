package com.university.auditservice.service;

import com.university.auditservice.domain.AuditLog;
import com.university.auditservice.domain.EventType;
import com.university.auditservice.dto.AuditEvent;
import com.university.auditservice.repository.AuditLogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuditServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditService auditService;

    private AuditEvent auditEvent;
    private AuditLog auditLog;

    @BeforeEach
    void setUp() {
        auditEvent = AuditEvent.builder()
                .eventType("USER_REGISTERED")
                .userId(1L)
                .userEmail("user@test.com")
                .action("Usuario registrado")
                .details("Nuevo usuario registrado: John Doe")
                .timestamp(LocalDateTime.now())
                .status("SUCCESS")
                .entityType("USER")
                .entityId(1L)
                .build();

        auditLog = AuditLog.builder()
                .id(1L)
                .eventType(EventType.USER_REGISTERED)
                .userId(1L)
                .userEmail("user@test.com")
                .action("Usuario registrado")
                .details("Nuevo usuario registrado: John Doe")
                .timestamp(LocalDateTime.now())
                .status("SUCCESS")
                .entityType("USER")
                .entityId(1L)
                .build();
    }

    @Test
    void logEvent_Success() {
        when(auditLogRepository.save(any(AuditLog.class))).thenReturn(auditLog);

        auditService.logEvent(auditEvent);

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());

        AuditLog savedLog = captor.getValue();
        assertThat(savedLog.getEventType()).isEqualTo(EventType.USER_REGISTERED);
        assertThat(savedLog.getUserEmail()).isEqualTo("user@test.com");
        assertThat(savedLog.getAction()).isEqualTo("Usuario registrado");
    }

    @Test
    void getAuditLogsByEventType_ReturnsLogs() {
        List<AuditLog> expectedLogs = Arrays.asList(auditLog);
        when(auditLogRepository.findByEventType(EventType.USER_REGISTERED)).thenReturn(expectedLogs);

        List<AuditLog> result = auditService.getAuditLogsByEventType(EventType.USER_REGISTERED);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEventType()).isEqualTo(EventType.USER_REGISTERED);
        verify(auditLogRepository).findByEventType(EventType.USER_REGISTERED);
    }

    @Test
    void getAuditLogsByUser_ReturnsLogs() {
        List<AuditLog> expectedLogs = Arrays.asList(auditLog);
        when(auditLogRepository.findByUserId(1L)).thenReturn(expectedLogs);

        List<AuditLog> result = auditService.getAuditLogsByUser(1L);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(1L);
        verify(auditLogRepository).findByUserId(1L);
    }

    @Test
    void getAuditLogsByEmail_ReturnsLogs() {
        List<AuditLog> expectedLogs = Arrays.asList(auditLog);
        when(auditLogRepository.findByUserEmail("user@test.com")).thenReturn(expectedLogs);

        List<AuditLog> result = auditService.getAuditLogsByEmail("user@test.com");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserEmail()).isEqualTo("user@test.com");
        verify(auditLogRepository).findByUserEmail("user@test.com");
    }

    @Test
    void getAuditLogsByDateRange_ReturnsLogs() {
        LocalDateTime start = LocalDateTime.now().minusDays(7);
        LocalDateTime end = LocalDateTime.now();
        List<AuditLog> expectedLogs = Arrays.asList(auditLog);

        when(auditLogRepository.findByTimestampBetween(start, end)).thenReturn(expectedLogs);

        List<AuditLog> result = auditService.getAuditLogsByDateRange(start, end);

        assertThat(result).hasSize(1);
        verify(auditLogRepository).findByTimestampBetween(start, end);
    }

    @Test
    void getAllAuditLogs_ReturnsAllLogs() {
        List<AuditLog> expectedLogs = Arrays.asList(auditLog);
        when(auditLogRepository.findAll()).thenReturn(expectedLogs);

        List<AuditLog> result = auditService.getAllAuditLogs();

        assertThat(result).hasSize(1);
        verify(auditLogRepository).findAll();
    }
}