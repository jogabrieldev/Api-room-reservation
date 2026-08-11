package com.ApiRoomRerservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		System.out.println("Hello World");
		imprimirNomes("Joao", "Maria", "Pedro");
	}

	public static void imprimirNomes(String... nomes) {
		for (String nome : nomes) {
			System.out.println(nome);
		}
	}

}
