package com.prova_api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.prova_api.dto.ApiResponse;
import com.prova_api.dto.PhraseResponseDto;
import com.prova_api.services.PhraseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller per la pagina iniziale (Thymeleaf) su "/".
 * <p>
 * Mostra una citazione casuale e la documentazione degli endpoint. Utilizza il
 * {@link PhraseService} e i DTO: l'entità JPA non viene mai esposta al template.
 * In caso di errore (DB vuoto o eccezione) la pagina mostra un messaggio amichevole.
 * </p>
 */
@Controller
public class HomeController {

    private final PhraseService phraseService;
    private final ObjectMapper objectMapper;

    public HomeController(PhraseService phraseService, ObjectMapper objectMapper) {
        this.phraseService = phraseService;
        this.objectMapper = objectMapper;
    }

    /**
     * GET / — Pagina principale con una citazione casuale e documentazione API.
     * Attributi modello: randomexample (testo frase), randomjson (JSON formattato per esempio).
     */
    @GetMapping("/")
    public String home(Model model) {
        String exampleText;
        String exampleJson;

        try {
            PhraseResponseDto randomDto = phraseService.findRandomOrNull();
            if (randomDto != null) {
                exampleText = randomDto.getPhrase();
                exampleJson = formatResponseJson(randomDto);
            } else {
                exampleText = "Nessuna citazione disponibile. Controlla che il database non sia vuoto.";
                exampleJson = "{}";
            }
        } catch (Exception e) {
            exampleText = "Errore nel caricamento della citazione. Riprova più tardi.";
            exampleJson = "{}";
        }

        model.addAttribute("randomexample", exampleText);
        model.addAttribute("randomjson", exampleJson);
        return "index";
    }

    /**
     * Serializza la risposta "data" in JSON formattato per la documentazione in pagina.
     * ObjectMapper non è una risorsa closeable, quindi non richiede try-with-resources.
     */
    private String formatResponseJson(PhraseResponseDto dto) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(new ApiResponse<>(dto));
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}
