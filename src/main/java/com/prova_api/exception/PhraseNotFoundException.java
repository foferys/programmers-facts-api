package com.prova_api.exception;

import org.springframework.http.HttpStatus;

/**
 * Eccezione unchecked (RuntimeException) lanciata quando una risorsa frase
 * richiesta per ID non esiste nel database, o quando si richiede una frase
 * casuale ma non ci sono frasi disponibili.
 * <p>
 * Il {@link com.prova_api.exception.GlobalExceptionHandler} la traduce in
 * risposta HTTP 404 Not Found con body JSON uniforme.
 * </p>
 */
public class PhraseNotFoundException extends RuntimeException {

    private final HttpStatus status = HttpStatus.NOT_FOUND;

    public PhraseNotFoundException(String message) {
        super(message);
    }

    public PhraseNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpStatus getStatus() {
        return status;
    }
}
