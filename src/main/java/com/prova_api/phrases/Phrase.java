package com.prova_api.phrases;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entità JPA che mappa la tabella {@code phrases} del database.
 * <p>
 * Questa classe è usata solo a livello di persistenza e servizi; non deve essere
 * esposta direttamente dall'API REST. L'esposizione avviene tramite
 * {@link com.prova_api.dto.PhraseResponseDto} per disaccoppiare il contratto API dal modello JPA.
 * </p>
 */
@Entity
@Table(name = "phrases")
public class Phrase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String phrase;
    private String type;


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getPhrase() {
        return phrase;
    }
    public void setPhrase(String phrase) {
        this.phrase = phrase;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    
}
