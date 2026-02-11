package com.prova_api.repository;

import com.prova_api.phrases.Phrase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Overview:
 * Questa classe testa il layer di persistenza (repository JPA) con query reali.
 * @DataJpaTest avvia solo componenti JPA + DataSource embedded, non l'intera app.
 * In questo progetto i test girano su H2 in-memory (vedi src/test/resources/application.properties),
 * quindi sono isolati da PostgreSQL esterno e ripetibili in CI.
 * Non copre controller/service: copre solo comportamento delle query repository.
 */
@DataJpaTest
// Slice test per JPA: crea schema temporaneo e rollback transazionale tra test.
class PhraseRepositoryTest {

    // Bean reale del repository: qui NON e' mockato, vogliamo testare SQL/JPA vero.
    @Autowired
    private PhraseRepository phraseRepository;

    @Test
    void findByTypeReturnsOnlyMatchingRows() {
        // Scenario: findByType deve filtrare solo righe con type richiesto.
        // Given / Arrange
        phraseRepository.save(buildPhrase("UI first", "frontend"));
        phraseRepository.save(buildPhrase("API first", "backend"));

        // When / Act
        List<Phrase> frontend = phraseRepository.findByType("frontend");

        // Then / Assert
        // Le assert garantiscono che la query non ritorni record con type diverso.
        assertEquals(1, frontend.size());
        assertEquals("frontend", frontend.get(0).getType());
        assertEquals("UI first", frontend.get(0).getPhrase());
    }

    @Test
    void findRandomPhraseReturnsExistingRow() {
        // Scenario: query random deve restituire una frase esistente e non null.
        // Given / Arrange
        Phrase one = phraseRepository.save(buildPhrase("One", "generic"));
        Phrase two = phraseRepository.save(buildPhrase("Two", "generic"));

        // When / Act
        Phrase random = phraseRepository.findRandomPhrase();

        // Then / Assert
        // assertTrue sugli id assicura che il record venga dal dataset creato nel test.
        assertNotNull(random);
        assertTrue(random.getId() == one.getId() || random.getId() == two.getId());
    }

    // Helper fixture: crea entity minima necessaria ai test repository.
    private static Phrase buildPhrase(String text, String type) {
        Phrase phrase = new Phrase();
        phrase.setPhrase(text);
        phrase.setType(type);
        return phrase;
    }

    /*
     * Common pitfalls:
     * - Mockare il repository in un test repository: annulla il valore del test.
     * - Dipendere da data.sql di produzione: rende il test fragile.
     * - Non controllare il filtro della query (solo size senza validare i campi).
     * - Usare DB esterno nei test locali: rompe la determinism/portabilita'.
     */
}
