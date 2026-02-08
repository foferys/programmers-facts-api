package com.prova_api.repository;

import com.prova_api.phrases.Phrase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Repository JPA per l'entità {@link Phrase}.
 * <p>
 *  La query random usa RANDOM() per PostgreSQL; per MySQL si userebbe RAND() e LIMIT 1.
 * </p>
 */
public interface PhraseRepository extends JpaRepository<Phrase, Integer> {

    /**
     * Restituisce tutte le frasi con il tipo indicato (es. frontend, backend, generic).
     */
    List<Phrase> findByType(String type);

    /**
     * Restituisce una frase casuale (una riga dalla tabella phrases, ordinata random).
     * Può restituire null se la tabella è vuota.
     */
    @Query(value = "SELECT * FROM phrases ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Phrase findRandomPhrase();
}
