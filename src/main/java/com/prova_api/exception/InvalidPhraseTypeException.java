package com.prova_api.exception;

import org.springframework.http.HttpStatus;

/**
 * Eccezione unchecked (RuntimeException) lanciata quando il parametro "type"
 * (path o query) non è tra i valori ammessi: frontend, backend, generic.
 * <p>
 * Il {@link com.prova_api.exception.GlobalExceptionHandler} la traduce in
 * risposta HTTP 400 Bad Request con body JSON uniforme.
 * </p>
 */
public class InvalidPhraseTypeException extends RuntimeException {

    private final HttpStatus status = HttpStatus.BAD_REQUEST;

    public InvalidPhraseTypeException(String message) {
        super(message);
    }

    public InvalidPhraseTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpStatus getStatus() {
        return status;
    }
}
