# PDR – Programmers Facts API

## 1. Analisi: è un’API REST?

**Sì.** Il progetto espone un’**API REST** per le risorse “frasi/citazioni” (phrases).

- **REST nel codice**: `FraseController` è annotato con `@RestController` e `@RequestMapping("/getPhrase")`; i metodi rispondono in **JSON** (serializzazione automatica da Spring), senza sessioni (stateless).
- **Risorse**: le URL rappresentano risorse (tutte le frasi, per tipo, una casuale, per ID).
- **Verbo HTTP**: viene usato **GET** per le operazioni di lettura (nessun POST/PUT/DELETE).

È quindi un’API REST **solo in lettura** (read-only), adatta a servire citazioni/fatti divertenti sui programmatori.

---

## 2. Cosa andrebbe fatto meglio (miglioramenti)

| Area | Situazione attuale | Suggerimento |
|------|--------------------|--------------|
| **URL e naming** | Base path `/getPhrase` (verbo nell’URL) | Usare sostantivi: es. `/api/v1/phrases`. Le risorse REST sono identificate da nomi, non da azioni. |
| **Versioning** | Nessuna versione nell’URL | Introdurre `/api/v1/phrases` (o simile) per evoluzioni future senza rompere i client. |
| **README vs codice** | README indica `/v1/phrases/random` | Allineare README (e eventuale Postman) agli endpoint reali (`/getPhrase/...`) oppure cambiare il codice per usare `/api/v1/phrases`. |
| **Solo GET** | Nessun POST/PUT/DELETE | Per un’API “solo citazioni” va bene; se un giorno servisse gestione (admin), aggiungere POST/PUT/DELETE su `/phrases` con autenticazione. |
| **Paginazione** | `GET /getPhrase/all` restituisce tutte le frasi | Per dataset grandi: parametri `page` e `size` (es. Spring `Pageable`) e risposta con `content`, `totalElements`, `totalPages`. |
| **Validazione tipo** | Controllo solo su lunghezza stringa (`type.length() < 5`) | Validare contro valori ammessi: `frontend`, `backend`, `generic` (enum o whitelist) e restituire 400 per tipo non valido. |
| **Modello esposto** | Si espone direttamente l’entity JPA `Phrase` | Preferibile usare **DTO** (Data Transfer Object) per l’API, così da non legare il contratto all’entità e nascondere eventuali campi interni. |
| **Errori** | Nessun gestore globale degli errori | Aggiungere `@ControllerAdvice` + `@ExceptionHandler` per risposte JSON uniformi (es. 404, 400, 500) con formato tipo `{ "error": "...", "code": "..." }`. |
| **Documentazione API** | Solo pagina HTML e README | Aggiungere **OpenAPI (Swagger)** per documentazione e client generati automaticamente. |
| **Organizzazione pacchetti** | `PhraseRepository` in `services` | Spostare in pacchetto `repository`: è un JPA Repository, non un servizio applicativo. |
| **Ambiente** | Credenziali DB in `compose.yaml` e in `application.properties` | Per produzione: usare variabili d’ambiente o secret; evitare password in chiaro nel repository. |

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

### 4.2 Endpoint (base: `http://localhost:8080/getPhrase`)

| Metodo | Endpoint | Descrizione | Risposta tipica |
|--------|----------|-------------|-----------------|
| GET | `/getPhrase/all` | Tutte le frasi | `200` – `{ "data": [ { "id", "phrase", "type" }, ... ] }` |
| GET | `/getPhrase/random` | Una frase casuale | `200` – `{ "data": { "id", "phrase", "type" } }` |
| GET | `/getPhrase/random-explicit` | Una frase casuale (status esplicito) | `200` con body come sopra; `404` se nessuna frase |
| GET | `/getPhrase/{type}` | Frasi per tipo (es. frontend, backend, generic) | `200` – `{ "data": [ ... ] }`. Se type assente/invalido viene usato "generic". |
| GET | `/getPhrase/by-type?type=backend` | Frasi per tipo (query param) | `200` come sopra; `400` se `type` mancante o &lt; 3 caratteri |
| GET | `/getPhrase/id/{id}` | Frase per ID numerico | `200` – `{ "data": { "id", "phrase", "type" } }`; `404` se ID inesistente |
| GET | `/getPhrase/ping` | Health/ping | `204` No Content |

### 4.3 Esempi di chiamata

**Browser / strumenti HTTP**  
- `http://localhost:8080/getPhrase/random`  
- `http://localhost:8080/getPhrase/frontend`  
- `http://localhost:8080/getPhrase/all`  
- `http://localhost:8080/getPhrase/id/1`  

**cURL**  
```bash
curl http://localhost:8080/getPhrase/random
curl "http://localhost:8080/getPhrase/by-type?type=backend"
curl http://localhost:8080/getPhrase/all
```

### 4.4 Formato risposta (successo)

- **Singola frase**: `{ "data": { "id": 24, "phrase": "...", "type": "generic" } }`
- **Lista**: `{ "data": [ { "id": 1, "phrase": "...", "type": "backend" }, ... ] }`

In caso di errore non c’è ancora un formato standardizzato (vedi miglioramenti: gestione errori globale e OpenAPI).

### 4.5 Documentazione e UI

- **Pagina principale**: `http://localhost:8080/`  
  - Mostra una citazione casuale e la documentazione essenziale degli endpoint (GET random, per tipo, all).  
- **README**: esempi e descrizione generale; da allineare agli URL effettivi (`/getPhrase/...`).

---

## 5. Riepilogo

- **È un’API REST** (solo lettura, JSON, stateless).
- **Cosa fa**: espone citazioni divertenti per programmatori (backend/frontend/generic) da PostgreSQL.
- **Come si usa**: avvio con Spring Boot (locale o Docker dev/prod), poi GET su `/getPhrase/...` come in tabella e negli esempi; la root `/` offre la pagina con docs.
- **Miglioramenti consigliati**: URL tipo `/api/v1/phrases`, versioning, paginazione su “all”, validazione tipo, DTO, gestione errori centralizzata, OpenAPI e allineamento README/codice.
