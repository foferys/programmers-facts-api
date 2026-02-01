package com.prova_api.controller;
import org.springframework.web.bind.annotation.RestController;
import com.prova_api.phrases.Phrase;
import com.prova_api.services.PhraseRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.CacheControl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * REST API Controller per la gestione delle frasi/citazioni.
 *
 * Questa classe espone endpoint HTTP che seguono il paradigma REST (Representational State Transfer).
 * Una REST API permette di accedere e manipolare risorse tramite richieste HTTP standard (GET, POST, PUT, DELETE)
 * e risponde con dati in formato JSON (o XML). Spring Boot serializza automaticamente gli oggetti Java in JSON.
 *
 * Caratteristiche principali di una REST API:
 * - Stateless: ogni richiesta è indipendente, il server non mantiene stato tra le chiamate
 * - Resource-based: le URL identificano risorse (es. /getPhrase/all = tutte le frasi)
 * - Utilizzo dei verbi HTTP per le operazioni (GET=leggere, POST=creare, PUT=aggiornare, DELETE=eliminare)
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
@RequestMapping("/getPhrase")
/*
 * Prefisso base per tutti gli endpoint di questo controller.
 * Tutte le URL inizieranno con: http://localhost:8080/getPhrase/...
 */
public class FraseController {

    @Autowired
    private PhraseRepository phrasesRepository;

    // ========== MAPPING CON STATUS CODE AUTOMATICO (Spring) ==========
    /*
     * Nei mapping sottostanti NON usiamo ResponseEntity: il valore di ritorno è solo il body.
     * Spring assegna automaticamente:
     * - HTTP 200 OK quando il metodo termina senza eccezioni (successo).
     * - HTTP 500 Internal Server Error se viene lanciata un'eccezione non gestita.
     *
     * HTTP Status Code (principali):
     * - 200 OK: richiesta GET/PUT riuscita, risorsa restituita/aggiornata.
     * - 201 Created: risorsa creata con successo (tipicamente dopo POST).
     * - 204 No Content: successo ma nessun contenuto da restituire (es. dopo DELETE).
     * - 400 Bad Request: richiesta malformata o parametri non validi.
     * - 404 Not Found: risorsa richiesta non trovata.
     * - 500 Internal Server Error: errore lato server.
     */

    /**
     * Restituisce tutte le frasi presenti nel database.
     * Status: Spring restituisce automaticamente 200 OK in caso di successo.
     */
    @GetMapping("/all")
    public Map<String, List<Phrase>> getAll() {

        List<Phrase> phrases = phrasesRepository.findAll();
        Map<String, List<Phrase>> response = new HashMap<>();
        response.put("data", phrases);
        return response;

    }

    /**
     * Restituisce le frasi filtrate per tipo (es. frontend, backend, generic).
     * Path variable: tipo nella URL (es. /getPhrase/frontend).
     * Status: Spring restituisce automaticamente 200 OK in caso di successo.
     */
    @GetMapping("/{type}")
    public Map<String, List<Phrase>> getAllByType(@PathVariable String type) {
        // public Map<String, List<Phrase>> getAllByType(@RequestParam String type) { // -> using the @RequestParam we can leave
        //@GetMapping("/") for the mapping but in the browser we have to use http://localhost:8080/getPhrase/?type=frontend

        // Imposta un valore predefinito se 'type' è nullo, vuoto o troppo corto
        // it would be better if we create a method that check if 1/3 word (frontend/backend/generic) is there
        if(type == null || type.isEmpty() || type.length() < 5) {
            type = "generic";
        }
        List<Phrase> phrases = phrasesRepository.findByType(type);
        System.out.println("il tipo: " + type);

        Map<String, List<Phrase>> response = new HashMap<>();
        response.put("data", phrases);

        return response;
    }

    /**
     * Restituisce una frase casuale.
     * Status: Spring restituisce automaticamente 200 OK in caso di successo.
     */
    @GetMapping("/random")
    public ResponseEntity<Map<String, Phrase>> getRandom() {

        Phrase phrase = phrasesRepository.findRandomPhrase();

        Map<String, Phrase> response = new HashMap<>();
        response.put("data", phrase);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(response);
    }

    // ========== MAPPING CON ResponseEntity E STATUS CODE ESPLICITO ==========
    /*
     * ResponseEntity<T> permette di controllare esplicitamente:
     * - body della risposta (il dato)
     * - status code HTTP (200, 201, 404, 400, ecc.)
     * - header opzionali
     *
     * Utile quando lo status deve dipendere dalla logica (es. 404 se non trovato, 201 se creato).
     */

    /**
     * Esempio: GET per ID con status esplicito.
     * - 200 OK se la frase esiste.
     * - 404 Not Found se l'ID non esiste nel database.
     */
    @GetMapping("/id/{id}")
    public ResponseEntity<Map<String, Phrase>> getById(@PathVariable int id) {
        
        Optional<Phrase> optional = phrasesRepository.findById(id);
        if (optional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            // 404 Not Found: risorsa non trovata
        }
        Map<String, Phrase> body = new HashMap<>();
        body.put("data", optional.get());
        return ResponseEntity.ok(body);
        // 200 OK + body
    }

    /**
     * Esempio: risposta 204 No Content (successo senza body).
     * Endpoint di esempio che "conferma" un'operazione senza restituire dati.
     */
    @GetMapping("/ping")
    public ResponseEntity<Void> ping() {
        return ResponseEntity.noContent().build();
        // 204 No Content
    }

    /**
     * Esempio: validazione parametro con 400 Bad Request.
     * Se "type" è vuoto o troppo corto, restituiamo 400 invece di usare un default.
     */
    @GetMapping("/by-type")
    public ResponseEntity<Map<String, List<Phrase>>> getByTypeQuery(@RequestParam(required = false) String type) {
        if (type == null || type.trim().length() < 3) {
            return ResponseEntity.badRequest().build();
            // 400 Bad Request: parametro mancante o non valido
        }
        List<Phrase> phrases = phrasesRepository.findByType(type.trim());
        Map<String, List<Phrase>> response = new HashMap<>();
        response.put("data", phrases);
        return ResponseEntity.ok(response);
        // 200 OK + body
    }

    /**
     * Esempio: risposta con body e status 200 usando ResponseEntity.
     * Equivalente concettuale a getRandom() ma con status esplicito.
     */
    @GetMapping("/random-explicit")
    public ResponseEntity<Map<String, Phrase>> getRandomExplicit() {
        Phrase phrase = phrasesRepository.findRandomPhrase();
        if (phrase == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Map<String, Phrase> response = new HashMap<>();
        response.put("data", phrase);
        return ResponseEntity.status(HttpStatus.OK).body(response);
        // 200 OK esplicito + body
    }
}
