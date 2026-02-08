package com.prova_api.services;

import com.prova_api.dto.PhraseResponseDto;
import com.prova_api.exception.InvalidPhraseTypeException;
import com.prova_api.exception.PhraseNotFoundException;
import com.prova_api.model.PhraseType;
import com.prova_api.phrases.Phrase;
import com.prova_api.repository.PhraseRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servizio applicativo per la logica sulle frasi/citazioni.
 * <p>
 * Si occupa di: lettura dal repository, validazione del tipo (frontend/backend/generic),
 * conversione da entità JPA a DTO. Il controller non accede mai all'entità né al repository
 * direttamente per la logica di business, così l'entity non viene esposta all'API.
 * </p>
 */
@Service
public class PhraseService {

    private final PhraseRepository phraseRepository;

    public PhraseService(PhraseRepository phraseRepository) {
        this.phraseRepository = phraseRepository;
    }

    /**
     * Converte un'entità JPA in DTO per l'esposizione API (nasconde il modello persistente).
     */
    public static PhraseResponseDto toDto(Phrase entity) {
        if (entity == null) {
            return null;
        }
        return new PhraseResponseDto(entity.getId(), entity.getPhrase(), entity.getType());
    }

    /**
     * Converte una lista di entità in lista di DTO.
     */
    public static List<PhraseResponseDto> toDtoList(List<Phrase> entities) {
        if (entities == null) {
            return List.of();
        }
        return entities.stream()
                .map(PhraseService::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Restituisce tutte le frasi come DTO.
     */
    public List<PhraseResponseDto> findAll() {
        return toDtoList(phraseRepository.findAll());
    }

    /**
     * Restituisce le frasi per tipo validato (frontend, backend, generic).
     * Se il tipo non è valido, lancia {@link InvalidPhraseTypeException} (gestita dal GlobalExceptionHandler → 400).
     */
    public List<PhraseResponseDto> findByType(String type) {
        try {
            String validatedType = PhraseType.fromString(type).getValue();
            return toDtoList(phraseRepository.findByType(validatedType));
        } catch (IllegalArgumentException e) {
            throw new InvalidPhraseTypeException(e.getMessage(), e);
        }
    }

    /**
     * Restituisce le frasi per tipo quando il tipo è già validato (es. path variable).
     * Per tipo nullo/vuoto/non valido usa "generic" come fallback (comportamento legacy path /{type}).
     */
    public List<PhraseResponseDto> findByTypeOrDefault(String type) {
        String effectiveType = (type != null && PhraseType.isValid(type)) ? PhraseType.fromString(type).getValue() : PhraseType.GENERIC.getValue();
        return toDtoList(phraseRepository.findByType(effectiveType));
    }

    /**
     * Una frase casuale. Lancia {@link PhraseNotFoundException} se non ci sono frasi (random-explicit).
     */
    public PhraseResponseDto findRandom() {
        Phrase entity = phraseRepository.findRandomPhrase();
        if (entity == null) {
            throw new PhraseNotFoundException("Nessuna frase disponibile");
        }
        return toDto(entity);
    }

    /**
     * Una frase casuale o null se il database è vuoto (per endpoint che accettano null senza 404).
     */
    public PhraseResponseDto findRandomOrNull() {
        Phrase entity = phraseRepository.findRandomPhrase();
        return toDto(entity);
    }

    /**
     * Frase per ID. Lancia {@link PhraseNotFoundException} se l'ID non esiste (→ 404).
     */
    public PhraseResponseDto findById(int id) {
        return phraseRepository.findById(id)
                .map(PhraseService::toDto)
                .orElseThrow(() -> new PhraseNotFoundException("Frase non trovata per id: " + id));
    }

    /**
     * Valida che il parametro type sia presente e sia uno dei tipi ammessi.
     * Lancia {@link InvalidPhraseTypeException} se mancante o non valido (→ 400).
     */
    public void validateTypeRequired(String type) {
        if (type == null || type.isBlank()) {
            throw new InvalidPhraseTypeException("Parametro 'type' obbligatorio");
        }
        if (type.trim().length() < 3) {
            throw new InvalidPhraseTypeException("Parametro 'type' troppo corto. Valori ammessi: frontend, backend, generic");
        }
        try {
            PhraseType.fromString(type);
        } catch (IllegalArgumentException e) {
            throw new InvalidPhraseTypeException(e.getMessage(), e);
        }
    }
}
