package com.prova_api.dto;

/**
 * DTO (Data Transfer Object) per l'esposizione di una singola frase verso l'API REST.
 * <p>
 * Serve a non esporre direttamente l'entità JPA {@link com.prova_api.phrases.Phrase}:
 * il contratto dell'API resta stabile anche se il modello persistente cambia,
 * e si evitano campi interni o annotazioni JPA nel JSON.
 * </p>
 * <p>
 * I campi coincidono con il formato richiesto dalla specifica: id, phrase, type.
 * </p>
 */
public class PhraseResponseDto {

    private final int id;
    private final String phrase;
    private final String type;

    public PhraseResponseDto(int id, String phrase, String type) {
        this.id = id;
        this.phrase = phrase;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public String getPhrase() {
        return phrase;
    }

    public String getType() {
        return type;
    }
}
