package com.cufe.searchengine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class App {
	public static void main(String[] args) {
		new SpringApplication(App.class).run(args);
	}
}
