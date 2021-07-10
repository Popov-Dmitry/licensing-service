package com.github.popovdmitry.informationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class InformationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InformationServiceApplication.class, args);
    }

}
