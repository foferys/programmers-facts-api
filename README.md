programming funny quotes
=========
[![Contributors](https://img.shields.io/badge/contributors-1-46CC12)](#contributors-)
[![Java](https://img.shields.io/badge/Java-%23ED8B00.svg?logo=openjdk&logoColor=white)](#)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?logo=springboot&logoColor=fff)](#)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-4169E1?logo=postgresql&logoColor=fff)](#)
[![Git](https://img.shields.io/badge/Git-F05032?logo=git&logoColor=fff)](#)

[![pp](https://img.shields.io/badge/Buy_me_a_coffee-3775A9?logo=paypal)](https://www.paypal.com/paypalme/foferys)
[![Hugging Face](https://img.shields.io/badge/Hugging%20Face-FFD21E?logo=huggingface&logoColor=000)](#Dedication-)


## Description

API REST in sola lettura che espone **citazioni/fatti divertenti** sui programmatori (tipi: `backend`, `frontend`, `generic`) in JSON. Base URL: `http://localhost:8080/api/v1/phrases`. Documentazione dettagliata e stato dei miglioramenti: vedi [PDR-Programmers-Facts-API.md](./PDR-Programmers-Facts-API.md).

### Setup (variabili d'ambiente)

Le credenziali del database **non** sono nel repository. Prima di avviare l'app o Docker:

1. Copy `.env.example` to `.env`:  
   `cp .env.example .env` (Linux/macOS) or rename/copy by hand on Windows.
2. Open `.env` and enter values ​​for:
- **Docker**: `POSTGRES_USER`, `POSTGRES_PASSWORD`, `POSTGRES_DB` (required for `docker-compose`).
- **Local Execution** (Spring without Docker): `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` (e.g., `jdbc:postgresql://localhost:5434/programmers-api` if Postgres is running locally on port 5434).

Never commit the `.env` file (it's in `.gitignore`).

### Key endpoints (base: `/api/v1/phrases`)

| Metodo | Endpoint | Descrizione |
|--------|----------|-------------|
| GET | `/all` | All sentences |
| GET | `/random` | Una frase casuale |
| GET | `/random-explicit` | A random phrase (404 se no phrase) |
| GET | `/{type}` |Phrases by type (frontend, backend, generic); invalid type → "generic" |
| GET | `/by-type?type=backend` | Phrases for type (query param; type required and valid, otherwise 400) |
| GET | `/id/{id}` | Phrase for ID (404 if nonexistent) |
| GET | `/ping` | Health check (204 No Content) |

In case of error the response is JSON: `{ "error": "...", "code": "400|404|500", "timestamp": "..." }`.

### Example Usage

```bash
curl http://localhost:8080/api/v1/phrases/random
```

Response:

```json
{
    "data": {
        "id": 24,
        "phrase": "The first rule of debugging: Don’t make it worse.",
        "type": "generic"
    }
}
```


### Advanced Usage

<!-- You can request more than one funny fact at a time by using the GET param `count`

```bash
curl http://localhost:8080/api/v1/phrases/random?count=3
```

Response

```json
{
  "data": [
    "0": 	"Mother cats teach their kittens to use the litter box.",
    "1": "A cat can sprint at about thirty-one miles per hour.",
    "2": "The worlds richest cat is worth $13 million after his human passed away and left her fortune to him."
  ]
}
``` -->

you can retrieve a list of quotes filtered by a specific type (backend, frontend, or generic).


```bash
curl http://localhost:8080/api/v1/phrases/by-type?type=backend  
```

Response

```json
{
    "data": [
        {
            "id": 1,
            "phrase": "Backend developers always say, 'It worked on my local server.'",
            "type": "backend"
        },
        {
            "id": 4,
            "phrase": "Backend developers don’t fear downtime; they fear 'urgent deployments.'",
            "type": "backend"
        }
    ]
}

```


### Dedication

<p>This API serves up quirky quotes that programmers can’t debug away! </p>
<img width="500" height="500" alt="my coding backpack in ASCII ART" src="./bkp_db/backpack_ascii.png" />
<p>If you enjoy this api, or just love programming, please donate to:</p>

[![pp](https://img.shields.io/badge/Donate-3775A9?logo=paypal)](https://www.paypal.com/paypalme/foferys)


## Contributors ✨ 
[![](https://img.shields.io/badge/contributors-1-46CC12)](# "Contributors")

<!-- Thanks goes to these wonderful people -->

<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tbody>
    <tr>
      <td align="center" valign="top" width="14.28%"><a href="https://github.com/gianpieroferraro"><img src="https://avatars.githubusercontent.com/u/123701797?v=4" width="100px;" alt="Gianpiero Ferraro"/><br /><sub><b>Gianpiero Ferraro</b>(always me lol)</sub></a><br />
      </td>
    </tr>
</tbody>
</table>
