# TESTING_REPORT

## 1) Obiettivo del lavoro
Costruire una suite di test reale e deterministica per la REST API Spring Boot in questo repository, coprendo i layer principali:
- Controller (`@WebMvcTest` + `MockMvc` + service mockato)
- Service (unit test puro con JUnit 5 + Mockito)
- Repository (`@DataJpaTest` con database in-memory)

L'obiettivo pratico e' eseguire tutto con `mvn test` senza dipendere da servizi esterni (es. PostgreSQL locale o Docker).

---

## 2) Cosa e' stato fatto (file creati/modificati)

### File creati
- `src/test/java/com/prova_api/controller/FraseControllerTest.java`
- `src/test/java/com/prova_api/services/PhraseServiceTest.java`
- `src/test/java/com/prova_api/repository/PhraseRepositoryTest.java`
- `src/test/resources/application.properties`
- `src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker`

### File modificati
- `pom.xml`
  - aggiunta dipendenza test H2:

```xml
<dependency>
  <groupId>com.h2database</groupId>
  <artifactId>h2</artifactId>
  <scope>test</scope>
</dependency>
```

### File preesistente mantenuto
- `src/test/java/com/prova_api/prova_api/ProvaApiApplicationTests.java`
  - smoke test del context (`@SpringBootTest`)

---

## 3) Perche' questi tipi di test

### `@WebMvcTest` per Controller
Scelta: testare solo web layer (routing, status code, JSON, exception handling), senza avviare l'intero contesto applicativo e senza database.

Motivo tecnico:
- veloce
- focalizzato sul contratto HTTP
- dipendenza del controller (`PhraseService`) mockata con `@MockBean`

### Mockito puro per Service
Scelta: test unitari senza Spring context.

Motivo tecnico:
- test rapidi e predicibili
- verifica diretta della logica business (`findByType`, fallback, eccezioni)
- controllo esplicito delle interazioni col repository (`verify`, `never`)

### `@DataJpaTest` per Repository
Scelta: testare query JPA/native query in isolamento.

Motivo tecnico:
- valida metodi repository reali (`findByType`, `findRandomPhrase`)
- usa DB embedded (H2) durante i test
- evita dipendenze da PostgreSQL esterno

---

## 4) Struttura dei test (pattern e convenzioni)

## Pattern AAA usato in ogni test
- **Arrange**: setup dati e mock (`when(...)`, `save(...)`)
- **Act**: chiamata al metodo o endpoint (`mockMvc.perform(...)`, `phraseService.findByType(...)`)
- **Assert**: verifiche (`assertEquals`, `assertThrows`, `jsonPath`, `verify`)

Esempio concreto (`PhraseServiceTest`):
```java
@Test
void findByTypeReturnsDtosWhenTypeIsValid() {
    Phrase phrase = phrase(10, "Frontend quote", "frontend"); // Arrange
    when(phraseRepository.findByType("frontend")).thenReturn(List.of(phrase));

    List<PhraseResponseDto> result = phraseService.findByType("FRONTEND"); // Act

    assertEquals(1, result.size()); // Assert
    assertEquals("frontend", result.get(0).getType());
    verify(phraseRepository).findByType("frontend");
}
```

## Naming convention
Formato usato: `metodoScenarioRisultatoAtteso`
- `getRandomReturnsNoStoreHeader`
- `findByTypeThrowsDomainExceptionWhenTypeIsInvalid`
- `findByTypeReturnsOnlyMatchingRows`

## Mock e setup
- Controller: `@MockBean PhraseService phraseService`
- Service: `@Mock PhraseRepository`, `@InjectMocks PhraseService`
- Repository: nessun mock (accesso reale a DB embedded)

---

## 5) Configurazione test deterministici

`src/test/resources/application.properties`:
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

Perche' cosi:
- H2 in-memory => niente DB esterno
- `MODE=PostgreSQL` => maggiore compatibilita' query SQL
- `ddl-auto=create-drop` => schema pulito ad ogni esecuzione
- `spring.sql.init.mode=never` => evita side effects da `data.sql` di produzione

Compatibilita' Mockito/Java:
- `src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker`
- contenuto: `mock-maker-subclass`

Questo evita problemi con inline mock maker su Java 25 e rende stabile il `@WebMvcTest` con `@MockBean`.

---

## 6) Diff summary

- **Modificato** `pom.xml`
  - aggiunta dipendenza test `com.h2database:h2`
- **Aggiunto** `src/test/java/com/prova_api/controller/FraseControllerTest.java`
- **Aggiunto** `src/test/java/com/prova_api/services/PhraseServiceTest.java`
- **Aggiunto** `src/test/java/com/prova_api/repository/PhraseRepositoryTest.java`
- **Aggiunto** `src/test/resources/application.properties`
- **Aggiunto** `src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker`

---

## 7) Comandi esatti eseguiti e output (riassunto)

## Fase 1: identificazione comando test
1. `./mvnw test`
   - esito: **errore**
   - output chiave: `zsh:1: permission denied: ./mvnw`
   - causa: wrapper non eseguibile in questa copia locale.

2. `mvn test`
   - esito: **errore iniziale in sandbox**
   - output chiave: impossibilita' di scrivere in `~/.m2` (`Operation not permitted`).
   - azione: esecuzione Maven fuori sandbox.

## Fase 2: test dopo aggiunta suite
3. `mvn test`
   - esito: **errore** su `FraseControllerTest`
   - output chiave: `Mockito cannot mock this class ... Java 25 ... Byte Buddy`
   - fix: aggiunto `mock-maker-subclass` in `src/test/resources/mockito-extensions/org.mockito.plugins.MockMaker`.

4. `mvn test`
   - esito: **errore** su assert JSON (`$.status` non presente)
   - output chiave: `No value at JSON path "$.status"`
   - causa: l'API error usa `error/code` e non `message/status`.
   - fix: aggiornate assert in `FraseControllerTest` su `$.code` e `$.error`.

5. `mvn test`
   - esito finale: **BUILD SUCCESS**
   - output chiave: `Tests run: 14, Failures: 0, Errors: 0, Skipped: 0`

---

## 8) Mappa copertura endpoint/feature -> test

| Endpoint / Feature | Tipo test | Classe test | Metodo test |
|---|---|---|---|
| `GET /api/v1/phrases/all` restituisce wrapper `data` | Web layer | `FraseControllerTest` | `getAllReturnsWrappedData` |
| `GET /api/v1/phrases/random` imposta header `Cache-Control: no-store` | Web layer | `FraseControllerTest` | `getRandomReturnsNoStoreHeader` |
| `GET /api/v1/phrases/by-type` senza `type` -> 400 | Web layer + exception handling | `FraseControllerTest` | `byTypeWithoutParamReturnsBadRequestFromGlobalHandler` |
| `GET /api/v1/phrases/id/not-a-number` -> 400 type mismatch | Web layer + exception handling | `FraseControllerTest` | `getByIdWithNonNumericIdReturnsBadRequest` |
| `findByType` converte e normalizza tipo | Service unit | `PhraseServiceTest` | `findByTypeReturnsDtosWhenTypeIsValid` |
| `findByType` tipo invalido -> `InvalidPhraseTypeException` | Service unit | `PhraseServiceTest` | `findByTypeThrowsDomainExceptionWhenTypeIsInvalid` |
| fallback tipo invalido -> `generic` | Service unit | `PhraseServiceTest` | `findByTypeOrDefaultFallsBackToGenericForInvalidType` |
| random senza dati -> `PhraseNotFoundException` | Service unit | `PhraseServiceTest` | `findRandomThrowsWhenRepositoryIsEmpty` |
| ricerca by id presente | Service unit | `PhraseServiceTest` | `findByIdReturnsDtoWhenPresent` |
| ricerca by id assente -> `PhraseNotFoundException` | Service unit | `PhraseServiceTest` | `findByIdThrowsWhenMissing` |
| validazione type blank/short -> eccezione | Service unit | `PhraseServiceTest` | `validateTypeRequiredRejectsBlankAndTooShortValues` |
| `findByType` su repository filtra correttamente | Data layer | `PhraseRepositoryTest` | `findByTypeReturnsOnlyMatchingRows` |
| `findRandomPhrase` ritorna un record esistente | Data layer | `PhraseRepositoryTest` | `findRandomPhraseReturnsExistingRow` |
| avvio context Spring Boot | Smoke | `ProvaApiApplicationTests` | `contextLoads` |

---

## 9) Come eseguire i test

Comando standard:
```bash
mvn test
```

Prerequisiti locali consigliati:
- JDK 17+ (nel run attuale: Java 25)
- Maven installato (`mvn -v`)
- rete disponibile alla prima esecuzione per dipendenze Maven

Note su profili/proprieta':
- Non e' richiesto profilo Spring specifico per i test.
- Le proprieta' in `src/test/resources/application.properties` sovrascrivono quelle di `src/main/resources/application.properties` durante i test.

Dati di test:
- Controller e Service: dati costruiti inline nei test (mock e factory method)
- Repository: dati inseriti con `phraseRepository.save(...)` dentro il test

---

## 10) Come leggere output e fare debug di test falliti

## Output Maven/surefire
- Report test in: `target/surefire-reports/`
- File utili:
  - `target/surefire-reports/com.prova_api.controller.FraseControllerTest.txt`
  - `target/surefire-reports/TEST-com.prova_api.controller.FraseControllerTest.xml`

## Debug veloce per singolo test
Eseguire solo una classe:
```bash
mvn -Dtest=FraseControllerTest test
```

Eseguire solo un metodo:
```bash
mvn -Dtest=FraseControllerTest#getAllReturnsWrappedData test
```

Log dettagliati Maven:
```bash
mvn -e -X test
```

## Casi reali incontrati e debug
- `permission denied: ./mvnw`
  - soluzione: usare `mvn test` oppure rendere eseguibile `mvnw`.
- errore Mockito/ByteBuddy su Java 25
  - soluzione: `mock-maker-subclass` in `src/test/resources/mockito-extensions`.
- mismatch JSON assert (`status` vs `code`)
  - soluzione: allineare `jsonPath` ai campi reali di `ApiError` (`error`, `code`).

---

## 11) Limitazioni e TODO

Limitazioni attuali:
- `PhraseRepositoryTest` usa H2 + `MODE=PostgreSQL`, utile ma non identico al 100% a PostgreSQL reale.
- Non c'e' coverage report automatico (JaCoCo non configurato).
- `ProvaApiApplicationTests` e' solo smoke test del context.

TODO suggeriti:
- aggiungere JaCoCo e soglie minime di coverage.
- aggiungere test per endpoint non ancora coperti (es. `/random-explicit`, `/ping`, `/api/v1/phrases/{type}`).
- aggiungere test parametrizzati per validazione `type`.
- valutare test di integrazione end-to-end con profilo dedicato e Testcontainers PostgreSQL.

---

## 12) Stato finale verificato
Ultima esecuzione:
```bash
mvn test
```
Risultato:
- `BUILD SUCCESS`
- `Tests run: 14, Failures: 0, Errors: 0, Skipped: 0`
