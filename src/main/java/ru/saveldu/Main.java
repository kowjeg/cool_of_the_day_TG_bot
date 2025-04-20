package ru.saveldu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "ru.saveldu")
@EnableJpaRepositories(basePackages = "ru.saveldu.repositories")
@EntityScan(basePackages = "ru.saveldu.entities")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}