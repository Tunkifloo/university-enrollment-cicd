package com.university.auditservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${kafka.topics.audit}")
    private String auditTopic;

    @Value("${kafka.topics.user-registered}")
    private String userRegisteredTopic;

    @Value("${kafka.topics.faculty-created}")
    private String facultyCreatedTopic;

    // ==================== TOPIC CREATION ====================
    @Bean
    public NewTopic auditTopic() {
        return TopicBuilder.name(auditTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic userRegisteredTopic() {
        return TopicBuilder.name(userRegisteredTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic facultyCreatedTopic() {
        return TopicBuilder.name(facultyCreatedTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}