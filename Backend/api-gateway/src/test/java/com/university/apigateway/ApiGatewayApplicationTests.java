package com.university.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        properties = {
                "spring.cloud.config.enabled=false",
                "spring.cloud.compatibility-verifier.enabled=false",
                "eureka.client.enabled=false",
                "jwt.secret=test-secret-key-for-testing-purposes-only",
                "jwt.expiration=3600000",
                "server.port=0"
        }
)
class ApiGatewayApplicationTests {

    @Test
    void contextLoads() {
    }
}