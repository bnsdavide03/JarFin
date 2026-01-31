package it.unibg.jarfin.analytics_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AnalyticsServiceApplication {

    public static void main(String[] args) {
        // Avvia il microservizio sulla porta definita nelle properties (8081)
        SpringApplication.run(AnalyticsServiceApplication.class, args);
    }
}