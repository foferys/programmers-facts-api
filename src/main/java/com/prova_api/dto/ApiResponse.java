package com.prova_api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Wrapper standard per le risposte JSON dell'API.
 * <p>
 * Ogni risposta di successo ha la forma {@code { "data": ... } } come da specifica PDR.
 * Il tipo generico T può essere {@link PhraseResponseDto} (singola frase) o
 * {@code List<PhraseResponseDto>} (lista di frasi).
 * </p>
 * <p>
 * Non esponiamo mai l'entità JPA direttamente: il controller e il client vedono solo DTO.
 * </p>
 *
 * @param <T> tipo del payload (PhraseResponseDto o List&lt;PhraseResponseDto&gt;)
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final T data;

    public ApiResponse(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }
}
