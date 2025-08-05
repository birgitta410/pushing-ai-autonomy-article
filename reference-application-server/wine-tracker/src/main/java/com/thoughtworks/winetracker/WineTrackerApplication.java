package com.thoughtworks.winetracker;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class WineTrackerApplication {

	public static void main(String[] args) {
		log.info("Putting a change here to create a local change set for testing");
		SpringApplication.run(WineTrackerApplication.class, args);
	}

}
