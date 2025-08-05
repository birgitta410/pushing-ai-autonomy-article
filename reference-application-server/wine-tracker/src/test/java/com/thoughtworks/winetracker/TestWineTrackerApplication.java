package com.thoughtworks.winetracker;

import org.springframework.boot.SpringApplication;

public class TestWineTrackerApplication {

	public static void main(String[] args) {
		SpringApplication.from(WineTrackerApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
