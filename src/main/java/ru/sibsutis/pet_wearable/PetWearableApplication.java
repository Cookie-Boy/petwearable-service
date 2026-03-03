package ru.sibsutis.pet_wearable;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PetWearableApplication {

	public static void main(String[] args) {
		SpringApplication.run(PetWearableApplication.class, args);
	}

}
