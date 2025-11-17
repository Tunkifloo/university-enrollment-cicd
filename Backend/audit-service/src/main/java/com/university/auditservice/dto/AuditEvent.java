package com.university.auditservice.dto;

import com.university.auditservice.domain.EventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEvent implements Serializable {
    private String eventType;
    private Long userId;
    private String userEmail;
    private String action;
    private String details;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime timestamp;
    private String status;
    private String entityType;
    private Long entityId;
}