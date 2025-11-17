package com.springback.apimatriculas.dto.kafka;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEvent {
    private String eventType;
    private Long userId;
    private String userEmail;
    private String action;
    private String details;
    private LocalDateTime timestamp;
    private String status;
    private String entityType;
    private Long entityId;
}