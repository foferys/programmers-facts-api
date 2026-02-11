package com.prova_api.services;

import com.prova_api.dto.PhraseResponseDto;
import com.prova_api.exception.InvalidPhraseTypeException;
import com.prova_api.exception.PhraseNotFoundException;
import com.prova_api.phrases.Phrase;
import com.prova_api.repository.PhraseRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Overview:
 * Questa classe testa la logica business di PhraseService in isolamento.
 * Non avvia Spring context: i test sono unit test puri e molto veloci.
 * Copre validazioni, fallback, eccezioni dominio e mapping da entity a DTO.
 * Non copre HTTP mapping: quello e' delegato ai test controller.
 */
@ExtendWith(MockitoExtension.class)
// MockitoExtension inizializza automaticamente @Mock e @InjectMocks in JUnit 5.
class PhraseServiceTest {

    // Mock = dipendenza simulata: non accede a DB reale e risponde solo come configurato nel test.
    @Mock
    private PhraseRepository phraseRepository;

    // Bean reale sotto test: Mockito inietta i mock nelle dipendenze del service.
    @InjectMocks
    private PhraseService phraseService;

    @Test
    void findByTypeReturnsDtosWhenTypeIsValid() {
        // Scenario: tipo valido (anche uppercase) -> service normalizza e ritorna DTO.
        // Given / Arrange
        Phrase phrase = phrase(10, "Frontend quote", "frontend");
        // when(...).thenReturn(...) definisce il comportamento del mock per input specifico.
        when(phraseRepository.findByType("frontend")).thenReturn(List.of(phrase));

        // When / Act
        List<PhraseResponseDto> result = phraseService.findByType("FRONTEND");

        // Then / Assert
        // Queste assert garantiscono il contratto del mapping e della normalizzazione del tipo.
        assertEquals(1, result.size());
        assertEquals(10, result.get(0).getId());
        assertEquals("Frontend quote", result.get(0).getPhrase());
        assertEquals("frontend", result.get(0).getType());
        // verify garantisce che il repository sia chiamato con il valore normalizzato.
        verify(phraseRepository).findByType("frontend");
    }

    @Test
    void findByTypeThrowsDomainExceptionWhenTypeIsInvalid() {
        // Scenario: tipo non ammesso -> eccezione dominio e nessuna query al repository.
        // Given / Arrange + When / Act + Then / Assert
        assertThrows(InvalidPhraseTypeException.class, () -> phraseService.findByType("mobile"));
        // never() + anyString() mostrano che non deve esistere alcuna interazione in questo path.
        verify(phraseRepository, never()).findByType(anyString());
    }

    @Test
    void findByTypeOrDefaultFallsBackToGenericForInvalidType() {
        // Scenario: tipo invalido nel path legacy -> fallback automatico a "generic".
        // Given / Arrange
        Phrase phrase = phrase(5, "Generic quote", "generic");
        when(phraseRepository.findByType("generic")).thenReturn(List.of(phrase));

        // When / Act
        List<PhraseResponseDto> result = phraseService.findByTypeOrDefault("x");

        // Then / Assert
        assertEquals(1, result.size());
        assertEquals("generic", result.get(0).getType());
        verify(phraseRepository).findByType("generic");
    }

    @Test
    void findRandomThrowsWhenRepositoryIsEmpty() {
        // Scenario: repository senza frasi -> il service deve fallire con eccezione esplicita.
        // Given / Arrange
        when(phraseRepository.findRandomPhrase()).thenReturn(null);

        // When / Act + Then / Assert
        assertThrows(PhraseNotFoundException.class, () -> phraseService.findRandom());
        verify(phraseRepository).findRandomPhrase();
    }

    @Test
    void findByIdReturnsDtoWhenPresent() {
        // Scenario: ID presente -> ritorna DTO popolato correttamente.
        // Given / Arrange
        when(phraseRepository.findById(3)).thenReturn(Optional.of(phrase(3, "By id", "backend")));

        // When / Act
        PhraseResponseDto result = phraseService.findById(3);

        // Then / Assert
        // assertNotNull evita false positive su assertion successive per campi DTO.
        assertNotNull(result);
        assertEquals(3, result.getId());
        assertEquals("By id", result.getPhrase());
        assertEquals("backend", result.getType());
        verify(phraseRepository).findById(3);
    }

    @Test
    void findByIdThrowsWhenMissing() {
        // Scenario: ID assente -> eccezione con messaggio utile al troubleshooting.
        // Given / Arrange
        when(phraseRepository.findById(99)).thenReturn(Optional.empty());

        // When / Act
        PhraseNotFoundException ex = assertThrows(PhraseNotFoundException.class, () -> phraseService.findById(99));

        // Then / Assert
        assertTrue(ex.getMessage().contains("99"));
        verify(phraseRepository).findById(99);
    }

    @Test
    void validateTypeRequiredRejectsBlankAndTooShortValues() {
        // Scenario: input non valido in validazione type -> sempre InvalidPhraseTypeException.
        // Given / Arrange + When / Act + Then / Assert
        assertThrows(InvalidPhraseTypeException.class, () -> phraseService.validateTypeRequired(" "));
        assertThrows(InvalidPhraseTypeException.class, () -> phraseService.validateTypeRequired("ab"));
    }

    // Helper di test per creare fixture leggibili e ridurre duplicazione nel blocco Arrange.
    private static Phrase phrase(int id, String text, String type) {
        Phrase phrase = new Phrase();
        phrase.setId(id);
        phrase.setPhrase(text);
        phrase.setType(type);
        return phrase;
    }

    /*
     * Common pitfalls:
     * - Mischiare unit test e Spring context: qui non serve @SpringBootTest.
     * - Dimenticare verify(...) e perdere controllo sulle interazioni col repository.
     * - Usare dati casuali/non deterministici che rendono i test flaky.
     * - Testare troppa logica in un solo metodo: meglio scenari piccoli e specifici.
     */
}
