package com.prova_api.controller;

import com.prova_api.dto.ApiResponse;
import com.prova_api.dto.PhraseResponseDto;
import com.prova_api.services.PhraseService;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller REST per la risorsa "frasi/citazioni" (phrases).
 *  REST API Controller per la gestione delle frasi/citazioni.
 *
 * Questa classe espone endpoint HTTP che seguono il paradigma REST (Representational State Transfer).
 * Una REST API permette di accedere e manipolare risorse tramite richieste HTTP standard (GET, POST, PUT, DELETE)
 * e risponde con dati in formato JSON (o XML). Spring Boot serializza automaticamente gli oggetti Java in JSON.
 *
 * Caratteristiche principali di una REST API:
 * - Stateless: ogni richiesta è indipendente, il server non mantiene stato tra le chiamate
 * - Resource-based: le URL identificano risorse (es. /api/v1/phrases = tutte le frasi)
 * - Utilizzo dei verbi HTTP per le operazioni (GET=leggere, POST=creare, PUT=aggiornare, DELETE=eliminare)
 * - Utilizzo di codici di stato HTTP standard (200 OK, 204 No Content, 400 Bad Request, 404 Not Found, 500 Internal Server Error)
 * - Utilizzo di header HTTP per la gestione delle cache, autenticazione, etc.
 * <p>
 * Espone solo dati in lettura (GET). Non espone mai l'entità JPA: tutte le risposte
 * usano {@link PhraseResponseDto} e il wrapper {@link ApiResponse}. Gli errori sono
 * gestiti dal {@link com.prova_api.exception.GlobalExceptionHandler} (404, 400, 500).
 * </p>
 * <p>
 * Base path: {@code /api/v1/phrases} (versioning API).
 * </p>
 */
@RestController
/*
 * @RestController = @Controller + @ResponseBody
 *
 * PERCHÉ CI VA:
 * - Indica a Spring che questa classe è un controller che gestisce richieste HTTP in ingresso.
 * - Senza questa annotation, Spring non riconoscerebbe i metodi come endpoint dell'API.
 *
 * COSA CI VA (comportamento):
 * 1) @Controller: registra la classe come bean e abilita il mapping degli URL ai metodi.
 * 2) @ResponseBody (implicito in RestController): il valore di ritorno di ogni metodo viene
 *    scritto direttamente nel body della risposta HTTP (es. oggetto Java → JSON), invece di
 *    essere interpretato come nome di una vista/template da renderizzare.
 *
 * In sintesi: ogni metodo che restituisce un tipo (Map, List, Phrase, ecc.) produce una
 * risposta HTTP con quel dato serializzato in JSON e con status code gestito da Spring (vedi sotto).
 */
@RequestMapping("/api/v1/phrases")
/*
 * Prefisso base per tutti gli endpoint di questo controller (versioning API).
 * Tutte le URL inizieranno con: http://localhost:8080/api/v1/phrases/...
 */
public class FraseController {

    private final PhraseService phraseService;

    public FraseController(PhraseService phraseService) {
        this.phraseService = phraseService;
    }

    /**
     * GET /api/v1/phrases/all — Restituisce tutte le frasi.
     * Risposta: 200 OK, body { "data": [ { "id", "phrase", "type" }, ... ] }.
     */
    @GetMapping("/all")
    public ApiResponse<List<PhraseResponseDto>> getAll() {
        List<PhraseResponseDto> data = phraseService.findAll();
        return new ApiResponse<>(data);
    }

    /**
     * GET /api/v1/phrases/{type} — Frasi per tipo (path variable).
     * Se type non è valido o è troppo corto, viene usato "generic" come default (comportamento legacy).
     * Risposta: 200 OK, body { "data": [ ... ] }.
     */
    @GetMapping("/{type}")
    public ApiResponse<List<PhraseResponseDto>> getAllByType(@PathVariable String type) {
        List<PhraseResponseDto> data = phraseService.findByTypeOrDefault(type);
        return new ApiResponse<>(data);
    }

    /**
     * GET /api/v1/phrases/random — Una frase casuale.
     * Con cache no-store per evitare che la risposta sia cachata.
     * Se non ci sono frasi, il servizio può restituire null: qui restituiamo 200 con body null (o si può lanciare PhraseNotFoundException per 404).
     */
    @GetMapping("/random")
    public ResponseEntity<ApiResponse<PhraseResponseDto>> getRandom() {
        PhraseResponseDto data = phraseService.findRandomOrNull();
        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(new ApiResponse<>(data));
    }

    /**
     * GET /api/v1/phrases/id/{id} — Frase per ID.
     * Se l'ID non esiste viene lanciata {@link com.prova_api.exception.PhraseNotFoundException} → 404.
     * Se l'ID non è numerico, MethodArgumentTypeMismatchException → 400 (gestita dal GlobalExceptionHandler).
     */
    @GetMapping("/id/{id}")
    public ApiResponse<PhraseResponseDto> getById(@PathVariable int id) {
        PhraseResponseDto data = phraseService.findById(id);
        return new ApiResponse<>(data);
    }

    /**
     * GET /api/v1/phrases/ping — Health/ping, nessun body.
     * Risposta: 204 No Content.
     */
    @GetMapping("/ping")
    public ResponseEntity<Void> ping() {
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/v1/phrases/by-type?type=backend — Frasi per tipo (query param).
     * Parametro type obbligatorio e deve essere frontend, backend o generic; altrimenti 400.
     */
    @GetMapping("/by-type")
    public ApiResponse<List<PhraseResponseDto>> getByTypeQuery(@RequestParam(required = false) String type) {
        phraseService.validateTypeRequired(type);
        List<PhraseResponseDto> data = phraseService.findByType(type);
        return new ApiResponse<>(data);
    }

    /**
     * GET /api/v1/phrases/random-explicit — Una frase casuale con status esplicito.
     * 200 OK se c'è almeno una frase; 404 se il database non contiene frasi (PhraseNotFoundException).
     */
    @GetMapping("/random-explicit")
    public ApiResponse<PhraseResponseDto> getRandomExplicit() {
        PhraseResponseDto data = phraseService.findRandom();
        return new ApiResponse<>(data);
    }
}
