package io.backendscience.rinha_backend_2025_java;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class, SecurityAutoConfiguration.class})
public class RinhaBackend2025JavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(RinhaBackend2025JavaApplication.class, args);
    }
}
