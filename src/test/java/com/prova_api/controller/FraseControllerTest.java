package com.prova_api.controller;

import com.prova_api.dto.PhraseResponseDto;
import com.prova_api.exception.InvalidPhraseTypeException;
import com.prova_api.services.PhraseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Overview:
 * Questa classe testa SOLO il layer HTTP del controller REST.
 * Verifica routing, status code, header e shape JSON delle risposte.
 * Non testa DB o logica business interna del service.
 * Copertura principale: endpoint GET e gestione errori mappata dal GlobalExceptionHandler.
 */
@WebMvcTest(FraseController.class)
// @WebMvcTest carica un contesto Spring "slice" del web layer, piu' veloce di @SpringBootTest.
// Nel contesto di questo test, il controller e' reale, mentre le dipendenze vengono mockate.
class FraseControllerTest {

    // Bean reale fornito da Spring Test per simulare richieste HTTP senza server reale.
    @Autowired
    private MockMvc mockMvc;

    // @MockBean sostituisce il bean Spring reale con un mock Mockito nel contesto WebMvcTest.
    // Qui mockiamo il service per isolare il controller e rendere il test deterministico.
    @MockBean
    private PhraseService phraseService;

    @Test
    void getAllReturnsWrappedData() throws Exception {
        // Scenario: GET /all deve restituire 200 e una lista nel wrapper "data".
        // Given / Arrange
        when(phraseService.findAll()).thenReturn(List.of(
                new PhraseResponseDto(1, "Keep it simple", "generic")
        ));

        // When / Act
        // mockMvc.perform(...) costruisce una request HTTP; accept(JSON) indica il formato atteso.
        mockMvc.perform(get("/api/v1/phrases/all").accept(MediaType.APPLICATION_JSON))
                // Then / Assert
                // status().isOk() garantisce che l'endpoint risponde con HTTP 200.
                .andExpect(status().isOk())
                // jsonPath verifica campi puntuali del payload JSON serializzato da Spring/Jackson.
                .andExpect(jsonPath("$.data[0].id").value(1))
                .andExpect(jsonPath("$.data[0].phrase").value("Keep it simple"))
                .andExpect(jsonPath("$.data[0].type").value("generic"));

        // verify(...) garantisce che il controller abbia delegato al service corretto.
        verify(phraseService).findAll();
    }

    @Test
    void getRandomReturnsNoStoreHeader() throws Exception {
        // Scenario: GET /random deve includere header anti-cache e payload frase.
        // Given / Arrange
        when(phraseService.findRandomOrNull()).thenReturn(new PhraseResponseDto(7, "Ship fast", "backend"));

        // When / Act
        mockMvc.perform(get("/api/v1/phrases/random").accept(MediaType.APPLICATION_JSON))
                // Then / Assert
                .andExpect(status().isOk())
                // header(...) verifica comportamento HTTP, non solo contenuto JSON.
                .andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.data.id").value(7))
                .andExpect(jsonPath("$.data.phrase").value("Ship fast"))
                .andExpect(jsonPath("$.data.type").value("backend"));

        verify(phraseService).findRandomOrNull();
    }

    @Test
    void byTypeWithoutParamReturnsBadRequestFromGlobalHandler() throws Exception {
        // Scenario: GET /by-type senza query param type deve tornare 400.
        // Given / Arrange
        // doThrow(...).when(...) simula un path d'errore lanciato dal service mockato.
        // isNull() e' un matcher Mockito: rende esplicito che il parametro atteso e' null.
        doThrow(new InvalidPhraseTypeException("Parametro 'type' obbligatorio"))
                .when(phraseService).validateTypeRequired(isNull());

        // When / Act
        mockMvc.perform(get("/api/v1/phrases/by-type").accept(MediaType.APPLICATION_JSON))
                // Then / Assert
                .andExpect(status().isBadRequest())
                // Verifichiamo il contratto JSON dell'errore (ApiError: code/error).
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.error").value("Parametro 'type' obbligatorio"));

        verify(phraseService).validateTypeRequired(null);
    }

    @Test
    void getByIdWithNonNumericIdReturnsBadRequest() throws Exception {
        // Scenario: GET /id/{id} con id non numerico deve tornare 400 (type mismatch).
        // Given / Arrange: nessun setup del service, l'errore nasce nel binding MVC.
        // When / Act + Then / Assert
        mockMvc.perform(get("/api/v1/phrases/id/not-a-number").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"));
    }

    /*
     * Common pitfalls:
     * - Usare @SpringBootTest per test di solo controller: rallenta e introduce dipendenze inutili.
     * - Assertare campi JSON sbagliati (es. status/message invece di code/error in questo progetto).
     * - Dimenticare verify(...) e perdere il controllo sulla delega controller -> service.
     * - Mockare troppi componenti: in WebMvcTest basta mockare le dipendenze dirette del controller.
     */
}
