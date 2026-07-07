package com.ats.postulaciones;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PostulacionesApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostulacionesApplication.class, args);
    }
}
