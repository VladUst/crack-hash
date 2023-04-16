package com.example.crackhash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class CrackhashApplication {

    public static void main(String[] args) {
        SpringApplication.run(CrackhashApplication.class, args);
    }

}
