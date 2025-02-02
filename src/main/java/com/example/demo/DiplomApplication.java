package com.example.demo;

import com.example.demo.properties.TaskListProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(TaskListProperties.class)
public class DiplomApplication {
	public static void main(String[] args) {
		SpringApplication.run(DiplomApplication.class, args);
	}

}
