package com.example.worker1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class Worker1Application {

    public static void main(String[] args) {
        SpringApplication.run(Worker1Application.class, args);
    }

}
