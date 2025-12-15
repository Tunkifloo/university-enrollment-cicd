package com.university.auditservice.consumer;

import com.university.auditservice.dto.AuditEvent;
import com.university.auditservice.service.AuditService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditConsumer {

    private final AuditService auditService;
    private static final String CONSUMER_GROUP_ID = "${spring.kafka.consumer.group-id}";

    @KafkaListener(
            topics = "${kafka.topics.audit}",
            groupId = CONSUMER_GROUP_ID
    )
    public void consumeAuditEvent(
            @Payload AuditEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        try {
            log.info("EVENTO RECIBIDO DE KAFKA");
            log.info("Topic: {}", topic);
            log.info("Partition: {}", partition);
            log.info("Offset: {}", offset);
            log.info("⚡ Action: {}", event.getAction());
            log.info("Details: {}", event.getDetails());

            auditService.logEvent(event);

            log.info("Evento procesado y almacenado exitosamente\n");

        } catch (Exception e) {
            log.error("Error al procesar evento de auditoría: {}", e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(
            topics = "${kafka.topics.user-registered}",
            groupId = CONSUMER_GROUP_ID
    )
    public void consumeUserRegisteredEvent(@Payload AuditEvent event) {
        try {
            log.info("👤 Usuario registrado: {}", event.getUserEmail());
            auditService.logEvent(event);
        } catch (Exception e) {
            log.error("Error procesando registro de usuario: {}", e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(
            topics = "${kafka.topics.faculty-created}",
            groupId = CONSUMER_GROUP_ID
    )
    public void consumeFacultyCreatedEvent(@Payload AuditEvent event) {
        try {
            log.info("Facultad creada: {}", event.getDetails());
            auditService.logEvent(event);
        } catch (Exception e) {
            log.error("Error procesando creación de facultad: {}", e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(
            topics = "${kafka.topics.faculty-updated}",
            groupId = CONSUMER_GROUP_ID
    )
    public void consumeFacultyUpdatedEvent(@Payload AuditEvent event) {
        try {
            log.info("Facultad actualizada: {}", event.getDetails());
            auditService.logEvent(event);
        } catch (Exception e) {
            log.error("Error procesando actualización de facultad: {}", e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(
            topics = "${kafka.topics.faculty-deleted}",
            groupId = CONSUMER_GROUP_ID
    )
    public void consumeFacultyDeletedEvent(@Payload AuditEvent event) {
        try {
            log.info("Facultad eliminada: {}", event.getDetails());
            auditService.logEvent(event);
        } catch (Exception e) {
            log.error("Error procesando eliminación de facultad: {}", e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(
            topics = "${kafka.topics.career-created}",
            groupId = CONSUMER_GROUP_ID
    )
    public void consumeCareerCreatedEvent(@Payload AuditEvent event) {
        try {
            log.info("Carrera creada: {}", event.getDetails());
            auditService.logEvent(event);
        } catch (Exception e) {
            log.error("Error procesando creación de carrera: {}", e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(
            topics = "${kafka.topics.career-updated}",
            groupId = CONSUMER_GROUP_ID
    )
    public void consumeCareerUpdatedEvent(@Payload AuditEvent event) {
        try {
            log.info("Carrera actualizada: {}", event.getDetails());
            auditService.logEvent(event);
        } catch (Exception e) {
            log.error("Error procesando actualización de carrera: {}", e.getMessage(), e);
            throw e;
        }
    }

    @KafkaListener(
            topics = "${kafka.topics.career-deleted}",
            groupId = CONSUMER_GROUP_ID
    )
    public void consumeCareerDeletedEvent(@Payload AuditEvent event) {
        try {
            log.info("Carrera eliminada: {}", event.getDetails());
            auditService.logEvent(event);
        } catch (Exception e) {
            log.error("Error procesando eliminación de carrera: {}", e.getMessage(), e);
            throw e;
        }
    }
}