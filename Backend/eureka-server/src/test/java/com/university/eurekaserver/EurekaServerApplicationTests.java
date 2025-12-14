package com.university.eurekaserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
                "spring.cloud.config.enabled=false",
                "spring.cloud.compatibility-verifier.enabled=false",
                "eureka.client.enabled=false",
                "eureka.client.register-with-eureka=false",
                "eureka.client.fetch-registry=false"
        }
)
@EnableAutoConfiguration(exclude = {
        org.springframework.cloud.netflix.eureka.server.EurekaServerAutoConfiguration.class
})
class EurekaServerApplicationTests {

    @Test
    void contextLoads() {
    }
}