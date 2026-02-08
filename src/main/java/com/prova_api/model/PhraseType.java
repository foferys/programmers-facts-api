package com.prova_api.model;

import java.util.Arrays;
import java.util.Locale;

/**
 * Enum che rappresenta i tipi ammessi per le frasi/citazioni.
 * <p>
 * Usato per validare i parametri in ingresso (path o query) e restituire 400 Bad Request
 * se il tipo non è tra quelli consentiti, invece di usare un default silenzioso.
 * </p>
 * Valori ammessi: {@code frontend}, {@code backend}, {@code generic}.
 */
public enum PhraseType {

    FRONTEND("frontend"),
    BACKEND("backend"),
    GENERIC("generic");

    private final String value;

    PhraseType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * Converte una stringa nel valore enum corrispondente (case-insensitive).
     *
     * @param type stringa (es. da path o query param)
     * @return l'enum corrispondente
     * @throws IllegalArgumentException se la stringa non è un tipo valido
     */
    public static PhraseType fromString(String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Tipo frase mancante o vuoto");
        }
        String normalized = type.trim().toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(t -> t.value.equals(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tipo non valido: " + type + ". Valori ammessi: frontend, backend, generic"));
    }

    /**
     * Verifica se la stringa è un tipo valido (senza lanciare eccezione).
     */
    public static boolean isValid(String type) {
        if (type == null || type.isBlank()) {
            return false;
        }
        String normalized = type.trim().toLowerCase(Locale.ROOT);
        return Arrays.stream(values()).anyMatch(t -> t.value.equals(normalized));
    }
}
