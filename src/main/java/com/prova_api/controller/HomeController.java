package com.prova_api.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prova_api.phrases.Phrase;
import com.prova_api.services.PhraseRepository;


@Controller
public class HomeController {

    @Autowired
    private PhraseRepository pRepository;

    @GetMapping("/")
    public String home(Model model) throws JsonProcessingException {

        Phrase randomphrase = pRepository.findRandomPhrase();

        try {
            
            if(randomphrase != null) {
    
                model.addAttribute("randomexample", randomphrase.getPhrase());
            }else {
                model.addAttribute("randomexample", "Errore nel db, controlla che non sia vuoto");
                
            }
            
        } catch (Exception e) {
            
            model.addAttribute("randomexample", "Errore interno del server");
            
            System.out.println(e.getCause());
            e.printStackTrace();
        }



        Map<String, Phrase> response = new HashMap<>();
        response.put("data", randomphrase);

        // Converti la mappa in una stringa JSON
        ObjectMapper randomObjectMapper = new ObjectMapper();
        String randomjsonquote = randomObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);

        model.addAttribute("randomjson", randomjsonquote);

        return "index";
    }
    
    
}
