package com.smartclassroom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SmartclassroomApplication {

    public static void main(String[] args) {

        SpringApplication.run(
                SmartclassroomApplication.class,
                args);
    }
}