package com.prova_api.controller;

import org.springframework.web.bind.annotation.RestController;
import com.prova_api.phrases.Phrase;
import com.prova_api.services.PhraseRepository;

import java.net.http.HttpHeaders;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.WebProperties.Resources.Cache.Cachecontrol;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;



@RestController
@RequestMapping("/v1/phrases")
public class FraseController {


    
    @Autowired
    private PhraseRepository phrasesRepository;

    

    @GetMapping("/")
    public Map<String, List<Phrase>> getAll() {

        List<Phrase> phrases = phrasesRepository.findAll();

        Map<String, List<Phrase>> response = new HashMap<>();
        response.put("data", phrases);

        return response;
    }

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
 

    @GetMapping("/random")
    public ResponseEntity<Map<String, Phrase>> getRandom() {

        Phrase phrase = phrasesRepository.findRandomPhrase();

        Map<String, Phrase> response = new HashMap<>();
        response.put("data", phrase);

        return ResponseEntity.ok()
                .cacheControl(CacheControl.noStore())
                .body(response);
    }



    

}
