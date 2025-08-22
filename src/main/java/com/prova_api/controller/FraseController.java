package com.prova_api.controller;

import org.springframework.web.bind.annotation.RestController;

import com.prova_api.phrases.Phrase;
import com.prova_api.services.PhraseRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/getPhrase")
public class FraseController {


    
    @Autowired
    private PhraseRepository phrasesRepository;

    
    
    @GetMapping("/all")
    public Map<String, List<Phrase>> getAll() {

        List<Phrase> phrases = phrasesRepository.findAll();

        Map<String, List<Phrase>> response = new HashMap<>();
        response.put("data", phrases);

        return response;
    }

    @GetMapping("/{type}")
    public Map<String, List<Phrase>> getAllByType(@PathVariable String type) {

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
    public Map<String, Phrase> getRandom() {

        Phrase phrase = phrasesRepository.findRandomPhrase();

        Map<String, Phrase> response = new HashMap<>();
        response.put("data", phrase);

        return response;
    }



    

}
