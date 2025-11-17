package com.university.auditservice.service;

import com.university.auditservice.domain.AuditLog;
import com.university.auditservice.domain.EventType;
import com.university.auditservice.dto.AuditEvent;
import com.university.auditservice.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;

    @Transactional
    public void logEvent(AuditEvent event) {
        log.info("Registrando evento de auditoría: {} - {}",
                event.getEventType(), event.getAction());

        EventType eventType = EventType.valueOf(event.getEventType());

        AuditLog auditLog = AuditLog.builder()
                .eventType(eventType)
                .userId(event.getUserId())
                .userEmail(event.getUserEmail())
                .action(event.getAction())
                .details(event.getDetails())
                .ipAddress(event.getIpAddress())
                .userAgent(event.getUserAgent())
                .timestamp(event.getTimestamp() != null ? event.getTimestamp() : LocalDateTime.now())
                .status(event.getStatus())
                .entityType(event.getEntityType())
                .entityId(event.getEntityId())
                .build();

        auditLogRepository.save(auditLog);

        log.info("Evento registrado exitosamente: ID {}", auditLog.getId());
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogsByEventType(EventType eventType) {
        return auditLogRepository.findByEventType(eventType);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogsByUser(Long userId) {
        return auditLogRepository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogsByEmail(String email) {
        return auditLogRepository.findByUserEmail(email);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getAuditLogsByDateRange(LocalDateTime start, LocalDateTime end) {
        return auditLogRepository.findByTimestampBetween(start, end);
    }

    @Transactional(readOnly = true)
    public List<AuditLog> getAllAuditLogs() {
        return auditLogRepository.findAll();
    }
}