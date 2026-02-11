package com.prova_api.prova_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Overview:
 * Questo e' uno smoke test del bootstrap Spring Boot.
 * Verifica che il contesto applicativo si avvii senza errori di wiring/configurazione.
 * Copre integrazione base dei bean, ma NON verifica endpoint o logica business specifica.
 * E' utile per intercettare regressioni grossolane dopo refactor/config changes.
 */
@SpringBootTest
// @SpringBootTest carica tutto il contesto: e' piu' costoso dei test slice, ma piu' integrato.
class ProvaApiApplicationTests {

	@Test
	void contextLoads() {
		// Scenario: l'applicazione deve avviarsi con configurazione valida.
		// Given / Arrange: nessun setup manuale, usa contesto reale.
		// When / Act: Spring prova ad avviare il contesto all'inizio del test.
		// Then / Assert: se non esplodono eccezioni, il test passa.
	}

	/*
	 * Common pitfalls:
	 * - Usarlo per ogni test: rallenta molto la suite.
	 * - Confondere smoke test con test funzionali di endpoint.
	 * - Dipendere da servizi esterni nel bootstrap (rende fragile il contextLoads).
	 */
}
