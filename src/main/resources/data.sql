-- =============================================================================
-- data.sql – Dati iniziali per sviluppo locale (senza Docker)
-- =============================================================================
-- A cosa serve data.sql
-- Uso: sviluppo locale (app avviata senza Docker, es. da IDE).
-- Comportamento: Spring Boot lo esegue dopo che JPA ha creato/aggiornato le tabelle e inserisce le 25 frasi nella tabella phrases.
-- Quando non viene usato: in Docker il database è già inizializzato da init-db/, quindi con il profilo docker l’esecuzione di data.sql è disattivata per evitare duplicati.
-- Quindi non va eliminato: serve per avere dati iniziali quando lavori in locale contro un Postgres vuoto.
-- =============================================================================
-- Spring Boot esegue questo file automaticamente se spring.sql.init.mode=always
-- e spring.jpa.defer-datasource-initialization=true (v. application.properties).
-- Inserisce le frasi nella tabella "phrases" dopo che JPA/Hibernate ha creato
-- lo schema. Usato quando avvii l'app in locale contro un DB Postgres vuoto.
-- Con Docker il DB è già popolato da init-db/, quindi questo file non viene
-- eseguito (profilo "docker" imposta spring.sql.init.mode=never).
-- =============================================================================

INSERT INTO phrases (phrase, type) VALUES
('Backend developers always say, "It worked on my local server."', 'backend'),
('Frontend is 90% making things pretty and 10% crying over browser compatibility.', 'frontend'),
('Generic coding advice: if it works, don''t ask why.', 'generic'),
('The backend is like a restaurant kitchen: no one sees it, but it''s where the magic happens.', 'backend'),
('Frontend without animations is like pizza without cheese.', 'frontend'),
('The only thing consistent in programming is inconsistency.', 'generic'),
('Backend developers can solve any problem, as long as it doesn''t involve CSS.', 'backend'),
('Frontend developers always say, "It looked fine on my machine."', 'frontend'),
('The golden rule of programming: If it''s working, don''t touch it.', 'generic'),
('Backend errors are like ghosts; they appear at night and disappear when you call for help.', 'backend'),
('A frontend developer''s worst nightmare? A client saying, "Can we make it pop more?"', 'frontend'),
('Programming is 1% writing code and 99% figuring out what you wrote last week.', 'generic'),
('Backend developers hate "quick fixes" as much as cats hate baths.', 'backend'),
('Frontend developers speak fluent "hexadecimal."', 'frontend'),
('Programming is the art of turning coffee into stack traces.', 'generic'),
('Backend developers don''t fear downtime; they fear "urgent deployments."', 'backend'),
('Frontend debugging is like playing hide and seek with invisible errors.', 'frontend'),
('The real enemy in programming is the "off by one" error.', 'generic'),
('Backend developers believe in one god: the database.', 'backend'),
('Frontend without JavaScript is like a car without wheels.', 'frontend'),
('The most dangerous phrase in programming: "This will only take a minute."', 'generic'),
('Backend developers know that the fastest query is the one you don''t run.', 'backend'),
('Frontend developers measure success in pixels and frustration.', 'frontend'),
('The first rule of debugging: Don''t make it worse.', 'generic'),
('Backend developers are the unsung heroes of 404 errors.', 'backend');