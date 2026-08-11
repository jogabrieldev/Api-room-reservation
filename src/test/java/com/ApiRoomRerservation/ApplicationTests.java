package com.ApiRoomRerservation;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.jupiter.api.Test;

class ApplicationTests {

	@Test
	void deveImprimirOsNomes() {
		ByteArrayOutputStream saida = new ByteArrayOutputStream();
		PrintStream saidaOriginal = System.out;

		try {
			System.setOut(new PrintStream(saida));
			Application.imprimirNomes("Ana", "Carlos");

			String esperado = "Ana" + System.lineSeparator()
					+ "Carlos" + System.lineSeparator();
			assertEquals(esperado, saida.toString());
		} finally {
			System.setOut(saidaOriginal);
		}
	}

}
