package com.prova_api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

/**
 * Struttura standard per il body JSON delle risposte di errore dell'API.
 * <p>
 * Permette ai client di ricevere sempre un formato uniforme in caso di 4xx/5xx,
 * con campi: error (messaggio), code (codice HTTP o identificativo), timestamp.
 * </p>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    private final String error;
    private final String code;
    private final Instant timestamp;

    public ApiError(String error, String code) {
        this.error = error;
        this.code = code;
        this.timestamp = Instant.now();
    }

    public String getError() {
        return error;
    }

    public String getCode() {
        return code;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
