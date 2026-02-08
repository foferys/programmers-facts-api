# PDR – Programmers Facts API

## 1. Analisi: è un’API REST?

**Sì.** Il progetto espone un’**API REST** per le risorse “frasi/citazioni” (phrases).

- **REST nel codice**: `FraseController` è annotato con `@RestController` e `@RequestMapping("/api/v1/phrases")`; i metodi rispondono in **JSON** (serializzazione automatica da Spring), senza sessioni (stateless).
- **Risorse**: le URL rappresentano risorse (tutte le frasi, per tipo, una casuale, per ID).
- **Verbo HTTP**: viene usato **GET** per le operazioni di lettura (nessun POST/PUT/DELETE).

È quindi un’API REST **solo in lettura** (read-only), adatta a servire citazioni/fatti divertenti sui programmatori.

---

## 2. Cosa andrebbe fatto meglio (miglioramenti)

| Area | Situazione attuale | Suggerimento | Stato |
|------|--------------------|--------------|--------|
| **URL e naming** | Base path `/api/v1/phrases` (sostantivo + versioning) ✓ | Applicato. | ✓ |
| **Versioning** | Versione nell’URL: /api/v1/ ✓ | Applicato. | ✓ |
| **README vs codice** | README indica `/v1/phrases/random` | Allineare README agli endpoint reali (`/api/v1/phrases/...`). | ✓ (README aggiornato) |
| **Solo GET** | Nessun POST/PUT/DELETE | Per un’API “solo citazioni” va bene; se un giorno servisse gestione (admin), aggiungere POST/PUT/DELETE su `/phrases` con autenticazione. | — |
| **Paginazione** | `GET /api/v1/phrases/all` restituisce tutte le frasi | Per dataset grandi: parametri `page` e `size` (es. Spring `Pageable`) e risposta con `content`, `totalElements`, `totalPages`. | Da fare |
| **Validazione tipo** | Controllo solo su lunghezza stringa | Validare contro valori ammessi: `frontend`, `backend`, `generic` (enum o whitelist) e restituire 400 per tipo non valido. | ✓ (enum `PhraseType`) |
| **Modello esposto** | Si espone direttamente l’entity JPA `Phrase` | Usare DTO per l’API. | ✓ (DTO + ApiResponse) |
| **Errori** | Nessun gestore globale degli errori | `@ControllerAdvice` + `@ExceptionHandler` per risposte JSON uniformi. | ✓ (GlobalExceptionHandler + ApiError) |
| **Documentazione API** | Solo pagina HTML e README | Aggiungere **OpenAPI (Swagger)** per documentazione e client generati automaticamente. | Da fare |
| **Organizzazione pacchetti** | `PhraseRepository` in `services` | Spostare in pacchetto `repository`. | ✓ |
| **Ambiente** | Credenziali DB in `compose.yaml` e in `application.properties` | Per produzione: usare variabili d’ambiente o secret; evitare password in chiaro nel repository. | — |

### 2.1 Miglioramenti già applicati (stato attuale)

- **DTO e non esposizione dell’entity JPA**: l’API restituisce sempre `PhraseResponseDto` e il wrapper `ApiResponse<T>`. L’entità `Phrase` è usata solo in repository e service.
- **Exception handling centralizzato**: `GlobalExceptionHandler` (`@RestControllerAdvice`) gestisce `PhraseNotFoundException` (404), `InvalidPhraseTypeException` (400), `MethodArgumentTypeMismatchException` (400), `IllegalArgumentException` (400) e `Exception` (500). Formato errore: `{ "error": "...", "code": "...", "timestamp": "..." }` (`ApiError`).
- **Validazione tipo**: enum `PhraseType` (frontend, backend, generic); tipo non valido o mancante (dove richiesto) → 400 con messaggio chiaro.
- **Organizzazione pacchetti**: `PhraseRepository` spostato in `repository`; logica applicativa in `PhraseService` (servizi); DTO in `dto`, eccezioni in `exception`, enum in `model`.
- **Commenti nel codice**: classi e metodi principali documentati (scopo, comportamento, eccezioni).
- **Try-with-resources**: utilizzato dove servono risorse `AutoCloseable`; nel flusso attuale (solo DB e JSON) non ci sono stream/file da chiudere esplicitamente.

---

## 3. Cosa fa questa API

- **Scopo**: servizio che espone **citazioni/fatti divertenti** sui programmatori (backend, frontend, generici) in formato JSON.
- **Dati**: tabella `phrases` (PostgreSQL) con campi `id`, `phrase`, `type`; tipi ammessi: `backend`, `frontend`, `generic`.
- **Funzionalità**:
  - Restituire **tutte** le frasi.
  - Restituire frasi **filtrate per tipo** (frontend, backend, generic).
  - Restituire **una frase casuale**.
  - Restituire **una frase per ID**.
  - Endpoint di **ping** (es. 204 No Content) e varianti con status espliciti (es. random con 404 se nessuna frase).
- **Interfaccia utente**: pagina iniziale (Thymeleaf) su `/` con una citazione casuale e documentazione degli endpoint principali.

---

## 4. Come si usa l’API

### 4.1 Avvio

**Variabili d'ambiente:** copia `.env.example` in `.env` e compila le credenziali (vedi README). Nessun dato sensibile in repository.

- **Locale (senza Docker)**  
  - Avviare PostgreSQL (es. sulla porta 5434, come in `application.properties`).  
  - Eseguire l’applicazione Spring Boot (IDE o `./mvnw spring-boot:run`).  
  - Base URL: `http://localhost:8080`.

- **Docker (sviluppo)**  
  - `docker-compose --profile dev up` (prima volta: `--build`).  
  - Stessa base URL; volumi su `templates` e `static` per modifiche senza rebuild.

- **Docker (produzione)**  
  - `docker-compose --profile prod up` (prima volta: `--build`).  
  - Include solo server + Postgres (e opzionalmente pgAdmin).

### 4.2 Endpoint (base: `http://localhost:8080/api/v1/phrases`)

| Metodo | Endpoint | Descrizione | Risposta tipica |
|--------|----------|-------------|-----------------|
| GET | `/api/v1/phrases/all` | Tutte le frasi | `200` – `{ "data": [ { "id", "phrase", "type" }, ... ] }` |
| GET | `/api/v1/phrases/random` | Una frase casuale | `200` – `{ "data": { "id", "phrase", "type" } }` |
| GET | `/api/v1/phrases/random-explicit` | Una frase casuale (status esplicito) | `200` con body come sopra; `404` se nessuna frase |
| GET | `/api/v1/phrases/{type}` | Frasi per tipo (es. frontend, backend, generic) | `200` – `{ "data": [ ... ] }`. Se type assente/invalido viene usato "generic". |
| GET | `/api/v1/phrases/by-type?type=backend` | Frasi per tipo (query param) | `200` come sopra; `400` se `type` mancante o &lt; 3 caratteri |
| GET | `/api/v1/phrases/id/{id}` | Frase per ID numerico | `200` – `{ "data": { "id", "phrase", "type" } }`; `404` se ID inesistente |
| GET | `/api/v1/phrases/ping` | Health/ping | `204` No Content |

### 4.3 Esempi di chiamata

**Browser / strumenti HTTP**  
- `http://localhost:8080/api/v1/phrases/random`  
- `http://localhost:8080/api/v1/phrases/frontend`  
- `http://localhost:8080/api/v1/phrases/all`  
- `http://localhost:8080/api/v1/phrases/id/1`  

**cURL**  
```bash
curl http://localhost:8080/api/v1/phrases/random
curl "http://localhost:8080/api/v1/phrases/by-type?type=backend"
curl http://localhost:8080/api/v1/phrases/all
```

### 4.4 Formato risposta (successo)

- **Singola frase**: `{ "data": { "id": 24, "phrase": "...", "type": "generic" } }`
- **Lista**: `{ "data": [ { "id": 1, "phrase": "...", "type": "backend" }, ... ] }`

**Formato risposta in caso di errore** (gestione centralizzata):

- **Body**: `{ "error": "messaggio leggibile", "code": "400|404|500", "timestamp": "2025-02-08T..." }`
- **Status**: 400 Bad Request (tipo non valido, parametri errati), 404 Not Found (frase/ID non trovato), 500 Internal Server Error (errore non gestito).

### 4.5 Documentazione e UI

- **Pagina principale**: `http://localhost:8080/`  
  - Mostra una citazione casuale e la documentazione essenziale degli endpoint (GET random, per tipo, all).  
- **README**: esempi e descrizione generale; URL allineati a `/api/v1/phrases/...`.

---

## 5. Riepilogo

- **È un’API REST** (solo lettura, JSON, stateless).
- **Cosa fa**: espone citazioni divertenti per programmatori (backend/frontend/generic) da PostgreSQL.
- **Come si usa**: avvio con Spring Boot (locale o Docker dev/prod), poi GET su `/api/v1/phrases/...` come in tabella e negli esempi; la root `/` offre la pagina con docs.
- **Già applicati**: DTO (nessuna entity JPA esposta), gestione errori centralizzata (GlobalExceptionHandler + ApiError), validazione tipo (PhraseType), repository in pacchetto dedicato, README allineato.
- **Ancora da considerare**: paginazione su “all”, OpenAPI (Swagger), credenziali in produzione via variabili d’ambiente/secret.
