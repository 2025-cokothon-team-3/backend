package com.example.cokothon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CokothonApplication {

    public static void main(String[] args) {
        SpringApplication.run(CokothonApplication.class, args);
    }

}
