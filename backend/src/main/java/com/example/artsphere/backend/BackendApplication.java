package com.example.artsphere.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Główna klasa uruchomieniowa aplikacji backendowej.
 */
@SpringBootApplication
public class BackendApplication {
	/**
	 * Konstruktor domyślny.
	 */
	public BackendApplication() {}

	/**
	 * Główna metoda uruchomieniowa aplikacji Spring Boot.
	 *
	 * @param args argumenty wejściowe przekazywane przy starcie JVM.
	 */
	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
