package com.prova_api.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Optional;

/**
 * Gestore globale delle eccezioni per l'API REST.
 * <p>
 * Intercetta le eccezioni lanciate dai controller e restituisce risposte JSON
 * uniformi (formato {@link ApiError}), con lo status HTTP appropriato.
 * </p>
 * <p>
 * Gestisce sia eccezioni unchecked (RuntimeException) che checked dove necessario:
 * - {@link PhraseNotFoundException} → 404 Not Found
 * - {@link InvalidPhraseTypeException} → 400 Bad Request
 * - {@link MethodArgumentTypeMismatchException} (es. ID non numerico) → 400 Bad Request
 * - {@link IllegalArgumentException} → 400 Bad Request
 * - Qualsiasi altra RuntimeException → 500 Internal Server Error
 * </p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Risorsa non trovata (es. frase per ID inesistente, random senza frasi).
     */
    @ExceptionHandler(PhraseNotFoundException.class)
    public ResponseEntity<ApiError> handlePhraseNotFound(PhraseNotFoundException ex) {
        log.debug("Risorsa non trovata: {}", ex.getMessage());
        ApiError body = new ApiError(ex.getMessage(), String.valueOf(HttpStatus.NOT_FOUND.value()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Tipo frase non valido (parametro type diverso da frontend/backend/generic).
     */
    @ExceptionHandler(InvalidPhraseTypeException.class)
    public ResponseEntity<ApiError> handleInvalidPhraseType(InvalidPhraseTypeException ex) {
        log.debug("Tipo frase non valido: {}", ex.getMessage());
        ApiError body = new ApiError(ex.getMessage(), String.valueOf(HttpStatus.BAD_REQUEST.value()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Parametro di tipo errato (es. /id/abc invece di /id/1).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = Optional.ofNullable(ex.getValue())
                .map(v -> "Valore non valido: " + v)
                .orElse("Parametro non valido");
        log.debug("Method argument type mismatch: {}", ex.getMessage());
        ApiError body = new ApiError(message, String.valueOf(HttpStatus.BAD_REQUEST.value()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Argomento non valido (es. validazione che lancia IllegalArgumentException).
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        log.debug("Illegal argument: {}", ex.getMessage());
        ApiError body = new ApiError(ex.getMessage(), String.valueOf(HttpStatus.BAD_REQUEST.value()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Eccezioni non previste: si restituisce 500 senza esporre dettagli interni al client.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        log.error("Errore non gestito", ex);
        ApiError body = new ApiError("Errore interno del server", String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}
