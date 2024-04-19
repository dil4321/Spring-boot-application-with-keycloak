package com.example.kcapplication;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KcApplication {
	private static String TIME_ZONE;

	@Value("${spring.jpa.properties.hibernate.jdbc.time_zone}")
	public void setNameStatic(String name){
		KcApplication.TIME_ZONE = name;
	}

	public static void main(String[] args) {
		SpringApplication.run(KcApplication.class, args);
	}

}
