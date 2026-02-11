# TESTING_TUTORIAL_JUNIOR

Guida pratica, passo-passo, per aggiungere e mantenere test in questo progetto Spring Boot REST API.

## 0) Obiettivo
Capire e replicare il setup di test del repository usando `mvn test`.

Copriamo tre livelli:
1. Controller: `@WebMvcTest + MockMvc`
2. Service: unit test puro con Mockito
3. Repository: `@DataJpaTest`

---

## 1) Struttura package test da seguire

Mantieni il mirror dei package di produzione:
- Produzione: `src/main/java/com/prova_api/controller/...`
- Test: `src/test/java/com/prova_api/controller/...`

Layout attuale:
- `src/test/java/com/prova_api/controller/FraseControllerTest.java`
- `src/test/java/com/prova_api/services/PhraseServiceTest.java`
- `src/test/java/com/prova_api/repository/PhraseRepositoryTest.java`
- `src/test/resources/application.properties`
- `src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker`

Convenzione nomi:
- classe test: `<Classe>Test`
- metodo test: `azioneScenarioRisultatoAtteso`

Esempi reali:
- `getRandomReturnsNoStoreHeader`
- `findByTypeThrowsDomainExceptionWhenTypeIsInvalid`

---

## 2) Dipendenze Maven per test

Nel file `pom.xml` assicurati di avere:

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-test</artifactId>
  <scope>test</scope>
</dependency>

<dependency>
  <groupId>com.h2database</groupId>
  <artifactId>h2</artifactId>
  <scope>test</scope>
</dependency>
```

Note:
- `spring-boot-starter-test` include JUnit 5, Mockito, AssertJ, Spring Test.
- H2 serve per test repository in-memory senza Postgres locale.

---

## 3) Configurazione test indipendenti (fondamentale)

Crea/aggiorna `src/test/resources/application.properties`:

```properties
spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false
spring.sql.init.mode=never
spring.jpa.defer-datasource-initialization=true
```

Perche' serve:
- isola i test da DB esterni
- non usa i segreti/env della app reale
- non dipende da `data.sql` di produzione

Compatibilita' Java/Mockito:
- file: `src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker`
- contenuto:

```text
mock-maker-subclass
```

Se non lo metti, con alcune combinazioni Java+Mockito i `@MockBean` possono fallire.

---

## 4) Controller test con `@WebMvcTest + MockMvc`

Obiettivo: verificare endpoint, status code, JSON e header HTTP.

### Esempio reale (dal repo)
`src/test/java/com/prova_api/controller/FraseControllerTest.java`

```java
@WebMvcTest(FraseController.class)
class FraseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PhraseService phraseService;

    @Test
    void getAllReturnsWrappedData() throws Exception {
        when(phraseService.findAll()).thenReturn(List.of(
                new PhraseResponseDto(1, "Keep it simple", "generic")
        ));

        mockMvc.perform(get("/api/v1/phrases/all").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1));

        verify(phraseService).findAll();
    }
}
```

### Checklist controller test
1. Usa `@WebMvcTest(ControllerClass.class)`
2. Inietta `MockMvc`
3. Mocka dipendenze controller con `@MockBean`
4. Esegui request con `mockMvc.perform(...)`
5. Verifica:
   - status (`isOk`, `isBadRequest`, ...)
   - body JSON (`jsonPath`)
   - header (`header().string(...)`)
6. Verifica chiamate service (`verify(...)`)

### Validazione ed errori HTTP
Esempio reale:
- endpoint `/api/v1/phrases/by-type` senza `type`
- service mockato che lancia `InvalidPhraseTypeException`
- assertion su risposta `400` con JSON errore:
  - `$.code == "400"`
  - `$.error == "Parametro 'type' obbligatorio"`

---

## 5) Service unit test con Mockito

Obiettivo: testare logica business senza Spring context.

### Esempio reale
`src/test/java/com/prova_api/services/PhraseServiceTest.java`

```java
@ExtendWith(MockitoExtension.class)
class PhraseServiceTest {

    @Mock
    private PhraseRepository phraseRepository;

    @InjectMocks
    private PhraseService phraseService;

    @Test
    void findByTypeThrowsDomainExceptionWhenTypeIsInvalid() {
        assertThrows(InvalidPhraseTypeException.class, () -> phraseService.findByType("mobile"));
        verify(phraseRepository, never()).findByType(anyString());
    }
}
```

### Cosa testare nel service
- percorso positivo (dato valido)
- percorso negativo (eccezione attesa)
- fallback/default
- mapping DTO
- interazioni con repository (`verify`, `never`)

### Pattern consigliato
AAA (Arrange / Act / Assert) in ogni test, con blocchi netti.

---

## 6) Repository test con `@DataJpaTest`

Obiettivo: testare query JPA/native query reali con DB embedded.

### Esempio reale
`src/test/java/com/prova_api/repository/PhraseRepositoryTest.java`

```java
@DataJpaTest
class PhraseRepositoryTest {

    @Autowired
    private PhraseRepository phraseRepository;

    @Test
    void findByTypeReturnsOnlyMatchingRows() {
        phraseRepository.save(buildPhrase("UI first", "frontend"));
        phraseRepository.save(buildPhrase("API first", "backend"));

        List<Phrase> frontend = phraseRepository.findByType("frontend");

        assertEquals(1, frontend.size());
        assertEquals("frontend", frontend.get(0).getType());
    }
}
```

Quando usarlo:
- c'e' logica SQL/JPA da verificare
- vuoi testare query custom/native

Quando NON usarlo:
- se vuoi solo testare logica di business (usa unit test service)

---

## 7) JSON assertions, eccezioni e status code

Regole pratiche:
- Leggi sempre il contratto JSON reale del progetto.
- In questo repo le risposte errore usano `ApiError`:
  - campo `error`
  - campo `code`

Esempio assertion corretta:
```java
.andExpect(status().isBadRequest())
.andExpect(jsonPath("$.code").value("400"))
.andExpect(jsonPath("$.error").value("Parametro 'type' obbligatorio"));
```

Errore comune (anti-pattern):
- assertare campi non presenti (`$.status`, `$.message`) senza controllare `ApiError`.

---

## 8) Eseguire i test

Comando base:
```bash
mvn test
```

Comandi utili:
- singola classe:
```bash
mvn -Dtest=FraseControllerTest test
```
- singolo metodo:
```bash
mvn -Dtest=PhraseServiceTest#findByIdThrowsWhenMissing test
```
- debug Maven esteso:
```bash
mvn -e -X test
```

Report Surefire:
- `target/surefire-reports/*.txt`
- `target/surefire-reports/TEST-*.xml`

---

## 9) Come aggiungere nuovi test in futuro

Flow raccomandato:
1. Identifica layer da testare:
   - contratto HTTP -> controller test
   - logica business -> service unit test
   - query DB -> repository test
2. Crea file test nel package mirror.
3. Applica AAA e naming leggibile.
4. Esegui `mvn test`.
5. Se fallisce:
   - leggi surefire report
   - correggi test o codice in base al contratto reale
6. Aggiorna `TESTING_REPORT.md` con nuova copertura.

---

## 10) Best practice e anti-pattern

## Best practice
- Usa il test piu' piccolo possibile per lo scopo.
- Mantieni i test deterministici (niente rete, niente clock non controllato).
- Verifica sia output che comportamento (es. `verify(...)`).
- Tieni i fixture semplici e locali al test.

## Anti-pattern
- Usare sempre `@SpringBootTest` anche per logica banale.
- Test dipendenti da database esterno o dati ambientali.
- Assert troppo deboli (es. solo status senza verificare body).
- Nomi test vaghi (`test1`, `works`).

---

## 11) Mini checklist prima di aprire PR
- [ ] `mvn test` verde
- [ ] nuovi test in package corretto
- [ ] naming chiaro e coerente
- [ ] nessuna dipendenza esterna nei test
- [ ] report aggiornato (`TESTING_REPORT.md`)

