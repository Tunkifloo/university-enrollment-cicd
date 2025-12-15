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
    private static final String STATUS_SUCCESS = "SUCCESS";
    private static final String ENTITY_FACULTY = "FACULTY";
    private static final String ENTITY_CAREER = "CAREER";
    private static final String EVENT_FACULTY_CREATED = "FACULTY_CREATED";
    private static final String EVENT_FACULTY_UPDATED = "FACULTY_UPDATED";
    private static final String EVENT_FACULTY_DELETED = "FACULTY_DELETED";
    private static final String EVENT_CAREER_CREATED = "CAREER_CREATED";
    private static final String EVENT_CAREER_UPDATED = "CAREER_UPDATED";
    private static final String EVENT_CAREER_DELETED = "CAREER_DELETED";

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
     * Obtiene el email del usuario autenticado
     */
    private String getAuthenticatedUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated()) ? auth.getName() : "system";
    }

    /**
     * Construye y publica el evento evitando la duplicación de código.
     */
    private void createAndPublishEvent(String specificTopic, String eventType, String action,
                                       String details, String entityType, Long entityId) {
        try {
            String userEmail = getAuthenticatedUserEmail();

            AuditEvent event = AuditEvent.builder()
                    .eventType(eventType)
                    .userEmail(userEmail)
                    .action(action)
                    .details(details)
                    .timestamp(LocalDateTime.now())
                    .status(STATUS_SUCCESS)
                    .entityType(entityType)
                    .entityId(entityId)
                    .build();

            // Publicar en topics
            kafkaTemplate.send(specificTopic, event);
            kafkaTemplate.send(auditTopic, event);

            log.info("Evento {} publicado. Entidad: {} ID: {} por usuario: {}",
                    eventType, entityType, entityId, userEmail);

        } catch (Exception e) {
            log.error("Error al publicar evento {}: {}", eventType, e.getMessage());
        }
    }

    // --- FACULTADES ---

    public void publishFacultyCreatedEvent(Long facultyId, String facultyName) {
        createAndPublishEvent(facultyCreatedTopic, EVENT_FACULTY_CREATED, "Facultad creada",
                "Nueva facultad: " + facultyName, ENTITY_FACULTY, facultyId);
    }

    public void publishFacultyUpdatedEvent(Long facultyId, String facultyName) {
        createAndPublishEvent(facultyUpdatedTopic, EVENT_FACULTY_UPDATED, "Facultad actualizada",
                "Facultad actualizada: " + facultyName, ENTITY_FACULTY, facultyId);
    }

    public void publishFacultyDeletedEvent(Long facultyId, String facultyName) {
        createAndPublishEvent(facultyDeletedTopic, EVENT_FACULTY_DELETED, "Facultad eliminada",
                "Facultad eliminada: " + facultyName, ENTITY_FACULTY, facultyId);
    }

    // --- CARRERAS ---

    public void publishCareerCreatedEvent(Long careerId, String careerName, Long facultyId, String facultyName) {
        String details = String.format("Nueva carrera: %s en facultad: %s", careerName, facultyName);
        createAndPublishEvent(careerCreatedTopic, EVENT_CAREER_CREATED, "Carrera creada",
                details, ENTITY_CAREER, careerId);
    }

    public void publishCareerUpdatedEvent(Long careerId, String careerName, Long facultyId, String facultyName) {
        String details = String.format("Carrera actualizada: %s en facultad: %s", careerName, facultyName);
        createAndPublishEvent(careerUpdatedTopic, EVENT_CAREER_UPDATED, "Carrera actualizada",
                details, ENTITY_CAREER, careerId);
    }

    public void publishCareerDeletedEvent(Long careerId, String careerName) {
        createAndPublishEvent(careerDeletedTopic, EVENT_CAREER_DELETED, "Carrera eliminada",
                "Carrera eliminada: " + careerName, ENTITY_CAREER, careerId);
    }
}