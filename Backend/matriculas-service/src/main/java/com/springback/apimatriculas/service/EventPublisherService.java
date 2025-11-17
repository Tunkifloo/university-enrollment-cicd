package com.springback.apimatriculas.service;

import com.springback.apimatriculas.dto.kafka.AuditEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventPublisherService {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.audit}")
    private String auditTopic;

    @Value("${kafka.topics.faculty-created}")
    private String facultyCreatedTopic;

    @Value("${kafka.topics.faculty-updated}")
    private String facultyUpdatedTopic;

    @Value("${kafka.topics.faculty-deleted}")
    private String facultyDeletedTopic;

    @Value("${kafka.topics.career-created}")
    private String careerCreatedTopic;

    @Value("${kafka.topics.career-updated}")
    private String careerUpdatedTopic;

    @Value("${kafka.topics.career-deleted}")
    private String careerDeletedTopic;

    /**
     * Obtiene el email del usuario autenticado desde el contexto de seguridad
     */
    private String getAuthenticatedUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated()) ? auth.getName() : "system";
    }

    // ==================== EVENTOS DE FACULTAD ====================

    public void publishFacultyCreatedEvent(Long facultyId, String facultyName) {
        try {
            String userEmail = getAuthenticatedUserEmail();

            AuditEvent event = AuditEvent.builder()
                    .eventType("FACULTY_CREATED")
                    .userEmail(userEmail)
                    .action("Facultad creada")
                    .details("Nueva facultad: " + facultyName)
                    .timestamp(LocalDateTime.now())
                    .status("SUCCESS")
                    .entityType("FACULTY")
                    .entityId(facultyId)
                    .build();

            // Publicar en el topic específico de facultades
            kafkaTemplate.send(facultyCreatedTopic, event);

            // Publicar también en el topic general de auditoría
            kafkaTemplate.send(auditTopic, event);

            log.info("Evento FACULTY_CREATED publicado para facultad: {} por usuario: {}",
                    facultyName, userEmail);
        } catch (Exception e) {
            log.error("Error al publicar evento de facultad creada: {}", e.getMessage());
        }
    }

    public void publishFacultyUpdatedEvent(Long facultyId, String facultyName) {
        try {
            String userEmail = getAuthenticatedUserEmail();

            AuditEvent event = AuditEvent.builder()
                    .eventType("FACULTY_UPDATED")
                    .userEmail(userEmail)
                    .action("Facultad actualizada")
                    .details("Facultad actualizada: " + facultyName)
                    .timestamp(LocalDateTime.now())
                    .status("SUCCESS")
                    .entityType("FACULTY")
                    .entityId(facultyId)
                    .build();

            kafkaTemplate.send(facultyUpdatedTopic, event);
            kafkaTemplate.send(auditTopic, event);

            log.info("Evento FACULTY_UPDATED publicado para facultad: {} por usuario: {}",
                    facultyName, userEmail);
        } catch (Exception e) {
            log.error("Error al publicar evento de facultad actualizada: {}", e.getMessage());
        }
    }

    public void publishFacultyDeletedEvent(Long facultyId, String facultyName) {
        try {
            String userEmail = getAuthenticatedUserEmail();

            AuditEvent event = AuditEvent.builder()
                    .eventType("FACULTY_DELETED")
                    .userEmail(userEmail)
                    .action("Facultad eliminada")
                    .details("Facultad eliminada: " + facultyName)
                    .timestamp(LocalDateTime.now())
                    .status("SUCCESS")
                    .entityType("FACULTY")
                    .entityId(facultyId)
                    .build();

            kafkaTemplate.send(facultyDeletedTopic, event);
            kafkaTemplate.send(auditTopic, event);

            log.info("Evento FACULTY_DELETED publicado para facultad: {} por usuario: {}",
                    facultyName, userEmail);
        } catch (Exception e) {
            log.error("Error al publicar evento de facultad eliminada: {}", e.getMessage());
        }
    }

    // ==================== EVENTOS DE CARRERA ====================

    public void publishCareerCreatedEvent(Long careerId, String careerName, Long facultyId, String facultyName) {
        try {
            String userEmail = getAuthenticatedUserEmail();

            AuditEvent event = AuditEvent.builder()
                    .eventType("CAREER_CREATED")
                    .userEmail(userEmail)
                    .action("Carrera creada")
                    .details(String.format("Nueva carrera: %s en facultad: %s", careerName, facultyName))
                    .timestamp(LocalDateTime.now())
                    .status("SUCCESS")
                    .entityType("CAREER")
                    .entityId(careerId)
                    .build();

            kafkaTemplate.send(careerCreatedTopic, event);
            kafkaTemplate.send(auditTopic, event);

            log.info("Evento CAREER_CREATED publicado para carrera: {} en facultad: {} por usuario: {}",
                    careerName, facultyName, userEmail);
        } catch (Exception e) {
            log.error("Error al publicar evento de carrera creada: {}", e.getMessage());
        }
    }

    public void publishCareerUpdatedEvent(Long careerId, String careerName, Long facultyId, String facultyName) {
        try {
            String userEmail = getAuthenticatedUserEmail();

            AuditEvent event = AuditEvent.builder()
                    .eventType("CAREER_UPDATED")
                    .userEmail(userEmail)
                    .action("Carrera actualizada")
                    .details(String.format("Carrera actualizada: %s en facultad: %s", careerName, facultyName))
                    .timestamp(LocalDateTime.now())
                    .status("SUCCESS")
                    .entityType("CAREER")
                    .entityId(careerId)
                    .build();

            kafkaTemplate.send(careerUpdatedTopic, event);
            kafkaTemplate.send(auditTopic, event);

            log.info("Evento CAREER_UPDATED publicado para carrera: {} en facultad: {} por usuario: {}",
                    careerName, facultyName, userEmail);
        } catch (Exception e) {
            log.error("Error al publicar evento de carrera actualizada: {}", e.getMessage());
        }
    }

    public void publishCareerDeletedEvent(Long careerId, String careerName) {
        try {
            String userEmail = getAuthenticatedUserEmail();

            AuditEvent event = AuditEvent.builder()
                    .eventType("CAREER_DELETED")
                    .userEmail(userEmail)
                    .action("Carrera eliminada")
                    .details("Carrera eliminada: " + careerName)
                    .timestamp(LocalDateTime.now())
                    .status("SUCCESS")
                    .entityType("CAREER")
                    .entityId(careerId)
                    .build();

            kafkaTemplate.send(careerDeletedTopic, event);
            kafkaTemplate.send(auditTopic, event);

            log.info("Evento CAREER_DELETED publicado para carrera: {} por usuario: {}",
                    careerName, userEmail);
        } catch (Exception e) {
            log.error("Error al publicar evento de carrera eliminada: {}", e.getMessage());
        }
    }
}