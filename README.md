# README — Build & Deploy Guide

## Indice

* [1. Project Overview](#1-project-overview)

  * [1.1 Descrizione sintetica del sistema di Flight Monitoring](#11-descrizione-sintetica-del-sistema-di-flight-monitoring)
  * [1.2 Microservizi coinvolti](#12-microservizi-coinvolti)

    * [1.2.1 User Manager Service](#121-user-manager-service)
    * [1.2.2 Data Collector Service](#122-data-collector-service)
  * [1.3 Componenti esterni](#13-componenti-esterni)

    * [1.3.1 OpenSky Network API](#131-opensky-network-api)
    * [1.3.2 Database PostgreSQL](#132-database-postgresql)
  * [1.4 Riferimenti alla documentazione di progetto](#14-riferimenti-alla-documentazione-di-progetto)

    * [1.4.1 Relazione tecnica (`documentation/written_report.pdf`)](#141-relazione-tecnica-documentationwrittenreportpdf)
    * [1.4.2 Diagrammi architetturali e di sequenza](#142-diagrammi-architetturali-e-di-sequenza)
    * [1.4.3 Diagramma Entity–Relationship (ER)](#143-diagramma-entityrelationship-er)
* [2. Repository Structure](#2-repository-structure)

  * [2.1 Root layout della repository](#21-root-layout-della-repository)
  * [2.2 Struttura delle cartelle principali](#22-struttura-delle-cartelle-principali)
  * [2.3 File chiave per build & deploy](#23-file-chiave-per-build-deploy)
* [3. Prerequisites](#3-prerequisites)

  * [3.1 Requisiti hardware e sistema operativo](#31-requisiti-hardware-e-sistema-operativo)
  * [3.2 Software necessario](#32-software-necessario)

    * [3.2.1 Docker](#321-docker)
    * [3.2.2 Docker Compose](#322-docker-compose)
    * [3.2.3 JDK (per build/esecuzione locale senza Docker)](#323-jdk-per-buildesecuzione-locale-senza-docker)
    * [3.2.4 Maven (per build/esecuzione locale senza Docker)](#324-maven-per-buildesecuzione-locale-senza-docker)
  * [3.3 Account e credenziali esterne](#33-account-e-credenziali-esterne)

    * [3.3.1 Registrazione a OpenSky Network](#331-registrazione-a-opensky-network)
    * [3.3.2 Ottenimento delle credenziali OAuth2 (client id/secret)](#332-ottenimento-delle-credenziali-oauth2-client-idsecret)
  * [3.4 Verifica installazione dei prerequisiti](#34-verifica-installazione-dei-prerequisiti)
* [4. Configuration](#4-configuration)

  * [4.1 Strategie di configurazione (env-based configuration)](#41-strategie-di-configurazione-env-based-configuration)
  * [4.2 File `.env` e variabili d’ambiente](#42-file-env-e-variabili-dambiente)

    * [4.2.1 File `.env` a livello di `docker/`](#421-file-env-a-livello-di-docker)
    * [4.2.2 File `.env` specifici per i servizi (se presenti)](#422-file-env-specifici-per-i-servizi-se-presenti)
  * [4.3 Configurazione del database PostgreSQL](#43-configurazione-del-database-postgresql)

    * [4.3.1 Parametri di connessione (host, port, user, password)](#431-parametri-di-connessione-host-port-user-password)
    * [4.3.2 Database logici: User DB e Data DB](#432-database-logici-user-db-e-data-db)
  * [4.4 Configurazione delle credenziali OpenSky](#44-configurazione-delle-credenziali-opensky)

    * [4.4.1 Variabili d’ambiente per client id e secret](#441-variabili-dambiente-per-client-id-e-secret)
    * [4.4.2 Gestione sicura delle credenziali (placeholder vs valori reali)](#442-gestione-sicura-delle-credenziali-placeholder-vs-valori-reali)
  * [4.5 Profili e configurazioni Spring Boot](#45-profili-e-configurazioni-spring-boot)

    * [4.5.1 Uso del profilo di default e overriding tramite variabili d’ambiente](#451-uso-del-profilo-di-default-e-overriding-tramite-variabili-dambiente)
    * [4.5.2 Estensioni opzionali con profili dedicati](#452-estensioni-opzionali-con-profili-dedicati)
* [5. Build Instructions](#5-build-instructions)

  * [5.1 Build tramite Docker (modalità raccomandata)](#51-build-tramite-docker-modalita-raccomandata)

    * [5.1.1 Posizionamento nella cartella corretta](#511-posizionamento-nella-cartella-corretta)
    * [5.1.2 Comando per buildare le immagini con Docker Compose](#512-comando-per-buildare-le-immagini-con-docker-compose)
    * [5.1.3 Descrizione dei Dockerfile dei microservizi (multi-stage build)](#513-descrizione-dei-dockerfile-dei-microservizi-multi-stage-build)
  * [5.2 Build locale senza Docker (opzionale)](#52-build-locale-senza-docker-opzionale)

    * [5.2.1 Build dello User Manager Service con Maven](#521-build-dello-user-manager-service-con-maven)
    * [5.2.2 Build del Data Collector Service con Maven](#522-build-del-data-collector-service-con-maven)
    * [5.2.3 Differenze rispetto alla modalità Docker-based](#523-differenze-rispetto-alla-modalita-docker-based)
* [6. Deploy & Run with Docker Compose](#6-deploy-run-with-docker-compose)

  * [6.1 Prima esecuzione del sistema](#61-prima-esecuzione-del-sistema)

    * [6.1.1 Preparazione dei file `.env`](#611-preparazione-dei-file-env)
    * [6.1.2 Avvio dei servizi (`docker compose up -d` / `docker compose up --build`)](#612-avvio-dei-servizi-docker-compose-up-d-docker-compose-up-build)
    * [6.1.3 Verifica che i container siano in esecuzione](#613-verifica-che-i-container-siano-in-esecuzione)
  * [6.2 Arresto del sistema](#62-arresto-del-sistema)

    * [6.2.1 Comando di stop (`docker compose down`)](#621-comando-di-stop-docker-compose-down)
    * [6.2.2 Rimozione volumi/persistenza (se necessario)](#622-rimozione-volumipersistenza-se-necessario)
  * [6.3 Comandi Docker utili](#63-comandi-docker-utili)

    * [6.3.1 Visualizzazione log di un singolo servizio](#631-visualizzazione-log-di-un-singolo-servizio)
    * [6.3.2 Accesso alla shell di un container](#632-accesso-alla-shell-di-un-container)
    * [6.3.3 Verifica delle porte esposte](#633-verifica-delle-porte-esposte)
* [7. Deploy & Run with Local Microservices and Dockerized PostgreSQL](#7-deploy-run-with-local-microservices-and-dockerized-postgresql)

  * [7.1 Avvio di PostgreSQL in Docker (solo database)](#71-avvio-di-postgresql-in-docker-solo-database)

    * [7.1.1 Comando Docker Compose per il solo servizio Postgres](#711-comando-docker-compose-per-il-solo-servizio-postgres)
    * [7.1.2 Verifica dell’inizializzazione del database](#712-verifica-dellinizializzazione-del-database)
  * [7.2 Configurazione dei microservizi locali verso il database in Docker](#72-configurazione-dei-microservizi-locali-verso-il-database-in-docker)

    * [7.2.1 Proprietà Spring Boot per la connessione al DB Dockerizzato](#721-proprieta-spring-boot-per-la-connessione-al-db-dockerizzato)
    * [7.2.2 Verifica della connettività (ping DB, log di avvio)](#722-verifica-della-connettivita-ping-db-log-di-avvio)
  * [7.3 Avvio dei microservizi in locale](#73-avvio-dei-microservizi-in-locale)

    * [7.3.1 Avvio via Maven (`mvn spring-boot:run`)](#731-avvio-via-maven-mvn-spring-bootrun)
    * [7.3.2 Avvio via IDE (configurazioni di run)](#732-avvio-via-ide-configurazioni-di-run)
  * [7.4 Verifica del sistema in modalità ibrida](#74-verifica-del-sistema-in-modalita-ibrida)

    * [7.4.1 Controllo delle porte locali](#741-controllo-delle-porte-locali)
    * [7.4.2 Smoke test rapido tramite browser o Postman](#742-smoke-test-rapido-tramite-browser-o-postman)
* [8. Accessing the Services](#8-accessing-the-services)

  * [8.1 User Manager Service](#81-user-manager-service)

    * [8.1.1 Endpoint base (host, port)](#811-endpoint-base-host-port)
    * [8.1.2 Principali API REST esposte (registrazione, lettura, cancellazione utente)](#812-principali-api-rest-esposte-registrazione-lettura-cancellazione-utente)
    * [8.1.3 Codici di risposta attesi per le operazioni chiave](#813-codici-di-risposta-attesi-per-le-operazioni-chiave)
  * [8.2 Data Collector Service](#82-data-collector-service)

    * [8.2.1 Endpoint base (host, port)](#821-endpoint-base-host-port)
    * [8.2.2 API REST per la gestione degli aeroporti e degli interessi](#822-api-rest-per-la-gestione-degli-aeroporti-e-degli-interessi)
    * [8.2.3 API REST per interrogare i voli](#823-api-rest-per-interrogare-i-voli)
  * [8.3 gRPC Interface](#83-grpc-interface)

    * [8.3.1 Panoramica del servizio gRPC esposto dallo User Manager](#831-panoramica-del-servizio-grpc-esposto-dallo-user-manager)
    * [8.3.2 Utilizzo interno da parte del Data Collector (non richiesto lato utente finale)](#832-utilizzo-interno-da-parte-del-data-collector-non-richiesto-lato-utente-finale)
* [9. Using Postman Collections](#9-using-postman-collections)

  * [9.1 Localizzazione delle collection (`postman/`)](#91-localizzazione-delle-collection-postman)
  * [9.2 Import delle collection in Postman](#92-import-delle-collection-in-postman)

    * [9.2.1 User Manager API collection](#921-user-manager-api-collection)
    * [9.2.2 Data Collector API collection](#922-data-collector-api-collection)
  * [9.3 Configurazione delle variabili di ambiente in Postman (host, port, base URL)](#93-configurazione-delle-variabili-di-ambiente-in-postman-host-port-base-url)
  * [9.4 Esecuzione di scenari end-to-end tramite Postman](#94-esecuzione-di-scenari-end-to-end-tramite-postman)

    * [9.4.1 Registrazione di un nuovo utente](#941-registrazione-di-un-nuovo-utente)
    * [9.4.2 Registrazione interessi utente–aeroporto](#942-registrazione-interessi-utenteaeroporto)
    * [9.4.3 Interrogazione dello stato dei voli](#943-interrogazione-dello-stato-dei-voli)
* [10. Health Checks, Logs and Basic Diagnostics](#10-health-checks-logs-and-basic-diagnostics)

  * [10.1 Verifica della raggiungibilità dei servizi](#101-verifica-della-raggiungibilita-dei-servizi)

    * [10.1.1 Endpoint di health (se presenti) o semplice ping](#1011-endpoint-di-health-se-presenti-o-semplice-ping)
  * [10.2 Log dei microservizi](#102-log-dei-microservizi)

    * [10.2.1 Accesso ai log via Docker (`docker compose logs`)](#1021-accesso-ai-log-via-docker-docker-compose-logs)
    * [10.2.2 Principali messaggi informativi/di errore da tenere d’occhio](#1022-principali-messaggi-informativodi-errore-da-tenere-docchio)
  * [10.3 Diagnostica del database](#103-diagnostica-del-database)

    * [10.3.1 Accesso a PostgreSQL (via CLI o client esterno)](#1031-accesso-a-postgresql-via-cli-o-client-esterno)
    * [10.3.2 Verifica della creazione automatica di schemi e tabelle (Flyway)](#1032-verifica-della-creazione-automatica-di-schemi-e-tabelle-flyway)
* [11. Troubleshooting](#11-troubleshooting)

  * [11.1 Problemi comuni in fase di build](#111-problemi-comuni-in-fase-di-build)

    * [11.1.1 Mancanza di JDK/Maven (in build locale)](#1111-mancanza-di-jdkmaven-in-build-locale)
    * [11.1.2 Errori di build delle immagini Docker](#1112-errori-di-build-delle-immagini-docker)
  * [11.2 Problemi comuni in fase di run](#112-problemi-comuni-in-fase-di-run)

    * [11.2.1 Il database non si avvia correttamente](#1121-il-database-non-si-avvia-correttamente)
    * [11.2.2 I servizi non riescono a connettersi a PostgreSQL](#1122-i-servizi-non-riescono-a-connettersi-a-postgresql)
    * [11.2.3 Errori di autenticazione verso OpenSky](#1123-errori-di-autenticazione-verso-opensky)
  * [11.3 Verifiche passo-passo per isolare gli errori](#113-verifiche-passo-passo-per-isolare-gli-errori)

    * [11.3.1 Verifica variabili d’ambiente](#1131-verifica-variabili-dambiente)
    * [11.3.2 Verifica delle porte occupate](#1132-verifica-delle-porte-occupate)
    * [11.3.3 Controllo dei log dei singoli container](#1133-controllo-dei-log-dei-singoli-container)
* [12. Validation Scenarios](#12-validation-scenarios)

  * [12.1 Scenario minimo di smoke test](#121-scenario-minimo-di-smoke-test)

    * [12.1.1 Avvio del sistema](#1211-avvio-del-sistema)
    * [12.1.2 Creazione di un utente di test](#1212-creazione-di-un-utente-di-test)
    * [12.1.3 Registrazione di un interesse per un aeroporto](#1213-registrazione-di-un-interesse-per-un-aeroporto)
    * [12.1.4 Verifica del popolamento dei dati di volo](#1214-verifica-del-popolamento-dei-dati-di-volo)
  * [12.2 Scenario di test della politica at-most-once](#122-scenario-di-test-della-politica-at-most-once)

    * [12.2.1 Ripetizione di una registrazione utente](#1221-ripetizione-di-una-registrazione-utente)
    * [12.2.2 Comportamento atteso (assenza di duplicati, codici HTTP attesi)](#1222-comportamento-atteso-assenza-di-duplicati-codici-http-attesi)
  * [12.3 Scenario di interrogazione dei voli su intervalli temporali](#123-scenario-di-interrogazione-dei-voli-su-intervalli-temporali)

## 1. Project Overview

### 1.1 Descrizione sintetica del sistema di Flight Monitoring

Il sistema è una piattaforma di *flight monitoring* orientata ai microservizi che consente di gestire utenti, aeroporti di interesse e dati di volo relativi ad arrivi e partenze. L’obiettivo principale è fornire a un client esterno un insieme di API **coerenti, stabili e facilmente integrabili** per:

* registrare e gestire utenti identificati univocamente tramite **indirizzo e‑mail**;
* associare a ciascun utente uno o più aeroporti di interesse;
* raccogliere periodicamente da una sorgente esterna autorevole (OpenSky Network) i dati di volo in arrivo e in partenza per tali aeroporti;
* memorizzare i dati di volo in un **database relazionale** per consentire interrogazioni efficienti;
* esporre API dedicate a interrogazioni di tipo operativo e analitico, ad esempio:

  * ultimo volo in arrivo o in partenza per un aeroporto;
  * interrogazioni su intervalli temporali arbitrari.

L’architettura è pensata per essere **modulare** e **estendibile**: ciascun microservizio è *owner* del proprio sottodominio e del relativo schema dati, e le interazioni tra componenti avvengono esclusivamente tramite interfacce pubbliche ben definite (REST e gRPC). Questo approccio riduce l’accoppiamento, facilita l’evoluzione indipendente dei servizi e prepara il sistema a possibili estensioni future, come l’aggiunta di nuove fonti dati o nuove tipologie di interrogazioni.

---

### 1.2 Microservizi coinvolti

L’applicazione è suddivisa in due microservizi Spring Boot **autonomi ma cooperanti**, ciascuno responsabile di un sottoinsieme chiaro del dominio applicativo:

* lo *User Manager Service* governa il ciclo di vita degli utenti e la loro validazione;
* il *Data Collector Service* si occupa della gestione degli aeroporti, degli interessi utente–aeroporto, della raccolta periodica dei dati di volo e della loro interrogazione.

Ogni microservizio:

* utilizza un proprio schema logico all’interno di un’istanza PostgreSQL condivisa;
* espone API REST *JSON over HTTP* per l’interazione con client esterni;
* impiega **Spring Data JPA** e **Flyway** per la gestione del livello di persistenza e delle migrazioni;
* incapsula la propria logica applicativa in servizi dedicati, separando il livello di esposizione delle API dal livello di dominio.

La cooperazione tra i due servizi avviene tramite una chiamata **gRPC** dallo *Data Collector Service* allo *User Manager Service* per la validazione dell’utente, in modo da evitare dipendenze dirette sul database di utenti e mantenere *bounded contexts* nettamente separati.

#### 1.2.1 User Manager Service

Lo **User Manager Service** gestisce il **sottodominio utente** e fornisce funzionalità di registrazione, consultazione e cancellazione. Le responsabilità principali sono:

* registrare un nuovo utente a partire da una richiesta contenente almeno e‑mail e nome;
* garantire l’unicità dell’utente tramite vincoli a livello di dominio e di database (chiave primaria sull’indirizzo e‑mail);
* rendere disponibili endpoint per la consultazione dei dati di un utente e, se necessario, per la sua eliminazione;
* esporre un servizio gRPC dedicato alla **validazione dell’esistenza di un utente**, utilizzato internamente dal *Data Collector Service*.

Il modello dati ruota attorno all’entità `User`, persistita nella tabella `users` dello schema dedicato (*User DB*). L’identificatore primario è il campo `email`, affiancato da attributi descrittivi (ad esempio il nome) e da metadati come la data di creazione (`created_at`).

Per garantire una semantica di tipo **at‑most‑once** nella registrazione, la logica applicativa:

* verifica l’eventuale esistenza preventiva dell’utente con la stessa e‑mail;
* solleva eccezioni di dominio specifiche in caso di duplicato;
* si appoggia sui vincoli di unicità del database come ulteriore linea di difesa.

L’esposizione tramite REST adotta convenzioni HTTP standard (codici di stato espressivi, payload JSON strutturati tramite DTO) e un *exception handler* centralizzato, in modo da restituire risposte coerenti e facilmente consumabili da client esterni.

#### 1.2.2 Data Collector Service

Il **Data Collector Service** è responsabile del **sottodominio aeroporti–interessi–voli** e rappresenta il cuore della logica di raccolta e interrogazione dei dati di volo. Le sue responsabilità principali includono:

* gestire il catalogo degli **aeroporti** monitorati dal sistema, modellati tramite l’entità `Airport` (tabella `airports`);
* gestire le relazioni di interesse tra utenti e aeroporti attraverso l’entità `UserAirportInterest` (tabella `user_airport_interest`), che collega l’e‑mail dell’utente a uno specifico aeroporto e registra la data di creazione dell’interesse;
* raccogliere periodicamente, tramite uno **scheduler interno**, i dati di volo (arrivi e partenze) per tutti gli aeroporti attualmente associati ad almeno un interesse utente;
* mappare e salvare i dati di volo raccolti nell’entità `FlightRecord` (tabella `flight_records`), che rappresenta l’unità informativa di base per le interrogazioni successive;
* mettere a disposizione API REST per interrogare i voli, con particolare attenzione a:

  * ultimo volo in arrivo o partenza per un aeroporto;
  * ricerca di voli all’interno di intervalli temporali specificati dal client.

Prima di registrare un nuovo interesse utente–aeroporto, il servizio invoca il gRPC esposto dallo *User Manager Service* per verificare che l’utente esista effettivamente, evitando così di creare riferimenti inconsistenti. L’associazione tra utente e aeroporto è resa univoca da un vincolo sul pair `(user_email, airport_id)`, che impedisce la duplicazione degli interessi.

Lo scheduler, configurato per eseguire la raccolta a intervalli regolari, calcola una finestra temporale di riferimento (ad esempio le ultime 24 ore) e, per ciascun aeroporto di interesse, interroga l’API OpenSky per arrivi e partenze. I dati grezzi vengono trasformati in `FlightRecord` e salvati nel *Data DB*, in modo che le interrogazioni successive possano lavorare su dati consolidati e omogenei, indipendenti dalla disponibilità istantanea del servizio esterno.

---

### 1.3 Componenti esterni

Il sistema si appoggia su due componenti esterni fondamentali: la **piattaforma OpenSky Network**, che costituisce la sorgente autorevole dei dati di volo, e un’istanza **PostgreSQL** utilizzata come livello di persistenza condiviso. Entrambi i componenti sono integrati in modo da preservare la separazione dei domini applicativi e consentire al contempo una gestione uniforme della configurazione.

#### 1.3.1 OpenSky Network API

Le **OpenSky Network API** rappresentano la fonte dati esterna da cui il sistema estrae le informazioni sui voli in arrivo e in partenza. L’integrazione è realizzata tramite un client dedicato (`OpenSkyClient`) che incapsula:

* l’autenticazione **OAuth2 Client Credentials**, effettuata verso un endpoint di *authorization* configurabile;
* la gestione dell’**access token** (ottenimento, caching in memoria, gestione della scadenza tramite `expires_in` e instant di validità);
* le invocazioni HTTP verso gli endpoint REST di OpenSky per estrarre i voli relativi a un particolare aeroporto e intervallo temporale.

Il client costruisce richieste HTTP con intestazioni adeguate (in particolare l’header `Authorization: Bearer <token>`), interpreta le risposte JSON mappandole in DTO (`OpenSkyFlightDto`) e converte tali DTO in entità di dominio `FlightRecord`. I parametri chiave, come gli URL base per l’autenticazione e per le API, sono esternalizzati tramite proprietà applicative e variabili d’ambiente, così da poter essere modificati senza necessità di ricompilare né ridistribuire il codice.

#### 1.3.2 Database PostgreSQL

Il sistema utilizza una singola istanza **PostgreSQL** come motore di persistenza, all’interno della quale vengono creati due **database logici** separati:

* `userdb`, dedicato al dominio utenti;
* `datadb`, dedicato al dominio aeroporti–interessi–voli.

La creazione dei database avviene tramite uno script di inizializzazione montato nel container (`./docker/db/init/01-create-databases.sql`), mentre la struttura delle tabelle è gestita a livello di ciascun microservizio tramite **Flyway**. In questo modo ogni servizio mantiene pieno controllo sul proprio schema, pur condividendo la stessa istanza PostgreSQL, e l’evoluzione del modello dati risulta tracciata e riproducibile.

I parametri di connessione fondamentali (host, porta, nome dei database, credenziali) sono definiti in un file di configurazione condiviso (`docker/env/postgres.env` per l’istanza e `docker/env/services.env` per i servizi), e iniettati nei microservizi tramite variabili d’ambiente. Questa scelta consente di:

* separare in modo netto la configurazione dalla logica applicativa;
* facilitare la portabilità del sistema tra ambienti diversi (*development*, *test*, *staging*, *production*);
* uniformare la gestione delle credenziali di accesso al database.

---

### 1.4 Riferimenti alla documentazione di progetto

La repository include una documentazione di supporto che integra le informazioni operative di questo README con una descrizione più ampia dell’architettura, del modello dati e dei principali flussi applicativi. Tali materiali sono organizzati in modo da risultare facilmente accessibili a chiunque debba comprendere o manutenere il sistema.

#### 1.4.1 Relazione tecnica (`documentation/written_report.pdf`)

La **relazione tecnica** descritta nel file `documentation/written_report.pdf` fornisce una visione di insieme dettagliata del sistema dal punto di vista architetturale e concettuale. In particolare, il documento illustra:

* i requisiti funzionali e non funzionali che hanno guidato le scelte progettuali;
* la scomposizione del dominio nei due microservizi principali e nei corrispondenti *bounded contexts*;
* il modello concettuale dei dati e le motivazioni alla base della suddivisione in due database logici;
* i principali flussi applicativi, con l’indicazione dei punti in cui intervengono i servizi esterni e i meccanismi di *error handling* e *fault tolerance*.

Questa relazione rappresenta il riferimento principale per comprendere le scelte di design sottostanti al codice e alle istruzioni di build e deploy fornite in questo README.

#### 1.4.2 Diagrammi architetturali e di sequenza

All’interno della cartella `documentation/diagram_screenshots/` sono presenti diversi **diagrammi architetturali e di sequenza** che supportano la comprensione dei flussi chiave del sistema. Fra i più rilevanti:

* un diagramma architetturale complessivo, che mostra i microservizi, il database PostgreSQL, le OpenSky Network API e le principali relazioni fra questi elementi;
* diagrammi di sequenza che descrivono, passo dopo passo, flussi centrali quali:

  * la registrazione di un nuovo utente con garanzia di semantica *at‑most‑once*;
  * la registrazione di un interesse utente–aeroporto con validazione via gRPC;
  * la raccolta periodica dei voli tramite lo scheduler del *Data Collector Service* e le API OpenSky;
  * le interrogazioni dei dati di volo da parte di un client esterno.

Questi diagrammi costituiscono un complemento visivo al codice e alle API, facilitando l’analisi delle dipendenze e dei passi eseguiti in ciascun caso d’uso.

#### 1.4.3 Diagramma Entity–Relationship (ER)

Nella stessa cartella è presente un **diagramma Entity–Relationship (ER)** che rappresenta lo schema logico dei due database `userdb` e `datadb`. Il diagramma evidenzia:

* l’entità `User` e i relativi attributi nel *User DB*;
* le entità `Airport`, `UserAirportInterest` e `FlightRecord` nel *Data DB*;
* le chiavi primarie, i vincoli di unicità (in particolare sul pair `(user_email, airport_id)`), le chiavi esterne e le principali cardinalità tra le entità.

Questo schema costituisce la base per la progettazione del livello di persistenza implementato tramite JPA e Flyway e permette di verificare rapidamente la coerenza tra modello concettuale, modello logico e implementazione effettiva nel codice.

## 2. Repository Structure

### 2.1 Root layout della repository

Nella directory radice sono presenti le cartelle e i file necessari per comprendere rapidamente i componenti principali del sistema e per avviare la piattaforma tramite Docker o in modalità ibrida. A livello logico, la root contiene:

* le directory dei due microservizi (`user-manager-service/` e `data-collector-service/`), ciascuna con il proprio codice applicativo e la propria configurazione;
* la directory `docker/`, che raccoglie i file di orchestrazione e configurazione dell’infrastruttura (PostgreSQL e servizi);
* la directory `documentation/`, che contiene la relazione tecnica e i diagrammi di supporto;
* la directory `postman/`, che include le collection pronte all’uso per verificare rapidamente le API esposte dai servizi;
* i file di supporto generali, come ad esempio il `README.md` (questo documento) e gli eventuali file di configurazione per il versionamento.

Questa impostazione consente a chiunque acceda per la prima volta al repository di individuare immediatamente dove risiedono il codice dei servizi, l’infrastruttura containerizzata e la documentazione tecnica.

---

### 2.2 Struttura delle cartelle principali

La struttura delle cartelle segue un modello *service-oriented* e separa in modo netto il codice applicativo, la configurazione infrastrutturale e gli artefatti di documentazione.

* **`user-manager-service/`**
  Contiene l’implementazione completa dello *User Manager Service* basato su Spring Boot. Al suo interno sono presenti:

  * il codice sorgente Java/Kotlin organizzato secondo i package applicativi (controller REST, servizi, repository JPA, client gRPC);
  * le risorse di configurazione applicativa (ad esempio `application.yml` e gli eventuali file `application-*.yml` per i profili);
  * i file di migrazione **Flyway** per lo schema dati relativo al dominio utente;
  * il file `pom.xml` con le dipendenze e le configurazioni di build Maven;
  * il `Dockerfile` utilizzato per costruire l’immagine container del microservizio.

* **`data-collector-service/`**
  Contiene l’implementazione del *Data Collector Service*. La struttura interna rispecchia quella dello *User Manager Service* e comprende:

  * il codice sorgente organizzato in layer (controller REST, servizi di dominio, client verso OpenSky, repository JPA, componenti scheduler);
  * i file di configurazione Spring Boot per la connessione al *Data DB* e ai servizi esterni (OpenSky, gRPC verso *User Manager*);
  * le migrazioni **Flyway** dedicate alla creazione e all’evoluzione delle tabelle `airports`, `user_airport_interest` e `flight_records`;
  * il `pom.xml` per la gestione delle dipendenze e del processo di build;
  * il `Dockerfile` per creare l’immagine container del servizio.

* **`docker/`**
  Raccoglie tutti gli artefatti relativi all’infrastruttura containerizzata:

  * il file `docker-compose.yml`, che definisce i servizi Docker (PostgreSQL, *User Manager Service*, *Data Collector Service*), le reti, i volumi e le dipendenze;
  * la sottocartella `env/`, che contiene i file di variabili d’ambiente condivisi (`postgres.env` per il database, `services.env` per i microservizi), utilizzati per parametrizzare le immagini senza esporre valori sensibili nel codice sorgente;
  * la sottocartella `db/init/`, che ospita gli script SQL di inizializzazione del database (ad esempio la creazione dei database logici `userdb` e `datadb`).

* **`documentation/`**
  Contiene la documentazione di supporto alla comprensione dell’architettura e del modello dati:

  * il file `written_report.pdf`, che rappresenta la relazione tecnica di progetto;
  * la sottocartella `diagram_screenshots/`, che include gli screenshot dei diagrammi architetturali, dei diagrammi di sequenza e del diagramma ER.

* **`postman/`**
  Contiene le **Postman collections** utilizzate per testare e dimostrare le funzionalità esposte dai microservizi. Tipicamente sono presenti due file JSON principali, ad esempio:

  * una collection per le API dello *User Manager Service* (registrazione, consultazione e cancellazione utente);
  * una collection per le API del *Data Collector Service* (gestione aeroporti, interessi utente–aeroporto, interrogazioni dei voli).

Questa articolazione permette di isolare le responsabilità e di identificare rapidamente dove intervenire in caso di modifiche al codice applicativo, alla configurazione dell’infrastruttura o alla documentazione.

---

### 2.3 File chiave per build & deploy

Alcuni file del repository rivestono un ruolo centrale nelle procedure di build e deploy e meritano una menzione esplicita.

* **`docker/docker-compose.yml`**
  Definisce l’orchestrazione completa dell’ambiente di esecuzione containerizzato. In questo file sono specificati:

  * i servizi Docker (`postgres`, `user-manager-service`, `data-collector-service`);
  * le immagini da utilizzare o generare, i *build context* e i `Dockerfile` associati;
  * i volumi per la persistenza dei dati PostgreSQL e per eventuali mount di configurazione;
  * le reti interne utilizzate per la comunicazione tra i container;
  * le dipendenze di avvio tra i servizi, in modo che il database sia disponibile prima dei microservizi.

* **`docker/env/postgres.env`**
  Contiene le variabili d’ambiente utilizzate per configurare l’istanza PostgreSQL (nome utente, password, porta, nome dei database). Questo file consente di centralizzare la configurazione del database ed evitare la duplicazione di parametri nei vari servizi.

* **`docker/env/services.env`**
  Raccoglie le variabili d’ambiente comuni ai microservizi, tra cui i parametri di connessione al database, gli URL di OpenSky e le impostazioni di base per l’esposizione delle API. I microservizi leggono tali valori all’avvio, rendendo agevole l’adattamento del sistema a diversi ambienti.

* **`user-manager-service/Dockerfile`** e **`data-collector-service/Dockerfile`**
  Descrivono il processo di build delle immagini Docker per i due microservizi. Entrambi utilizzano una strategia *multi-stage* che prevede una prima fase di build con Maven e una seconda fase di runtime più leggera basata su una immagine JDK/JRE minimal.

* **`user-manager-service/pom.xml`** e **`data-collector-service/pom.xml`**
  Definiscono le dipendenze, i plugin e le configurazioni necessarie per compilare, testare e *packagizzare* i microservizi. Questi file sono fondamentali per eseguire build locali senza Docker e per integrare il progetto in pipeline CI/CD.

* **Script SQL di inizializzazione in `docker/db/init/`**
  Comprendono gli script per la creazione dei database logici e di eventuali oggetti di supporto. Sono eseguiti automaticamente all’avvio del container PostgreSQL e garantiscono che l’ambiente sia correttamente predisposto prima dell’esecuzione delle migrazioni applicative.

Nel complesso, questi file costituiscono il nucleo operativo necessario per configurare, buildare e distribuire il sistema in modo riproducibile e controllato in diversi contesti di esecuzione.

## 3. Prerequisites

### 3.1 Requisiti hardware e sistema operativo

Il sistema è progettato per essere eseguito su una macchina in grado di eseguire **Docker** e, opzionalmente, una toolchain Java locale per la build e il run dei microservizi al di fuori dei container. Sono raccomandate le seguenti caratteristiche minime:

* CPU: almeno **2 core** fisici (4 thread consigliati) per evitare contenention eccessiva fra i container;
* RAM: almeno **8 GB** di memoria, con **16 GB** consigliati per lavorare in modo agevole con Docker, IDE e altri strumenti aperti in parallelo;
* Storage: almeno **5–10 GB** di spazio libero dedicato ai container Docker, alle immagini e ai log applicativi.

Per quanto riguarda il sistema operativo, il progetto è stato pensato per ambienti moderni e supportati:

* **Linux** (distribuzioni recenti come Ubuntu, Debian, Fedora, ecc.);
* **macOS** (versioni con supporto ufficiale per Docker Desktop);
* **Windows 10/11** a 64 bit, preferibilmente con **Docker Desktop** installato.

È importante che il sistema operativo consenta l’esecuzione di Docker in modalità **Linux container** e che l’utente disponga dei permessi necessari per avviare e gestire i container.

---

### 3.2 Software necessario

Il funzionamento completo della piattaforma richiede un insieme di strumenti software. Alcuni sono **obbligatori** per l’esecuzione standard tramite Docker, altri sono **opzionali ma raccomandati** per la build e il run locale dei servizi.

#### 3.2.1 Docker

**Docker** è il requisito principale per l’esecuzione containerizzata dell’intero sistema (PostgreSQL + microservizi). Si raccomanda l’installazione di una versione recente, ad esempio:

* Docker Engine / Docker Desktop **20.x** o superiore.

La presenza di Docker consente di:

* avviare l’istanza PostgreSQL con la configurazione prevista;
* eseguire i microservizi all’interno di container isolati;
* riprodurre con facilità l’ambiente di esecuzione su macchine differenti.

#### 3.2.2 Docker Compose

Il progetto utilizza **Docker Compose** per orchestrare l’avvio congiunto dei servizi. In base alla versione di Docker installata, Docker Compose può essere integrato come **sottocomando** (`docker compose`) o come binario separato (`docker-compose`).

È consigliato l’utilizzo della sintassi moderna:

```bash
docker compose version
```

Una versione recente di Docker Desktop include già Docker Compose v2.x, sufficiente per eseguire il file `docker/docker-compose.yml` fornito nella repository.

#### 3.2.3 JDK (per build/esecuzione locale senza Docker)

Per chi desidera eseguire i microservizi localmente (senza containerizzarli), è necessario disporre di un **Java Development Kit (JDK)** compatibile con la versione di Spring Boot utilizzata. Si raccomanda:

* **JDK 21** (LTS) oppure una versione **JDK 17+** compatibile.

È importante che il comando `java` punti al JDK e non a un JRE obsoleto, in modo da garantire il corretto funzionamento dei plugin Maven e delle applicazioni Spring Boot.

#### 3.2.4 Maven (per build/esecuzione locale senza Docker)

La build dei microservizi in modalità non containerizzata richiede **Apache Maven**. È sufficiente una versione recente, ad esempio:

* **Maven 3.8.x** o superiore.

Maven viene utilizzato per:

* compilare il codice sorgente dei microservizi;
* risolvere e scaricare le dipendenze dal repository Maven centrale;
* eseguire i test (se configurati);
* creare i *fat jar* eseguibili o avviare direttamente le applicazioni Spring Boot tramite il plugin dedicato.

---

### 3.3 Account e credenziali esterne

Il sistema integra i dati di volo tramite le **OpenSky Network API**, che richiedono un’apposita registrazione e l’ottenimento di credenziali.

#### 3.3.1 Registrazione a OpenSky Network

Per utilizzare le API, è necessario disporre di un **account OpenSky** valido. Il processo tipico prevede:

* la creazione di un account sul portale OpenSky;
* l’eventuale abilitazione alle API e la consultazione della documentazione ufficiale per i dettagli sui limiti e sulle policy di utilizzo;
* la verifica delle condizioni d’uso per garantire la conformità alle norme previste.

Le credenziali associate all’account saranno utilizzate per ottenere un **access token** OAuth2 mediante il flusso *Client Credentials*.

#### 3.3.2 Ottenimento delle credenziali OAuth2 (client id/secret)

L’integrazione con OpenSky è basata su **OAuth2 Client Credentials**, che richiede la definizione di una *client application* sul lato OpenSky con relativa coppia **client id / client secret**. Una volta ottenute tali credenziali, devono essere configurate come variabili d’ambiente o inserite nei file `env` previsti (ad esempio `docker/env/services.env`), in modo che il `Data Collector Service` possa:

* richiedere un *access token* al servidor di authorization configurato;
* riutilizzare il token fino alla scadenza (`expires_in`), con successivo rinnovo automatico.

Per motivi di sicurezza, è fondamentale **non committare** client id e client secret all’interno del repository. Nel progetto sono previsti placeholder che il deployer deve sostituire con valori reali in fase di configurazione.

---

### 3.4 Verifica installazione dei prerequisiti

Prima di procedere con la build e il deploy, è opportuno verificare che tutti i prerequisiti software siano installati e correttamente configurati.

Per controllare la versione di Docker:

```bash
docker --version
```

Per verificare la presenza di Docker Compose (sintassi moderna):

```bash
docker compose version
```

Per verificare l’installazione del JDK:

```bash
java -version
```

L’output dovrebbe indicare una versione **17** o **21** (o superiore compatibile), con riferimento a un JDK.

Per controllare la versione di Maven:

```bash
mvn -v
```

È opportuno infine validare che le variabili d’ambiente relative a OpenSky (client id, client secret, endpoint di authorization e API base URL) siano configurate correttamente nel contesto in cui verrà eseguito `docker compose` o i microservizi in locale. Un semplice metodo consiste nel verificare, dal terminale, che le variabili risultino valorizzate, ad esempio:

```bash
echo "$OPEN_SKY_CLIENT_ID"
echo "$OPEN_SKY_CLIENT_SECRET"
```

In assenza di valori o in presenza di placeholder, è necessario aggiornare i file `env` o le variabili d’ambiente di sistema prima di procedere con i passi di build e deploy.

## 4. Configuration

### 4.1 Strategie di configurazione (env-based configuration)

La configurazione del sistema è basata su un approccio **env-based**, in cui i parametri sensibili o dipendenti dall’ambiente (host, porte, credenziali, URL di servizi esterni) vengono veicolati tramite **variabili d’ambiente** e file `.env`, mentre i file di configurazione Spring Boot (`application-*.yml`) si limitano a referenziarli. Questa strategia consente di:

* separare in modo netto il **codice applicativo** dai **valori di configurazione**;
* utilizzare la stessa immagine container in ambienti diversi (sviluppo, test, produzione) variando soltanto i file `.env` o le variabili di runtime;
* evitare l’hardcoding di credenziali e URL all’interno dei sorgenti.

A livello infrastrutturale, il file `docker/docker-compose.yml` carica i file `.env` presenti in `docker/env/` e li espone come variabili d’ambiente nei container. Sul lato applicativo, i microservizi leggono tali variabili tramite le property Spring (ad esempio `${DB_HOST}`, `${OPEN_SKY_CLIENT_ID}`, ecc.).

---

### 4.2 File `.env` e variabili d’ambiente

La gestione centralizzata delle variabili d’ambiente avviene principalmente tramite la cartella `docker/env/`. I file `.env` definiti in questa cartella vengono montati nei container al momento dell’avvio tramite Docker Compose.

#### 4.2.1 File `.env` a livello di `docker/`

I file `.env` principali sono:

* **`docker/env/postgres.env`**: definisce i parametri di inizializzazione dell’istanza PostgreSQL;
* **`docker/env/services.env`**: fornisce ai microservizi i parametri comuni di connessione al database e i riferimenti alle OpenSky Network API.

Un esempio semplificato di `postgres.env` può essere:

```dotenv
POSTGRES_USER=user
POSTGRES_PASSWORD=password
POSTGRES_DB=postgres
USER_DB_NAME=userdb
DATA_DB_NAME=datadb
```

In questo file vengono configurati:

* l’utente e la password amministrativi di PostgreSQL (`POSTGRES_USER`, `POSTGRES_PASSWORD`);
* il database predefinito (`POSTGRES_DB`) utilizzato come contesto iniziale per l’esecuzione degli script di bootstrap;
* i nomi dei due **database logici** dedicati ai microservizi (`USER_DB_NAME`, `DATA_DB_NAME`), che saranno creati dallo script di inizializzazione.

Un esempio di `services.env` può essere:

```dotenv
DB_HOST=postgres
DB_PORT=5432
DB_USERNAME=user
DB_PASSWORD=password
USER_DB_NAME=userdb
DATA_DB_NAME=datadb

OPEN_SKY_AUTH_BASE_URL=https://auth.opensky-network.org/auth/realms/opensky-network/protocol/openid-connect/token
OPEN_SKY_API_BASE_URL=https://opensky-network.org/api
OPEN_SKY_CLIENT_ID=your_client_id
OPEN_SKY_CLIENT_SECRET=your_client_secret
```

Queste variabili permettono ai microservizi di:

* connettersi al database PostgreSQL tramite host e porta logici (`DB_HOST`, `DB_PORT`), riutilizzando gli stessi nomi per entrambi i servizi;
* accedere ai database corretti (`USER_DB_NAME`, `DATA_DB_NAME`) pur condividendo la stessa istanza PostgreSQL;
* ottenere le credenziali e gli endpoint necessari per interagire con le OpenSky Network API.

Il file `services.env` viene referenziato nel `docker-compose.yml` per i container dei microservizi, in modo che le variabili siano disponibili al runtime delle applicazioni Spring.

#### 4.2.2 File `.env` specifici per i servizi (se presenti)

Oltre ai file `.env` gestiti a livello di Docker, è possibile definire ulteriori file di configurazione esterni o variabili d’ambiente specifiche per ogni microservizio, ad esempio:

* variabili dedicate a parametri di **logging** (livello di log, formati);
* variabili per controllare l’intervallo di esecuzione dello **scheduler** (ad esempio `FLIGHT_COLLECTION_CRON` o `FLIGHT_COLLECTION_INTERVAL_SECONDS`).

Nel caso in cui si decida di utilizzare file `.env` separati per i singoli servizi, è opportuno mantenere una convenzione chiara (ad esempio `user-manager.env`, `data-collector.env`) e documentarne il contenuto in modo analogo a quanto fatto per `postgres.env` e `services.env`, assicurandosi che Docker Compose o l’ambiente di esecuzione li carichino esplicitamente.

---

### 4.3 Configurazione del database PostgreSQL

La configurazione del database è incapsulata nella combinazione di:

* variabili d’ambiente fornite dai file `.env`;
* script SQL di inizializzazione;
* configurazioni Spring Boot nei microservizi.

#### 4.3.1 Parametri di connessione (host, port, user, password)

I parametri di connessione fondamentali sono:

* **host** del database (ad esempio `postgres` nella rete Docker);
* **porta** di ascolto (tipicamente `5432`);
* **nome utente** e **password** per l’autenticazione;
* **nome del database logico** a cui connettersi.

Nei file `application-docker.yml` dei microservizi, la URL JDBC viene costruita a partire dalle variabili d’ambiente, ad esempio:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:postgres}:${DB_PORT:5432}/${USER_DB_NAME:userdb}
    username: ${DB_USERNAME:user}
    password: ${DB_PASSWORD:password}
```

per lo *User Manager Service*, e in modo analogo per il *Data Collector Service*:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://${DB_HOST:postgres}:${DB_PORT:5432}/${DATA_DB_NAME:datadb}
    username: ${DB_USERNAME:user}
    password: ${DB_PASSWORD:password}
```

In questo modo i microservizi non hanno conoscenza diretta di host, porte o credenziali, ma delegano l’intero set di parametri alle variabili d’ambiente fornite dall’infrastruttura.

#### 4.3.2 Database logici: User DB e Data DB

La separazione tra **User DB** e **Data DB** è realizzata attraverso due database logici distinti (`userdb` e `datadb`) all’interno della stessa istanza PostgreSQL. Lo script `docker/db/init/01-create-databases.sql` utilizza i nomi definiti in `postgres.env` per creare i due database al bootstrap del container.

Ogni microservizio si connette esclusivamente al proprio database logico e gestisce lo schema tramite **Flyway**, con migrazioni collocate in:

* `user-manager-service/src/main/resources/db/migration/` per lo schema utenti;
* `data-collector-service/src/main/resources/db/migration/` per lo schema aeroporti–interessi–voli.

Questo approccio garantisce un isolamento netto dei domain model, pur mantenendo una infrastruttura di persistenza condivisa e facilmente gestibile.

### 4.4 Configurazione delle credenziali OpenSky

Le credenziali per l’accesso alle **OpenSky Network API** sono fornite tramite variabili d’ambiente, in modo da evitare la loro inclusione diretta nei sorgenti o nei file di configurazione versionati.

#### 4.4.1 Variabili d’ambiente per client id e secret

Gli elementi minimi necessari per l’autenticazione OAuth2 *Client Credentials* sono:

* `OPEN_SKY_AUTH_BASE_URL`: URL base del server di authorization;
* `OPEN_SKY_API_BASE_URL`: URL base delle API di volo;
* `OPEN_SKY_CLIENT_ID`: identificativo della client application registrata presso OpenSky;
* `OPEN_SKY_CLIENT_SECRET`: secret associato alla client application.

Nel `Data Collector Service`, tali variabili vengono mappate nelle property applicative, ad esempio:

```yaml
opensky:
  auth-url: ${OPENSKY_AUTH_URL}
  api-base-url: ${OPENSKY_API_URL}
  client-id: ${OPENSKY_CLIENT_ID}
  client-secret: ${OPENSKY_CLIENT_SECRET}
```

Il client dedicato (`OpenSkyClient`) utilizza queste proprietà per:

* richiedere un *access token* valido al server di authorization;
* invocare gli endpoint delle API con l’header `Authorization: Bearer <token>`;
* gestire la scadenza del token sulla base del campo `expires_in`.

#### 4.4.2 Gestione sicura delle credenziali (placeholder vs valori reali)

All’interno del repository, i file `.env` di esempio e le proprietà applicative devono contenere **placeholder** (ad esempio `your_client_id`, `your_client_secret`) e non valori reali. Le credenziali effettive vanno:

* inserite localmente nei file `.env` non versionati (ad esempio mantenuti come `docker/env/services.env` esclusi dal VCS, se richiesto);
* oppure configurate tramite variabili d’ambiente del sistema o del servizio di orchestrazione.

È opportuno garantire che:

* i file contenenti valori reali non vengano committati nel repository remoto;
* l’accesso a tali file sia limitato agli operatori che necessitano di eseguire il sistema.

---

### 4.5 Profili e configurazioni Spring Boot

La configurazione dei microservizi è incentrata su un unico file **`application.yml`** per ciascun servizio, integrato con variabili d’ambiente fornite dall’infrastruttura (Docker e file `.env`). In assenza di profili espliciti, Spring Boot utilizza il **profilo di default**, che nel progetto è già predisposto per l’esecuzione in scenari tipici quali:

* microservizi in esecuzione locale con database PostgreSQL dockerizzato su `localhost:5432`;
* microservizi containerizzati che leggono host, porte e credenziali dal layer di configurazione basato su variabili d’ambiente.

In questo modello, il file `application.yml` funge da **singola sorgente di verità** per la configurazione applicativa, mentre gli aspetti ambiente‑specifici (host, credenziali, URL esterni) sono demandati alle variabili d’ambiente illustrate nelle sezioni precedenti.

#### 4.5.1 Uso del profilo di default e overriding tramite variabili d’ambiente

Il profilo di default di Spring Boot è sufficiente per la maggior parte degli scenari supportati dal progetto. In particolare:

* i parametri relativi al **datasource** (URL JDBC, utente, password) sono definiti in `application.yml` in forma generica, assumendo `localhost` e la porta standard `5432` come configurazione di base;
* i parametri relativi ai **servizi esterni** (endpoint OpenSky, credenziali OAuth2, configurazione gRPC) sono referenziati tramite placeholder e risolti al runtime utilizzando le variabili d’ambiente (`OPEN_SKY_AUTH_BASE_URL`, `OPEN_SKY_API_BASE_URL`, `OPEN_SKY_CLIENT_ID`, `OPEN_SKY_CLIENT_SECRET`, ecc.).

Quando l’applicazione viene eseguita:

* **in locale**, i microservizi si connettono al database Dockerizzato esposto su `localhost:5432`, come descritto nel Capitolo 7;
* **in container**, gli stessi placeholder vengono risolti rispetto alle variabili d’ambiente iniettate da Docker Compose (ad esempio `DB_HOST=postgres`, `DB_PORT=5432`), mantenendo invariata la configurazione applicativa.

Questo approccio riduce la necessità di gestire molteplici file `application-*.yml` per ciascun ambiente e privilegia un modello in cui il comportamento dell’applicazione è governato principalmente dal **contesto di esecuzione** (ambiente) anziché da configurazioni duplicate.

#### 4.5.2 Estensioni opzionali con profili dedicati

Qualora in futuro si rendesse necessario introdurre una differenziazione più marcata tra ambienti (ad esempio **sviluppo**, **test**, **produzione**), è possibile estendere il modello attuale definendo profili Spring Boot dedicati, ad esempio:

* `application-dev.yml` per configurazioni specifiche di sviluppo (logging più verboso, feature flag, parametri di scheduler meno aggressivi);
* `application-prod.yml` per parametri più conservativi, time‑out più stringenti, livelli di log più restrittivi.

L’attivazione di tali profili potrà avvenire tramite:

* variabile d’ambiente `SPRING_PROFILES_ACTIVE` (ad esempio `SPRING_PROFILES_ACTIVE=prod`);
* oppure parametro da riga di comando, ad esempio:

  ```bash
  java -jar data-collector-service.jar --spring.profiles.active=prod
  ```

Nel contesto attuale, tuttavia, l’utilizzo del **profilo di default** combinato con la configurazione env‑based descritta nelle sezioni precedenti è sufficiente e rappresenta la modalità consigliata per build & deploy del sistema.

## 5. Build Instructions

### 5.1 Build tramite Docker (modalità raccomandata)

La modalità di build raccomandata prevede l’utilizzo di **Docker** e **Docker Compose** per costruire le immagini dei microservizi e del database ed eseguire l’intero sistema in ambiente containerizzato.

#### 5.1.1 Posizionamento nella cartella corretta

Dopo aver clonato la repository, è necessario posizionarsi nella cartella `docker/`, che contiene il file `docker-compose.yml` e i file `.env` utilizzati per la configurazione:

```bash
cd docker
```

Tutti i comandi di build ed esecuzione tramite Docker Compose indicati in questo documento presuppongono che la directory corrente sia `docker/`.

#### 5.1.2 Comando per buildare le immagini con Docker Compose

Per costruire le immagini Docker dei due microservizi a partire dai rispettivi `Dockerfile` è possibile utilizzare **Docker Compose** in uno dei seguenti modi.

Build esplicita delle immagini, senza avviare i container:

```bash
docker compose build
```

Questo comando:

* analizza il file `docker-compose.yml`;
* esegue la build delle immagini per i servizi che la richiedono (tipicamente `user-manager-service` e `data-collector-service`);
* effettua il pull dell’immagine di PostgreSQL, se non già presente in locale.

In alternativa, è possibile combinare build e avvio dei servizi utilizzando l’opzione `--build`:

```bash
docker compose up --build
```

In questo caso Docker Compose:

* ricostruisce le immagini se il contesto di build è cambiato (modifiche al codice sorgente, al `pom.xml` o al `Dockerfile`);
* avvia i container nella foreground, mostrando i log in tempo reale.

Per eseguire il sistema in **background**, mantenendo comunque la fase di build automatica, è possibile usare:

```bash
docker compose up --build -d
```

Dopo la prima esecuzione, se il codice non è cambiato, è sufficiente utilizzare:

```bash
docker compose up -d
```

per riavviare i servizi senza ricostruire le immagini.

#### 5.1.3 Descrizione dei Dockerfile dei microservizi (multi-stage build)

Ciascun microservizio dispone di un proprio **`Dockerfile`** alla radice della rispettiva cartella (`user-manager-service/Dockerfile` e `data-collector-service/Dockerfile`). Entrambi adottano una strategia di **multi-stage build** con l’obiettivo di:

* separare la fase di **build Maven** dall’immagine runtime;
* produrre immagini finali più **leggere** e **sicure**, contenenti solo il necessario per l’esecuzione.

Lo schema tipico di un Dockerfile multi-stage per un microservizio Spring Boot è il seguente:

1. **Stage di build**:

   * utilizza un’immagine base con Maven e JDK (ad esempio una immagine ufficiale Maven basata su OpenJDK);
   * copia il file `pom.xml` e scarica le dipendenze (per sfruttare la cache Docker);
   * copia il sorgente (`src/`) e lancia il comando `mvn clean package` per produrre l’artefatto eseguibile (jar Spring Boot).

2. **Stage runtime**:

   * utilizza una immagine base JRE o JDK ridotta (ad esempio una variante *slim* o *distroless* compatibile);
   * copia dal primo stage il jar già costruito in una directory di destinazione (ad esempio `/app/app.jar`);
   * definisce il comando di avvio, tipicamente:

     ```Dockerfile
     ENTRYPOINT ["java", "-jar", "/app/app.jar"]
     ```

In `docker-compose.yml`, ogni servizio referenzia il proprio `Dockerfile` indicando il contesto di build e il valore di `Dockerfile`, ad esempio:

```yaml
services:
  user-manager-service:
    build:
      context: ../user-manager-service
      dockerfile: Dockerfile
    # ...

  data-collector-service:
    build:
      context: ../data-collector-service
      dockerfile: Dockerfile
    # ...
```

Questa configurazione permette di mantenere i microservizi indipendenti, garantendo al contempo una pipeline di build coerente e ripetibile.

---

### 5.2 Build locale senza Docker (opzionale)

È possibile costruire i microservizi anche in modalità **non containerizzata**, utilizzando direttamente Maven. Questa modalità è utile durante lo sviluppo, per l’esecuzione da IDE o per scenari di debug approfondito, mantenendo comunque una pipeline di build allineata a quella utilizzata negli image Docker.

#### 5.2.1 Build dello User Manager Service con Maven

Per compilare e pacchettizzare lo **User Manager Service** senza utilizzare Docker è sufficiente eseguire:

```bash
cd user-manager-service
mvn clean package
```

Il comando:

* rimuove eventuali artefatti di build precedenti (`clean`);
* compila il sorgente ed esegue i test configurati (se presenti);
* genera un jar eseguibile in `target/`.

Al termine, nella directory `target/` sarà disponibile un artefatto del tipo:

```text
target/user-manager-service-<version>.jar
```

Il jar utilizza la configurazione definita in `application.yml`, che prevede — nella configurazione predefinita — una connessione al database `userdb` esposto da PostgreSQL su `localhost:5432`. Eventuali parametri (host, porta, credenziali) possono essere sovrascritti all’avvio tramite variabili d’ambiente o property passate da riga di comando, se necessario.

#### 5.2.2 Build del Data Collector Service con Maven

La procedura per il **Data Collector Service** è del tutto analoga:

```bash
cd data-collector-service
mvn clean package
```

Anche in questo caso Maven:

* risolve le dipendenze dichiarate nel `pom.xml`;
* compila il codice (inclusi scheduler, client gRPC e integrazione con OpenSky);
* esegue gli eventuali test;
* produce un jar eseguibile in `target/`, ad esempio:

  ```text
  target/data-collector-service-<version>.jar
  ```

Il jar utilizza `application.yml` come sorgente di configurazione di default, puntando al database `datadb` su `localhost:5432` e al servizio gRPC dello *User Manager Service* sull’host e porta configurati (tipicamente `localhost:9090`). Anche in questo caso, è possibile sovrascrivere tali valori tramite variabili d’ambiente o argomenti a riga di comando.

#### 5.2.3 Differenze rispetto alla modalità Docker-based

La build locale presenta alcune differenze sostanziali rispetto alla build basata su Docker:

* richiede che **JDK** e **Maven** siano installati sulla macchina host;
* produce **jar eseguibili** anziché immagini container;
* demanda all’operatore la responsabilità di:

  * avviare e configurare il database PostgreSQL (ad esempio tramite Docker, come descritto nel Capitolo 7);
  * impostare correttamente le variabili d’ambiente (ad esempio credenziali OpenSky, host DB) qualora si discostino dai valori di default in `application.yml`;
  * evitare conflitti di porte fra i servizi locali e altri processi in esecuzione.

La modalità Docker-based, al contrario, incapsula le dipendenze runtime e le configurazioni infrastrutturali all’interno di un ambiente controllato, semplificando la riproduzione dello stesso scenario di esecuzione su macchine differenti.

## 6. Deploy & Run with Docker Compose

### 6.1 Prima esecuzione del sistema

#### 6.1.1 Preparazione dei file `.env`

Prima di avviare lo stack applicativo è necessario verificare che i file di configurazione **env-based** siano presenti e correttamente valorizzati nella cartella `docker/env/`.

1. Posizionarsi nella cartella `docker/` della repository:

   ```bash
   cd docker
   ```

2. Verificare la presenza dei file richiesti:

   ```bash
   ls env/
   ```

   Devono essere presenti almeno:

   * `postgres.env` per la configurazione dell’istanza PostgreSQL;
   * `services.env` per la configurazione dei microservizi.

3. Aprire `env/postgres.env` e impostare, se necessario, i valori desiderati per:

   * `POSTGRES_USER` e `POSTGRES_PASSWORD`;
   * `POSTGRES_DB` (database di bootstrap);
   * `USER_DB_NAME` e `DATA_DB_NAME` (database logici dei due domini).

4. Aprire `env/services.env` e impostare:

   * i parametri di connessione al database (`DB_HOST`, `DB_PORT`, `DB_USERNAME`, `DB_PASSWORD`, `USER_DB_NAME`, `DATA_DB_NAME`);
   * gli endpoint e le credenziali per l’integrazione con OpenSky (`OPEN_SKY_AUTH_BASE_URL`, `OPEN_SKY_API_BASE_URL`, `OPEN_SKY_CLIENT_ID`, `OPEN_SKY_CLIENT_SECRET`).

È consigliabile utilizzare valori di default significativi per l’ambiente di sviluppo e sostituire solo i placeholder sensibili (ad esempio le credenziali di accesso e le chiavi OpenSky) prima del primo avvio.

#### 6.1.2 Avvio dei servizi (`docker compose up -d` / `docker compose up --build`)

Una volta preparati i file `.env`, è possibile avviare l’intero sistema tramite **Docker Compose**.

Per eseguire una prima build delle immagini e avviare i container in foreground, con log aggregati:

```bash
docker compose up --build
```

Questo comando:

* costruisce o aggiorna le immagini dei microservizi sulla base dei rispettivi `Dockerfile`;
* scarica l’immagine PostgreSQL se non è già presente in locale;
* avvia i servizi definiti nel `docker-compose.yml` (database e microservizi).

Per avviare il sistema in **background**, lasciando i container attivi ma senza mantenere il terminale bloccato, è possibile usare:

```bash
docker compose up --build -d
```

Dopo la prima esecuzione, se non sono intervenute modifiche al codice o ai `Dockerfile`, è sufficiente:

```bash
docker compose up -d
```

per riavviare i servizi utilizzando le immagini già costruite.

Durante il primo avvio è normale che il servizio PostgreSQL impieghi alcuni secondi per completare l’inizializzazione, compresa l’esecuzione degli script SQL in `db/init/` e la preparazione dei database logici. I microservizi si connetteranno al database una volta che questo sarà operativo.

#### 6.1.3 Verifica che i container siano in esecuzione

Dopo l’avvio, è opportuno verificare che tutti i servizi previsti siano effettivamente in esecuzione.

Per elencare i container attivi associati allo stack:

```bash
docker compose ps
```

L’output deve riportare, con stato `running`, almeno:

* il container dell’istanza PostgreSQL (ad esempio `postgres` o nome equivalente configurato nel `docker-compose.yml`);
* il container del *User Manager Service*;
* il container del *Data Collector Service*.

Per esaminare rapidamente i log complessivi:

```bash
docker compose logs
```

o, per seguire i log in tempo reale:

```bash
docker compose logs -f
```

È utile verificare nei log dei microservizi che:

* la connessione al database venga stabilita correttamente;
* le migrazioni Flyway siano applicate senza errori;
* gli endpoint REST risultino esposti sulle porte attese (come configurate nel `docker-compose.yml`).

---

### 6.2 Arresto del sistema

#### 6.2.1 Comando di stop (`docker compose down`)

Per arrestare tutti i servizi e rilasciare le risorse allocate è possibile utilizzare, dalla cartella `docker/`:

```bash
docker compose down
```

Il comando interrompe i container associati allo stack e rimuove le relative definizioni di rete, mantenendo però intatti i volumi di persistenza (ad esempio quelli associati a PostgreSQL), così che i dati restino disponibili ai successivi riavvii.

In alternativa, se si preferisce fermare temporaneamente i container senza rimuoverli, è possibile ricorrere a:

```bash
docker compose stop
```

che sospende l’esecuzione dei container, lasciando invariata la loro configurazione e il loro stato sul disco.

#### 6.2.2 Rimozione volumi/persistenza (se necessario)

In alcuni scenari può essere necessario ripartire da uno stato completamente pulito (ad esempio per ripetere da zero l’inizializzazione dei database o per cancellare dati di test). In questo caso è possibile utilizzare l’opzione `--volumes`:

```bash
docker compose down --volumes
```

Questo comando rimuove, oltre ai container e alle reti, anche i volumi associati allo stack, eliminando di fatto tutti i dati persistiti da PostgreSQL. Al successivo `docker compose up`, il database verrà ricreato da zero eseguendo nuovamente gli script di inizializzazione e le migrazioni applicative.

L’uso di `--volumes` deve essere valutato con attenzione, poiché comporta la perdita definitiva dei dati memorizzati nei volumi coinvolti.

---

### 6.3 Comandi Docker utili

#### 6.3.1 Visualizzazione log di un singolo servizio

Per analizzare nel dettaglio il comportamento di un singolo servizio è possibile utilizzare il comando `logs` specificando il nome del servizio definito in `docker-compose.yml`. Ad esempio:

```bash
docker compose logs user-manager-service
```

```bash
docker compose logs data-collector-service
```

È possibile aggiungere l’opzione `-f` per seguire i log in streaming:

```bash
docker compose logs -f data-collector-service
```

#### 6.3.2 Accesso alla shell di un container

Per accedere alla shell di un container in esecuzione (ad esempio per effettuare verifiche puntuali o consultare file interni), è possibile utilizzare `docker exec`. Dalla cartella `docker/`:

```bash
docker compose exec postgres bash
```

All’interno della shell del container PostgreSQL è ad esempio possibile utilizzare il client `psql` per ispezionare i database, le tabelle o gli schemi creati dalle migrazioni applicative.

In modo analogo, si può accedere alla shell dei container dei microservizi (se l’immagine lo consente) per effettuare verifiche aggiuntive.

#### 6.3.3 Verifica delle porte esposte

Per verificare le porte esposte dai container verso l’host si può utilizzare il comando standard Docker:

```bash
docker ps
```

La colonna `PORTS` mostra le associazioni del tipo `host_port:container_port` per ciascun servizio. Le porte host riportate in questa colonna sono quelle da utilizzare per accedere alle API REST dai client esterni (browser, Postman, sistemi di integrazione).

Qualora si modifichino le mappature delle porte nel file `docker-compose.yml`, è necessario rieseguire il ciclo di:

```bash
docker compose down
```

seguito da

```bash
docker compose up --build -d
```

per applicare le modifiche alla configurazione dell’ambiente di esecuzione.

## 7. Deploy & Run with Local Microservices and Dockerized PostgreSQL

### 7.1 Avvio di PostgreSQL in Docker (solo database)

#### 7.1.1 Comando Docker Compose per il solo servizio Postgres

Per eseguire i microservizi in locale mantenendo **PostgreSQL** in Docker, è necessario avviare esclusivamente il servizio del database definito nel file `docker/docker-compose.yml`.

1. Aprire un terminale e posizionarsi nella cartella `docker/` della repository:

   ```bash
   cd docker
   ```

2. Avviare il solo servizio PostgreSQL in modalità *detached* (in background). Se nel `docker-compose.yml` il servizio è denominato, ad esempio, `postgres`, il comando sarà:

   ```bash
   docker compose up -d postgres
   ```

   In alternativa, se il servizio è esposto con un diverso nome logico, è sufficiente sostituire `postgres` con il nome configurato nel file `docker-compose.yml`.

Il comando avvia il container del database utilizzando i parametri definiti in `env/postgres.env` e monta gli script di inizializzazione in `db/init/`.

#### 7.1.2 Verifica dell’inizializzazione del database

Per verificare che il database sia correttamente inizializzato e in esecuzione:

* controllare lo stato del servizio:

  ```bash
  docker compose ps
  ```

  Il container associato a PostgreSQL deve risultare in stato `running`.

* consultare i log del container per verificare l’esecuzione degli script di bootstrap e l’assenza di errori:

  ```bash
  docker compose logs postgres
  ```

Se la configurazione prevede l’uso di volumi persistenti, al primo avvio verranno creati i database logici (`userdb`, `datadb`) e gli oggetti iniziali; agli avvii successivi PostgreSQL riutilizzerà i dati già presenti nei volumi.

---

### 7.2 Configurazione dei microservizi locali verso il database in Docker

Quando i microservizi vengono eseguiti **in locale** (al di fuori dei container), la connessione al database PostgreSQL avviene tramite la porta pubblicata dal container sull’host. La configurazione di default fornita dal progetto è già orientata a questo scenario e si basa sul file `application.yml` di ciascun servizio.

#### 7.2.1 Proprietà Spring Boot per la connessione al DB Dockerizzato

Per lo *User Manager Service*, `application.yml` definisce una configurazione del datasource analoga alla seguente:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/userdb
    username: user
    password: password
    driver-class-name: org.postgresql.Driver
```

Per il *Data Collector Service* la configurazione è simile, puntando al database logico `datadb`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/datadb
    username: user        
    password: password    
    driver-class-name: org.postgresql.Driver
```

Queste impostazioni presuppongono che:

* il container PostgreSQL esposto da Docker pubblichi la porta **5432** sull’host (`5432:5432` nel `docker-compose.yml`);
* l’utente e la password configurati in `docker/env/postgres.env` coincidano con quelli utilizzati in `application.yml`.

Se si modificano i valori in `postgres.env` (ad esempio nome utente, password o porta esterna), è necessario:

* aggiornare i corrispondenti parametri in `application.yml`; oppure
* sovrascrivere le proprietà Spring Boot tramite variabili d’ambiente (`SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`) nel contesto di esecuzione locale dei microservizi.

In questo modo la logica applicativa rimane invariata, mentre la configurazione può essere adattata con facilità a diversi ambienti mantenendo `application.yml` come base unica e coerente.

#### 7.2.2 Verifica della connettività (ping DB, log di avvio)

Una volta avviato il container PostgreSQL, la connettività può essere verificata indipendentemente dai microservizi:

* utilizzando un client PostgreSQL esterno (`psql` o client grafico) per connettersi a `localhost:5432` con l’utente e la password configurati;
* controllando che i database logici `userdb` e `datadb` siano effettivamente presenti e accessibili.

Dopo l’avvio dei microservizi in locale, i log di Spring Boot devono mostrare:

* la creazione del pool di connessioni verso `jdbc:postgresql://localhost:5432/...`;
* l’esecuzione delle migrazioni Flyway sul database corretto;
* l’assenza di eccezioni di tipo *connection refused*, *authentication failed* o *database does not exist*.

Eventuali errori di connessione indicano tipicamente uno dei seguenti problemi:

* container PostgreSQL non avviato o terminato inaspettatamente;
* porta diversa da `5432` o non esposta correttamente sull’host;
* credenziali in `application.yml` non allineate con quelle effettive del database.

---

### 7.3 Avvio dei microservizi in locale

L’esecuzione locale dei microservizi può avvenire tramite **Maven** o tramite l’IDE utilizzato per lo sviluppo. In entrambi i casi, in assenza di profili espliciti, Spring Boot utilizza il profilo di default e le impostazioni definite in `application.yml`, già predisposte per l’utilizzo del database Dockerizzato su `localhost:5432` e per la comunicazione gRPC tra i servizi.

#### 7.3.1 Avvio via Maven (`mvn spring-boot:run`)

Per avviare lo *User Manager Service* in locale tramite Maven:

```bash
cd user-manager-service
mvn spring-boot:run
```

Il comando:

* compila il codice (se necessario);
* avvia l’applicazione Spring Boot utilizzando `application.yml` come configurazione di riferimento;
* espone le API REST sulla porta configurata (ad esempio `8081`);
* avvia il server gRPC sulla porta configurata (ad esempio `9090`).

Per il *Data Collector Service* la procedura è analoga:

```bash
cd data-collector-service
mvn spring-boot:run
```

In questo caso l’applicazione:

* utilizza `application.yml` per connettersi al database `datadb` su `localhost:5432`;
* configura il client gRPC verso lo *User Manager Service* all’indirizzo e porta definiti in configurazione (tipicamente `localhost:9090`);
* attiva lo scheduler per la raccolta periodica dei dati di volo, se abilitato.

È possibile avviare i due servizi in due terminali distinti, mantenendo in esecuzione il container PostgreSQL lanciato in precedenza.

#### 7.3.2 Avvio via IDE (configurazioni di run)

Per l’avvio da IDE è sufficiente:

* importare ciascun microservizio come progetto Maven;
* creare una configurazione di esecuzione che punti alla classe `main` dell’applicazione Spring Boot (`UserManagerServiceApplication` e `DataCollectorServiceApplication`);
* assicurarsi che **non** siano impostate variabili o parametri che attivino profili non desiderati (ad esempio `SPRING_PROFILES_ACTIVE`), lasciando che l’applicazione utilizzi la configurazione predefinita in `application.yml`.

Se la configurazione è corretta, l’IDE mostrerà nei log di avvio:

* la connessione al database `localhost:5432`;
* l’applicazione delle migrazioni Flyway;
* l’apertura delle porte HTTP/gRPC previste.

A questo punto i microservizi saranno pronti per essere interrogati tramite browser, `curl` o Postman, come descritto nelle sezioni dedicate all’accesso ai servizi e ai flussi di validazione.

---

### 7.4 Verifica del sistema in modalità ibrida

Una volta che PostgreSQL è in esecuzione in Docker e i microservizi sono attivi in locale, il sistema opera in modalità **ibrida**: il database è containerizzato, mentre le applicazioni sono eseguite direttamente sulla macchina host.

#### 7.4.1 Controllo delle porte locali

È opportuno verificare che le porte locali configurate per i microservizi non siano occupate da altri processi. Ad esempio, se lo *User Manager Service* espone le API REST su `localhost:8081` e il *Data Collector Service* su `localhost:8082`, è possibile controllare rapidamente la raggiungibilità tramite un browser o comandi `curl`, ad esempio:

```bash
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
```

(ammesso che gli endpoint di health siano abilitati). In assenza di actuator, è possibile utilizzare qualsiasi endpoint REST pubblico definito dai servizi.

#### 7.4.2 Smoke test rapido tramite browser o Postman

Per validare il corretto funzionamento del sistema in modalità ibrida è possibile utilizzare le collection disponibili nella cartella `postman/`, configurando come **base URL** gli indirizzi locali dei microservizi. Alcuni esempi di smoke test includono:

* chiamare le API dello *User Manager Service* per registrare un nuovo utente e verificarne la persistenza nel `userdb`;
* utilizzare le API del *Data Collector Service* per registrare un aeroporto e associare un interesse utente–aeroporto;
* invocare gli endpoint di interrogazione dei voli (dopo che lo scheduler ha effettuato almeno un ciclo di raccolta) per verificare che i dati vengano correttamente estratti dal `datadb`.

Questi test permettono di accertare che i microservizi locali comunichino correttamente con il database Dockerizzato, che le configurazioni di rete siano adeguate e che la logica applicativa operi come previsto in uno scenario di esecuzione ibrido.

## 8. Accessing the Services

### 8.1 User Manager Service

Lo *User Manager Service* espone un set di API REST orientate alla gestione del ciclo di vita degli utenti, utilizzando l’e‑mail come identificatore univoco. Tutte le operazioni prevedono scambi in formato **JSON** su HTTP e seguono le convenzioni sui codici di stato descritte nelle sezioni precedenti.

#### 8.1.1 Endpoint base (host, port)

In un ambiente standard, il servizio è esposto tramite un endpoint HTTP configurabile. I casi più comuni sono:

* **Esecuzione in locale**:

  * Base URL: `http://localhost:8080/api/users`
* **Esecuzione in Docker**:

  * All’interno della rete Docker: `http://user-manager-service:8080/api/users`
  * Dall’host, tramite port‑mapping: `http://localhost:<HOST_PORT>/api/users` (dove `<HOST_PORT>` è il valore configurato nel `docker-compose.yml`, tipicamente `8080`).

Tutti gli endpoint dello *User Manager Service* derivano da questa base URL, aggiungendo eventuali path e parametri.

#### 8.1.2 Principali API REST esposte (registrazione, lettura, cancellazione utente)

Le operazioni principali sono:

**Registrazione di un nuovo utente (idempotente)**

* **Metodo**: `POST`
* **URL**: `/api/users`
* **Body (JSON)**, esempio:

  ```json
  {
    "email": "alice@example.com",
    "name": "Alice Doe"
  }
  ```

* **Vincoli di validazione**:

  * `email` obbligatoria, in formato e‑mail valido;
  * `name` obbligatorio, con lunghezza entro i limiti previsti.
* **Semantica**:

  * se l’utente non esiste, viene creato un nuovo record e restituito con *timestamp* di creazione;
  * se l’utente esiste già con la stessa e‑mail, l’operazione è **idempotente** e restituisce la rappresentazione dell’utente esistente senza creare duplicati.

**Lettura di un utente per e‑mail**

* **Metodo**: `GET`
* **URL**: `/api/users/{email}`
* **Path variable**:

  * `email`: indirizzo e‑mail dell’utente da recuperare (deve rispettare i vincoli di formato).
* **Semantica**:

  * se l’utente esiste, viene restituita la sua rappresentazione completa (e‑mail, nome, `createdAt`);
  * se l’utente non esiste, viene restituito un errore strutturato che segnala la risorsa mancante.

**Cancellazione di un utente**

* **Metodo**: `DELETE`
* **URL**: `/api/users/{email}`
* **Path variable**:

  * `email`: indirizzo e‑mail dell’utente da cancellare.
* **Semantica**:

  * in caso di successo, il record viene rimosso dal *User DB*;
  * eventuali interessi associati nel *Data DB* rimangono a carico del *Data Collector Service* secondo le politiche applicative adottate.

Tutte le API REST dello *User Manager Service* espongono risposte in formato JSON, inclusi i casi di errore (ad esempio campi mancanti, formato non valido, risorse non trovate), secondo il modello di errore standard del sistema.

#### 8.1.3 Codici di risposta attesi per le operazioni chiave

Per le operazioni esposte dallo *User Manager Service* valgono le seguenti convenzioni:

* **201 Created** – registrazione di un nuovo utente (`POST /api/users`) quando viene effettivamente creato un record:

  ```json
  {
    "email": "alice@example.com",
    "name": "Alice Doe",
    "createdAt": "2025-11-30T15:40:00Z"
  }
  ```

* **200 OK** – lettura riuscita o registrazione idempotente:

  * risposta di `GET /api/users/{email}` con un utente esistente;
  * risposta di `POST /api/users` quando l’utente era già presente e viene restituita la rappresentazione esistente.

* **204 No Content** – cancellazione riuscita tramite `DELETE /api/users/{email}`, senza body in risposta.

* **400 Bad Request** – input non valido:

  * payload JSON non conforme (campi mancanti);
  * e‑mail malformata nel body o nel path;
  * violazioni di vincoli di validazione sui campi.

* **404 Not Found** – risorsa non trovata:

  * utente inesistente per `GET` o `DELETE /api/users/{email}`.

* **409 Conflict** (eventuale) – violazione di vincoli di unicità non assorbita dalla logica di idempotenza.

* **5xx** – errori interni non previsti o malfunzionamenti infrastrutturali; vengono comunque mappati in una risposta JSON strutturata con i campi standard (`timestamp`, `status`, `error`, `errorCode`, `message`, `path`).

---

### 8.2 Data Collector Service

Il *Data Collector Service* espone API REST dedicate alla gestione degli interessi utente–aeroporto e all’interrogazione dei dati di volo raccolti tramite OpenSky. Tutte le operazioni seguono le stesse convenzioni di formato JSON e codici HTTP adottate per lo *User Manager Service*.

#### 8.2.1 Endpoint base (host, port)

In un ambiente standard, il servizio è accessibile tramite:

* **Esecuzione in locale**:

  * Base URL per la gestione degli interessi: `http://localhost:8081/api/interests`
  * Base URL per le interrogazioni sui voli: `http://localhost:8081/api/flights`
* **Esecuzione in Docker**:

  * All’interno della rete Docker:

    * `http://data-collector-service:8081/api/interests`
    * `http://data-collector-service:8081/api/flights`
  * Dall’host, tramite port‑mapping:

    * `http://localhost:<HOST_PORT_INTERESTS>/api/interests`
    * `http://localhost:<HOST_PORT_FLIGHTS>/api/flights`

Tutti gli endpoint descritti nelle sottosezioni successive derivano da queste base URL.

#### 8.2.2 API REST per la gestione degli aeroporti e degli interessi

La relazione tra utenti e aeroporti viene modellata tramite **interessi utente–aeroporto**, gestiti dalle API esposte sul path `/api/interests`. Ogni interesse collega un utente (identificato dall’e‑mail) a un aeroporto (identificato dal codice IATA/ICAO configurato nel catalogo).

**Registrazione di un interesse utente–aeroporto**

* **Metodo**: `POST`
* **URL**: `/api/interests`
* **Body (JSON)**, esempio:

  ```json
  {
    "userEmail": "alice@example.com",
    "airportCode": "LIRF"
  }
  ```

* **Semantica**:

  1. validazione sintattica del payload;
  2. validazione dell’utente via gRPC verso lo *User Manager Service*;
  3. verifica dell’esistenza dell’aeroporto nel catalogo del *Data Collector*;
  4. verifica dell’assenza di un interesse duplicato per la coppia (`userEmail`, `airportCode`);
  5. creazione del nuovo interesse oppure restituzione idempotente di quello esistente.

* **Risposte principali**:

  * `201 Created` con il DTO dell’interesse se viene creato un nuovo record;
  * `200 OK` con il DTO dell’interesse esistente se la coppia era già presente;
  * `400 Bad Request` in caso di payload non valido;
  * `404 Not Found` se l’utente non esiste o se l’aeroporto non è presente nel catalogo;
  * `409 Conflict` in caso di violazione di vincoli di unicità non assorbita dalla logica applicativa.

**Rimozione di un interesse**

* **Metodo**: `DELETE`

* **URL**: `/api/interests`

* **Query parameters**:

  * `userEmail`: e‑mail dell’utente;
  * `airportCode`: codice dell’aeroporto.

* **Esempio di richiesta**:

  ```text
  DELETE /api/interests?userEmail=alice@example.com&airportCode=LIRF
  ```

* **Risposte principali**:

  * `204 No Content` in caso di rimozione riuscita;
  * `404 Not Found` se non esiste alcun interesse per la coppia indicata (a seconda della semantica scelta);
  * `400 Bad Request` se i parametri sono mancanti o non validi.

**Elenco degli interessi per utente**

* **Metodo**: `GET`

* **URL**: `/api/interests`

* **Query parameters**:

  * `userEmail`: e‑mail dell’utente (obbligatorio).

* **Esempio di richiesta**:

  ```text
  GET /api/interests?userEmail=alice@example.com
  ```

* **Risposte principali**:

  * `200 OK` con una lista JSON di interessi presenti per l’utente; la lista può essere vuота se non sono registrati interessi;
  * `400 Bad Request` se l’e‑mail è mancante o non valida;
  * `404 Not Found` opzionale, se si decide di segnalare esplicitamente il caso di utente inesistente.

Il **catalogo degli aeroporti** utilizzato da queste API è gestito a livello di *Data DB* e viene popolato tramite gli script e le migrazioni del microservizio. Le API sugli interessi assumono che i codici aeroporto utilizzati nelle richieste siano coerenti con tale catalogo.

#### 8.2.3 API REST per interrogare i voli

Le interrogazioni sui voli si appoggiano sul path `/api/flights` e sulle relative varianti. Gli endpoint operano sui dati storici memorizzati nella tabella `flight_records`, alimentata dallo scheduler interno che integra i dati forniti da OpenSky.

**Ultimo volo in arrivo o partenza per aeroporto/direzione**

* **Metodo**: `GET`

* **URL**: `/api/flights/last`

* **Query parameters**:

  * `airportCode` – codice dell’aeroporto (obbligatorio);
  * `direction` – direzione del volo, ad esempio `ARRIVAL` o `DEPARTURE` (obbligatorio).

* **Esempio di richiesta**:

  ```text
  GET /api/flights/last?airportCode=LIRF&direction=ARRIVAL
  ```

* **Risposte principali**:

  * `200 OK` con un oggetto JSON che descrive l’ultimo volo registrato per l’aeroporto/direzione indicati (campi tipici: `flightNumber`, `actualTime`, `scheduledTime`, `status`, `delayMinutes`, `collectedAt`);
  * `400 Bad Request` se `direction` non è ammessa o se i parametri sono mancanti/non validi;
  * `404 Not Found` se:

    * non esistono voli registrati per la combinazione indicata;
    * l’aeroporto non è presente nel catalogo.

**Media dei voli sugli ultimi *N* giorni**

* **Metodo**: `GET`

* **URL**: `/api/flights/average`

* **Query parameters**:

  * `airportCode` – codice dell’aeroporto (obbligatorio);
  * `direction` – `ARRIVAL` o `DEPARTURE` (obbligatorio);
  * `days` – numero di giorni da considerare a ritroso rispetto all’istante corrente (obbligatorio, intero > 0).

* **Esempio di richiesta**:

  ```text
  GET /api/flights/average?airportCode=LIRF&direction=DEPARTURE&days=7
  ```

* **Semantica**:

  * il servizio calcola l’intervallo `[now − days, now]`;
  * conta i flight records corrispondenti a aeroporto/direzione nell’intervallo;
  * restituisce il numero totale di voli e la media giornaliera (`totalFlights / days`).

* **Risposte principali**:

  * `200 OK` con un oggetto JSON che include, tra gli altri, i campi `airportCode`, `direction`, `days`, `totalFlights`, `averagePerDay`, `from`, `to`;
  * `400 Bad Request` se `days <= 0`, se i parametri sono mancanti o se gli intervalli temporali risultano mal formati;
  * `404 Not Found` se l’aeroporto non è presente nel catalogo.

**Interrogazioni su intervalli temporali arbitrari**

* **Metodo**: `GET`
* **URL**: `/api/flights`
* **Query parameters**:

  * `airportCode` – codice dell’aeroporto (obbligatorio);
  * `direction` – `ARRIVAL` o `DEPARTURE` (obbligatorio);
  * `from` – istante di inizio intervallo, in formato ISO‑8601 (opzionale);
  * `to` – istante di fine intervallo, in formato ISO‑8601 (opzionale).

In assenza di parametri temporali, il servizio può applicare una finestra temporale di default (ad esempio le ultime *N* ore) per evitare interrogazioni non limitate nel tempo.

* **Esempio di richiesta**:

  ```text
  GET /api/flights?airportCode=LIRF&direction=ARRIVAL&from=2025-11-29T00:00:00Z&to=2025-11-30T00:00:00Z
  ```

* **Risposte principali**:

  * `200 OK` con una lista JSON di voli che soddisfano i criteri indicati; la lista può essere vuota se nell’intervallo specificato non sono presenti voli;
  * `400 Bad Request` se i parametri temporali sono mal formati o se `from > to`, oppure se `direction` ha un valore non ammesso;
  * `404 Not Found` se l’aeroporto non è presente nel catalogo.

Gli endpoint di interrogazione applicano un pattern uniforme di **validazione → risoluzione aeroporto → query sul *Data DB* → mapping del risultato** in DTO di risposta, garantendo coerenza semantica e prevedibilità per il client.

---

### 8.3 gRPC Interface

L’interfaccia gRPC di validazione utente costituisce il canale di comunicazione **service‑to‑service** tra *Data Collector Service* e *User Manager Service*. Questo canale è interno all’architettura e non è esposto direttamente verso client esterni.

#### 8.3.1 Panoramica del servizio gRPC esposto dallo User Manager

Lo *User Manager Service* espone un servizio gRPC denominato, a livello concettuale, `UserValidationService`, definito in un file `.proto` dedicato. La firma principale è:

```protobuf
service UserValidationService {
  rpc CheckUserExists (UserValidationRequest)
      returns (UserValidationResponse);
}

message UserValidationRequest {
  string email = 1;
}

message UserValidationResponse {
  bool exists = 1;
}
```

Elementi chiave dell’interfaccia:

* **Servizio gRPC**: `UserValidationService`, responsabile della validazione dell’esistenza di un utente a partire dall’e‑mail.
* **Request**: `UserValidationRequest`, contenente il campo `email` da verificare.
* **Response**: `UserValidationResponse`, contenente il campo booleano `exists` che indica se l’utente è presente nel dominio dello *User Manager Service*.

Sul lato *User Manager*, l’implementazione server del servizio (ad esempio `UserValidationGrpcService`) riceve la richiesta, interroga il *User DB* e restituisce una risposta coerente con lo stato corrente del sistema.

A livello di trasporto, il servizio gRPC è tipicamente esposto sulla stessa macchina del *User Manager Service*, su una porta dedicata (configurabile tramite proprietà applicative), con serializzazione **Protobuf** e supporto a connessioni sicure qualora richiesto dall’ambiente di esecuzione.

#### 8.3.2 Utilizzo interno da parte del Data Collector (non richiesto lato utente finale)

Il *Data Collector Service* utilizza un client gRPC generato a partire dalla stessa definizione `.proto` (ad esempio `UserValidationGrpcClient`) per validare gli utenti prima di creare nuovi interessi utente–aeroporto.

Il flusso tipico di utilizzo è il seguente:

1. il client REST invoca `POST /api/interests` sul *Data Collector Service* fornendo `userEmail` e `airportCode`;
2. il *Data Collector* costruisce una richiesta gRPC `UserValidationRequest` con il campo `email` valorizzato a `userEmail`;
3. il client gRPC chiama `CheckUserExists` sullo *User Manager Service*;
4. se `UserValidationResponse.exists` è `true`, il *Data Collector* prosegue con la verifica dell’aeroporto e la creazione dell’interesse;
5. se `exists` è `false`, il *Data Collector* traduce la condizione in un errore applicativo, tipicamente `404 Not Found` con codice di errore `USER_NOT_FOUND`.

L’interfaccia gRPC non richiede configurazione da parte dei consumatori finali del sistema, ma è rilevante in ottica di integrazione e manutenzione, poiché consente di mantenere **separati i domini applicativi** (utenti vs. dati di volo) evitando accessi diretti incrociati ai rispettivi database.

## 9. Using Postman Collections

### 9.1 Localizzazione delle collection (`postman/`)

Le **Postman collections** fornite nel repository consentono di esercitare in modo rapido e controllato le API esposte dai due microservizi. Tutti i file necessari sono collocati nella cartella:

```text
postman/
```

All’interno di questa directory sono presenti, in particolare:

* un file JSON dedicato alle API dello *User Manager Service* (ad esempio `user-manager-api.postman_collection.json`);
* un file JSON dedicato alle API del *Data Collector Service* (ad esempio `data-collector-api.postman_collection.json`).

Ogni collection contiene richieste preconfigurate con path, metodi HTTP, header e payload di esempio. Le uniche informazioni che richiedono un adattamento al contesto di esecuzione sono gli **indirizzi host**, le **porte** e, ove necessario, i token o gli identificativi di ambiente.

---

### 9.2 Import delle collection in Postman

L’utilizzo delle collection parte dal loro import in Postman, che può avvenire sia tramite interfaccia grafica sia trascinando i file JSON nell’applicazione.

1. Avviare Postman.
2. Selezionare la voce **Import**.
3. Scegliere l’opzione **File** e selezionare i file JSON nella cartella `postman/`.
4. Confermare l’import: le collection compariranno nella sezione **Collections** con il nome definito nel file.

Una volta importate, le richieste possono essere eseguite singolarmente oppure organizzate in **folder** logici all’interno di ogni collection, riflettendo i diversi gruppi di API (gestione utenti, interessi, interrogazioni voli).

#### 9.2.1 User Manager API collection

La collection dedicata allo *User Manager Service* contiene le richieste fondamentali per gestire il ciclo di vita dell’utente. Tipicamente include:

* **Create / Register User** – richiesta `POST` verso `/api/users` con body JSON di esempio;
* **Get User by Email** – richiesta `GET` verso `/api/users/{email}`;
* **Delete User** – richiesta `DELETE` verso `/api/users/{email}`.

Ogni richiesta è preconfigurata con:

* metodo HTTP corretto;
* path relativo dell’endpoint;
* header standard (ad esempio `Content-Type: application/json` per le richieste con body);
* payload di esempio, modificabile per simulare diversi scenari.

La base URL viene normalmente parametrizzata tramite una variabile di ambiente Postman (ad esempio `{{user_manager_base_url}}`), in modo che il passaggio da esecuzione locale a esecuzione in Docker richieda soltanto la modifica del valore di tale variabile.

#### 9.2.2 Data Collector API collection

La collection dedicata al *Data Collector Service* copre i principali casi d’uso relativi agli interessi utente–aeroporto e alle interrogazioni sui voli. Le richieste tipiche includono:

* **Create Interest** – richiesta `POST` verso `/api/interests` con body contenente `userEmail` e `airportCode`;
* **List Interests for User** – richiesta `GET` verso `/api/interests` con query parameter `userEmail`;
* **Delete Interest** – richiesta `DELETE` verso `/api/interests` con query parameter `userEmail` e `airportCode`;
* **Get Last Flight** – richiesta `GET` verso `/api/flights/last` con parametri `airportCode` e `direction`;
* **Get Average Flights over Last N Days** – richiesta `GET` verso `/api/flights/average` con parametri `airportCode`, `direction`, `days`;
* **Query Flights by Time Interval** – richiesta `GET` verso `/api/flights` con parametri `airportCode`, `direction`, `from`, `to`.

Anche in questo caso, le richieste utilizzano in genere una variabile di ambiente Postman per la base URL (ad esempio `{{data_collector_base_url}}`), consentendo di cambiare velocemente host e porta senza modificare manualmente ogni singola richiesta.

---

### 9.3 Configurazione delle variabili di ambiente in Postman (host, port, base URL)

Per poter eseguire le richieste contro un’istanza specifica del sistema (locale o Docker), è opportuno definire in Postman uno o più **Environment** dedicati, in cui parametrizzare host, porta e base URL. Una configurazione tipica prevede:

* variabile `user_manager_base_url`, ad esempio:

  * `http://localhost:8080` in esecuzione locale;
  * `http://localhost:<HOST_PORT_UMS>` in esecuzione Docker con port‑mapping;
* variabile `data_collector_base_url`, ad esempio:

  * `http://localhost:8081` in esecuzione locale;
  * `http://localhost:<HOST_PORT_DCS>` in esecuzione Docker;
* eventuali variabili aggiuntive, come:

  * `default_user_email` per simulare uno specifico utente;
  * `default_airport_code` per impostare un aeroporto di interesse di riferimento.

Procedura consigliata per la configurazione:

1. In Postman, aprire la sezione **Environments**.

2. Creare un nuovo environment, ad esempio chiamato `Flight Monitoring Local`.

3. Aggiungere le variabili desiderate, ad esempio:

   | Key                       | Value                   |
   | ------------------------- | ----------------------- |
   | `user_manager_base_url`   | `http://localhost:8080` |
   | `data_collector_base_url` | `http://localhost:8081` |
   | `default_user_email`      | `alice@example.com`     |
   | `default_airport_code`    | `LIRF`                  |

4. Selezionare l’environment appena creato dal menu a tendina in alto a destra in Postman.

All’interno delle collection, le richieste faranno riferimento a queste variabili tramite la sintassi `{{user_manager_base_url}}`, `{{data_collector_base_url}}`, `{{default_user_email}}` e così via. Questo approccio rende semplice:

* utilizzare la stessa collection contro ambienti diversi (sviluppo, test, produzione);
* modificare host e porta da un unico punto di configurazione.

---

### 9.4 Esecuzione di scenari end-to-end tramite Postman

Le collection sono progettate per supportare scenari **end-to-end** che attraversano entrambi i microservizi, simulando il comportamento di un client applicativo reale. Le sezioni seguenti descrivono alcuni flussi tipici che possono essere eseguiti direttamente da Postman.

#### 9.4.1 Registrazione di un nuovo utente

1. Assicurarsi che:

   * lo *User Manager Service* sia in esecuzione e raggiungibile all’URL indicato da `{{user_manager_base_url}}`;
   * il database PostgreSQL sia correttamente inizializzato.

2. Aprire nella collection *User Manager API* la richiesta **Create / Register User**.

3. Verificare il body JSON, ad esempio:

   ```json
   {
     "email": "alice@example.com",
     "name": "Alice Doe"
   }
   ```

4. Inviare la richiesta e verificare la risposta:

   * `201 Created` se l’utente è stato creato ex novo;
   * `200 OK` se l’utente era già presente ed è gestita la semantica idempotente.

5. Facoltativamente, eseguire la richiesta **Get User by Email** per confermare la persistenza del record.

#### 9.4.2 Registrazione interessi utente–aeroporto

1. Verificare che l’utente da utilizzare (ad esempio `{{default_user_email}}`) sia stato registrato tramite lo scenario precedente.

2. Assicurarsi che il *Data Collector Service* sia in esecuzione e che `{{data_collector_base_url}}` punti correttamente al servizio.

3. Aprire nella collection *Data Collector API* la richiesta **Create Interest**.

4. Configurare il body JSON, ad esempio:

   ```json
   {
     "userEmail": "alice@example.com",
     "airportCode": "LIRF"
   }
   ```

5. Inviare la richiesta e osservare la risposta:

   * `201 Created` in caso di creazione di un nuovo interesse;
   * `200 OK` se l’interesse era già presente ed è gestita la semantica idempotente;
   * `404 Not Found` se l’utente non esiste (*Data Collector* non riesce a validarlo via gRPC) o se l’aeroporto non è presente nel catalogo.

6. Utilizzare la richiesta **List Interests for User** per verificare che l’interesse sia stato correttamente registrato.

#### 9.4.3 Interrogazione dello stato dei voli

Per poter interrogare lo stato dei voli è necessario che lo scheduler del *Data Collector Service* abbia già eseguito almeno un ciclo di raccolta tramite OpenSky, popolando la tabella `flight_records` nel *Data DB*.

1. Verificare, tramite i log del *Data Collector Service*, che la raccolta periodica dei voli sia attiva e che non siano presenti errori nell’interazione con le OpenSky Network API.

2. In Postman, aprire la richiesta **Get Last Flight** nella collection *Data Collector API*.

3. Impostare i parametri di query, ad esempio:

   ```text
   airportCode=LIRF
   direction=ARRIVAL
   ```

4. Inviare la richiesta e verificare la risposta `200 OK` con i dettagli dell’ultimo volo registrato per la combinazione aeroporto/direzione indicata.

5. Utilizzare poi la richiesta **Get Average Flights over Last N Days**, configurando parametri come:

   ```text
   airportCode=LIRF
   direction=DEPARTURE
   days=7
   ```

   e verificare la restituzione di `totalFlights` e `averagePerDay` nel JSON di risposta.

6. Infine, esercitare la richiesta **Query Flights by Time Interval** impostando i parametri `from` e `to` (in formato ISO‑8601) per analizzare uno specifico intervallo temporale, ad esempio:

   ```text
   from=2025-11-29T00:00:00Z
   to=2025-11-30T00:00:00Z
   ```

   e verificare che la lista di voli restituita sia coerente con i dati attesi.

Questi scenari, combinati, permettono di validare l’intero flusso: dalla registrazione dell’utente alla definizione degli interessi sugli aeroporti, fino alla consultazione dei dati di volo raccolti e storicizzati dal sistema.

## 10. Health Checks, Logs and Basic Diagnostics

### 10.1 Verifica della raggiungibilità dei servizi

La prima forma di diagnostica consiste nel verificare che i microservizi siano effettivamente **raggiungibili** e che stiano esponendo le API previste sulle porte attese.

Un controllo preliminare può essere effettuato direttamente dal terminale utilizzando `curl` oppure tramite browser o strumenti come Postman.

* Verifica della raggiungibilità dello *User Manager Service* (esempio esecuzione locale):

  ```bash
  curl -i http://localhost:8080/
  ```

* Verifica della raggiungibilità del *Data Collector Service* (esempio esecuzione locale):

  ```bash
  curl -i http://localhost:8081/
  ```

La risposta non deve necessariamente contenere un payload specifico; è sufficiente che il server risponda con un codice **2xx** o **3xx** per confermare che il processo sia in ascolto sulla porta indicata. Risposte `5xx` o errori di connessione (ad esempio *connection refused* o *timeout*) indicano un problema a livello di avvio del servizio, configurazione della porta o connettività.

In ambiente Docker, la verifica avviene analogamente utilizzando il mapping delle porte configurato nel `docker-compose.yml`, ad esempio:

```bash
curl -i http://localhost:<HOST_PORT_UMS>/
curl -i http://localhost:<HOST_PORT_DCS>/
```

#### 10.1.1 Endpoint di health (se presenti) o semplice ping

Se gli **endpoint di health** (ad esempio basati su Spring Boot Actuator) sono abilitati, rappresentano il metodo preferenziale per verificare lo stato interno del servizio.

Esempio di chiamata ad un endpoint di health standard:

```bash
curl -i http://localhost:8080/actuator/health
```

Una risposta tipica, in caso di servizio *UP*, può essere del tipo:

```json
{
  "status": "UP"
}
```

Endpoint analoghi possono essere esposti anche dal *Data Collector Service*, ad esempio:

```bash
curl -i http://localhost:8081/actuator/health
```

Se gli endpoint di health non sono disponibili o non risultano abilitati, è possibile utilizzare un **semplice ping applicativo** verso un endpoint REST funzionale noto, ad esempio una richiesta di lettura con parametri controllati:

* per lo *User Manager Service*:

  ```bash
  curl -i "http://localhost:8080/api/users/{emailDiTest}"
  ```

* per il *Data Collector Service*:

  ```bash
  curl -i "http://localhost:8081/api/interests?userEmail={emailDiTest}"
  ```

In questo caso, anche una risposta `4xx` (ad esempio `404 Not Found` per utente inesistente) può essere considerata un segnale positivo, in quanto indica che il servizio è attivo e sta elaborando correttamente la richiesta.

---

### 10.2 Log dei microservizi

I **log applicativi** costituiscono lo strumento principale per comprendere lo stato interno dei microservizi, diagnosticare errori e analizzare il comportamento del sistema durante le varie fasi di esecuzione. Il logging è gestito da Spring Boot e può essere indirizzato sia allo standard output (utilizzato in ambiente Docker) sia a file locali.

#### 10.2.1 Accesso ai log via Docker (`docker compose logs`)

Quando i servizi vengono eseguiti in Docker, i log sono accessibili tramite i comandi `docker compose logs`. Dalla cartella `docker/` è possibile:

* visualizzare i log di tutti i servizi:

  ```bash
  docker compose logs
  ```

* seguire i log in streaming:

  ```bash
  docker compose logs -f
  ```

* filtrare i log di un singolo servizio, ad esempio:

  ```bash
  docker compose logs -f user-manager-service
  ```

  ```bash
  docker compose logs -f data-collector-service
  ```

* limitare il numero di righe iniziali, ad esempio le ultime 200:

  ```bash
  docker compose logs --tail=200 -f data-collector-service
  ```

In caso di esecuzione locale senza Docker, i log sono normalmente visibili direttamente nel terminale o nella console dell’IDE da cui è stato avviato il microservizio (`mvn spring-boot:run`, run configuration, ecc.).

#### 10.2.2 Principali messaggi informativi/di errore da tenere d’occhio

Alcune categorie di messaggi di log sono particolarmente significative per la diagnostica.

**Fase di bootstrap del servizio**

* Messaggi relativi all’avvio di Spring Boot, ad esempio:

  * `Started UserManagerApplication in ... seconds`
  * `Started DataCollectorApplication in ... seconds`
* Messaggi di inizializzazione del contesto, come:

  * registrazione dei bean e mapping degli endpoint REST (`Mapped "{[/api/users],methods=[POST]}" ...`);
  * attivazione degli scheduler nel *Data Collector Service*.

La presenza di questi messaggi indica che il servizio è stato avviato correttamente e ha completato la fase di bootstrap.

**Connessione al database e migrazioni Flyway**

* Messaggi di connessione al datasource, ad esempio:

  * `HikariPool-1 - Starting...`
  * `HikariPool-1 - Start completed.`
* Messaggi di Flyway, come:

  * `Flyway Community Edition ...`;
  * `Current version of schema "public": ...`;
  * `Successfully applied ... migration(s)`.

Eventuali errori in questa fase possono manifestarsi con messaggi quali:

* `org.postgresql.util.PSQLException: Connection refused` o `could not connect to server`: indicano problemi di connettività verso PostgreSQL (host, porta o credenziali errate, database non disponibile);
* `FlywayException: Validate failed` o errori di migrazione: indicano inconsistenze tra le migrazioni attese e lo stato corrente dello schema.

**Interazione gRPC tra Data Collector e User Manager**

Nel *Data Collector Service* è importante monitorare:

* la creazione del canale gRPC verso lo *User Manager Service*;
* eventuali errori di chiamata durante la validazione degli utenti, ad esempio:

  * `UNAVAILABLE: io exception` (problemi di rete o servizio non raggiungibile);
  * `DEADLINE_EXCEEDED` (timeout sulla chiamata gRPC);
  * messaggi applicativi che indicano l’assenza dell’utente (`User not found for email ...`).

Questi log sono cruciali per diagnosticare problemi di integrazione tra i due microservizi.

**Interazione con le OpenSky Network API**

Per il *Data Collector Service*, i log relativi al client OpenSky permettono di verificare:

* il corretto ottenimento dei token OAuth2 (`Successfully obtained access token`);
* eventuali errori di autenticazione o autorizzazione (`401 Unauthorized`, `403 Forbidden`);
* errori di rete (`Read timed out`, `Connection reset`);
* eventuali limiti di rate limiting o vincoli sui parametri delle richieste.

È utile prestare attenzione a messaggi che segnalano fallimenti ripetuti nella raccolta dei dati, poiché possono indicare problemi di configurazione delle credenziali o modifiche lato provider esterno.

**Errori applicativi e REST**

Gli errori a livello di API REST vengono generalmente riportati sotto forma di eccezioni Spring (ad esempio `MethodArgumentNotValidException`, `HttpMessageNotReadableException`) e di log generati dal *Global Exception Handler*. È opportuno monitorare:

* errori sistematici di validazione input (che possono indicare problemi nei client);
* errori `500 Internal Server Error` ricorrenti su endpoint specifici, che suggeriscono bug nella logica applicativa.

---

### 10.3 Diagnostica del database

La diagnostica del database PostgreSQL consente di verificare lo stato degli schemi, la corretta applicazione delle migrazioni e la presenza dei dati attesi. È possibile accedere al database sia tramite **CLI** (`psql`) sia tramite client grafici esterni.

#### 10.3.1 Accesso a PostgreSQL (via CLI o client esterno)

Quando PostgreSQL è eseguito in Docker, è possibile accedere alla shell del container e utilizzare `psql` direttamente dall’interno:

```bash
cd docker
docker compose exec postgres bash
```

All’interno del container:

```bash
psql -U flight_monitor -d userdb
```

per connettersi al **User DB**, oppure:

```bash
psql -U flight_monitor -d datadb
```

per connettersi al **Data DB**.

Da `psql` è possibile, ad esempio:

* elencare le tabelle:

  ```sql
  \dt
  ```

* esplorare la struttura di una tabella specifica:

  ```sql
  \d users
  ```

  oppure

  ```sql
  \d flight_records
  ```

In alternativa, se PostgreSQL espone la porta `5432` sull’host (`5432:5432` nel `docker-compose.yml`), è possibile utilizzare `psql` o un client grafico (ad esempio DBeaver, DataGrip, pgAdmin) direttamente dall’host, specificando:

* host: `localhost`;
* port: `5432` (o altra porta mappata);
* database: `userdb` o `datadb`;
* user: `flight_monitor` (o altro utente configurato);
* password: quella definita in `postgres.env`.

#### 10.3.2 Verifica della creazione automatica di schemi e tabelle (Flyway)

La creazione di schemi e tabelle è gestita dai microservizi tramite **Flyway**. Per verificare che le migrazioni siano state applicate correttamente:

1. Connettersi al database di interesse (`userdb` o `datadb`) tramite `psql` o client esterno.

2. Controllare la tabella `flyway_schema_history`:

   ```sql
   SELECT installed_rank, version, description, success
   FROM flyway_schema_history
   ORDER BY installed_rank;
   ```

   Tutte le migrazioni rilevanti devono avere `success = true`. Eventuali record con `success = false` indicano problemi durante l’applicazione di una migrazione.

3. Verificare la presenza delle tabelle attese.

   Nel **User DB**:

   ```sql
   \dt
   ```

   Ci si aspetta di trovare almeno la tabella `users` e la tabella `flyway_schema_history`.

   Nel **Data DB**:

   ```sql
   \dt
   ```

   Ci si aspetta di trovare le tabelle `airports`, `user_airport_interest`, `flight_records`, oltre alla tabella `flyway_schema_history`.

4. Effettuare alcune query di controllo sui dati, ad esempio:

   * numero di utenti registrati:

     ```sql
     SELECT COUNT(*) FROM users;
     ```

   * numero di aeroporti censiti:

     ```sql
     SELECT COUNT(*) FROM airports;
     ```

   * numero di record di volo raccolti:

     ```sql
     SELECT COUNT(*) FROM flight_records;
     ```

Se il numero di record in `flight_records` è pari a zero nonostante lo scheduler del *Data Collector Service* sia attivo, è opportuno:

* controllare i log del servizio per capire se le chiamate a OpenSky hanno successo;
* verificare che esistano interessi utente–aeroporto (tabella `user_airport_interest`) per almeno un aeroporto;
* controllare che i parametri temporali utilizzati dallo scheduler siano configurati correttamente.

Questi controlli permettono di isolare rapidamente problemi legati alla **fase di bootstrap** (mancata applicazione delle migrazioni), alla **popolazione iniziale dei dati** (assenza di aeroporti o interessi) o alla **raccolta periodica dei voli** (scheduler non attivo o errori nelle chiamate alle OpenSky Network API).

## 11. Troubleshooting

### 11.1 Problemi comuni in fase di build

La fase di build può fallire per prerequisiti mancanti o per errori nella costruzione delle immagini Docker. Questa sezione elenca i casi più frequenti e le azioni consigliate per risolverli.

#### 11.1.1 Mancanza di JDK/Maven (in build locale)

Quando si esegue la build **senza Docker** (ad esempio con `mvn clean package` o `mvn spring-boot:run`), sono necessari un **JDK** supportato e **Apache Maven** correttamente installati e presenti nel `PATH`.

*Sintomi tipici*

* Il comando `mvn` non viene riconosciuto:

  ```text
  'mvn' is not recognized as an internal or external command
  ```

* Il comando `java` non è presente o punta a una versione non supportata:

  ```text
  'java' is not recognized as an internal or external command
  ```

  oppure, in caso di versione non compatibile, errori del tipo:

  ```text
  Unsupported class file major version
  ```

*Verifiche e rimedi*

1. Verificare la versione di Java:

   ```bash
   java -version
   ```

   Assicurarsi che sia installato un **JDK** compatibile (ad esempio JDK 17 o 21) e non un JRE obsoleto.

2. Verificare la versione di Maven:

   ```bash
   mvn -v
   ```

   Controllare che la versione riportata sia >= 3.8.x.

3. In caso di assenza dei comandi, installare JDK e Maven secondo le linee guida del sistema operativo in uso e aggiornare il `PATH` in modo che punti alle directory corrette.

4. Se dopo l’installazione persistono problemi, verificare l’eventuale presenza di più versioni di Java o Maven e assicurarsi che quella attivata sia coerente con i requisiti del progetto.

#### 11.1.2 Errori di build delle immagini Docker

La build tramite Docker Compose (`docker compose build` o `docker compose up --build`) può fallire per diversi motivi: problemi di rete nel download delle dipendenze, errori di compilazione del codice, configurazioni errate dei `Dockerfile`.

*Sintomi tipici*

* Errori durante la fase `mvn clean package` all’interno dello stage di build:

  ```text
  [ERROR] Failed to execute goal ... on project user-manager-service
  ```

* Errori nel recupero delle dipendenze Maven (timeout, errori DNS):

  ```text
  Could not resolve dependencies for project ...
  ```

* Errori legati al `Dockerfile` (path errati, file mancanti):

  ```text
  COPY failed: file not found in build context
  ```

*Verifiche e rimedi*

1. Verificare che la build Maven locale (fuori da Docker) vada a buon fine per ciascun microservizio:

   ```bash
   cd user-manager-service
   mvn clean package

   cd ../data-collector-service
   mvn clean package
   ```

   Se la build locale fallisce, risolvere prima gli errori riportati (problemi di compilazione, test falliti, dipendenze mancanti).

2. Controllare che il contesto di build indicato nel `docker-compose.yml` corrisponda alla radice di ciascun microservizio e che i percorsi nei `Dockerfile` (ad esempio `COPY pom.xml`, `COPY src/`) siano coerenti con la struttura effettiva del progetto.

3. In caso di errori di rete nel download delle dipendenze Maven durante la build Docker, verificare:

   * la connettività verso Internet dal nodo che esegue Docker;
   * eventuali proxy o firewall che possano bloccare l’accesso ai repository Maven pubblici.

4. Se Docker riutilizza una cache di build non coerente, può essere utile forzare la ricostruzione senza cache:

   ```bash
   docker compose build --no-cache
   ```

5. In presenza di messaggi di errore generici, eseguire la build in modo verboso (ad esempio aggiungendo `-X` a Maven nello stage di build) per ottenere maggiori dettagli.

---

### 11.2 Problemi comuni in fase di run

Una volta completata la build, la fase di esecuzione può essere ostacolata da problemi legati al database, alla connettività tra servizi o alle integrazioni esterne.

#### 11.2.1 Il database non si avvia correttamente

*Sintomi tipici*

* Il container PostgreSQL non risulta in stato `running` dopo `docker compose up`:

  ```bash
  docker compose ps
  ```

  mostra il servizio `postgres` con stato `exited` o `unhealthy`.

* I log del container mostrano errori in fase di bootstrap, ad esempio:

  ```text
  database files are incompatible with server
  ```

  oppure

  ```text
  FATAL:  password authentication failed for user "flight_monitor"
  ```

*Verifiche e rimedi*

1. Controllare i log del container PostgreSQL:

   ```bash
   cd docker
   docker compose logs postgres
   ```

   Identificare eventuali messaggi di errore relativi a:

   * credenziali errate (`POSTGRES_USER`, `POSTGRES_PASSWORD`);
   * conflitti con dati preesistenti nei volumi;
   * errori negli script di inizializzazione.

2. Verificare che le variabili d’ambiente in `postgres.env` siano coerenti con quelle utilizzate dai microservizi (`services.env`), in particolare utente, password e nomi dei database logici.

3. In caso di problemi legati a dati corrotti o a modifiche incompatibili della configurazione, può essere necessario rimuovere i volumi associati al database (attenzione: questa operazione **cancella tutti i dati persistenti**):

   ```bash
   docker compose down -v
   docker compose up -d postgres
   ```

4. Verificare che la porta host configurata per PostgreSQL non sia già occupata da un’installazione locale del database o da altri servizi. In caso di conflitto, adeguare il port mapping nel `docker-compose.yml` o fermare il servizio in conflitto.

#### 11.2.2 I servizi non riescono a connettersi a PostgreSQL

*Sintomi tipici*

* Nei log dei microservizi compaiono errori di tipo:

  ```text
  org.postgresql.util.PSQLException: Connection refused
  ```

  oppure

  ```text
  Connection to postgres:5432 refused
  ```

* Le applicazioni non completano la fase di avvio e si arrestano con errori legati al datasource.

*Verifiche e rimedi*

1. Verificare che il container PostgreSQL sia effettivamente in esecuzione e in stato `healthy`:

   ```bash
   docker compose ps
   ```

2. Controllare che il nome host del database utilizzato dai microservizi (`DB_HOST`) sia coerente con il nome del servizio nel `docker-compose.yml` (ad esempio `postgres`). In un ambiente Docker, non deve essere utilizzato `localhost` come host del database all’interno dei container.

3. Verificare che **porta**, **utente**, **password** e **nome del database** coincidano tra i file `.env` e le property Spring Boot (per i profili `docker` o equivalenti).

4. Se i microservizi vengono eseguiti in locale contro un database Dockerizzato, assicurarsi di aver adattato il `DB_HOST` a `localhost` e che il port mapping (`5432:5432` o equivalente) sia correttamente configurato.

5. In caso di errori di autenticazione (`password authentication failed`), controllare che l’utente indicato (`DB_USERNAME`) esista effettivamente in PostgreSQL e che la password impostata in `services.env` coincida con quella di `postgres.env`.

#### 11.2.3 Errori di autenticazione verso OpenSky

*Sintomi tipici*

* Nei log del *Data Collector Service* compaiono errori durante l’ottenimento del token OAuth2, ad esempio:

  ```text
  401 Unauthorized
  invalid_client
  ```

  oppure

  ```text
  403 Forbidden
  ```

* Le richieste alle OpenSky Network API falliscono sistematicamente e lo scheduler non riesce a popolare la tabella `flight_records`.

*Verifiche e rimedi*

1. Verificare che le variabili d’ambiente `OPEN_SKY_CLIENT_ID` e `OPEN_SKY_CLIENT_SECRET` siano impostate con valori reali e non con placeholder (ad esempio `your_client_id`).

2. Controllare che gli endpoint configurati (`OPEN_SKY_AUTH_BASE_URL`, `OPEN_SKY_API_BASE_URL`) siano corretti e aggiornati rispetto alla documentazione ufficiale di OpenSky.

3. Assicurarsi che l’account OpenSky associato alle credenziali sia attivo e abilitato all’utilizzo delle API richieste.

4. Esaminare i log completi del *Data Collector Service* durante il tentativo di autenticazione per individuare dettagli aggiuntivi restituiti dal server OAuth2 (errori di formato della richiesta, grant type non supportato, scope non valido).

5. In presenza di errori intermittenti dovuti a problemi di rete o di disponibilità del servizio OpenSky, valutare l’introduzione di meccanismi di retry e backoff (se non già presenti) o eseguire nuovamente il sistema in un momento successivo.

---

### 11.3 Verifiche passo-passo per isolare gli errori

Le seguenti verifiche guidano un percorso **step-by-step** per isolare i problemi più comuni, partendo dallo strato di configurazione fino ai singoli container.

#### 11.3.1 Verifica variabili d’ambiente

1. Controllare il contenuto dei file `.env` nella cartella `docker/env/` (`postgres.env`, `services.env`) e assicurarsi che non contengano valori evidentemente errati o placeholder.

2. Dal terminale, verificare che le variabili critiche siano effettivamente visibili nel contesto da cui verrà lanciato `docker compose`. Ad esempio:

   ```bash
   echo "$DB_HOST"
   echo "$DB_USERNAME"
   echo "$OPEN_SKY_CLIENT_ID"
   ```

   Se i valori non risultano impostati, è possibile che vengano letti esclusivamente dai file `.env` a livello di Docker Compose (in tal caso il controllo va effettuato leggendo direttamente tali file) o che sia necessario esportarli manualmente per l’esecuzione locale dei microservizi.

3. Verificare eventuali errori di digitazione nei nomi delle variabili, tanto nei file `.env` quanto nelle proprietà Spring Boot che le referenziano (ad esempio `${DB_HOST}` vs `${DB_HOSTNAME}`).

#### 11.3.2 Verifica delle porte occupate

1. Controllare che le porte configurate per PostgreSQL e per i microservizi non siano già occupate da altri processi sull’host. È possibile utilizzare comandi come:

   * su sistemi Unix-like:

     ```bash
     lsof -i :5432
     lsof -i :8080
     lsof -i :8081
     ```

   * su Windows:

     ```powershell
     netstat -ano | findstr ":5432"
     netstat -ano | findstr ":8080"
     netstat -ano | findstr ":8081"
     ```

2. Se una porta risulta occupata, identificare il processo che la utilizza e valutare se può essere arrestato o se è opportuno modificare il port mapping nel `docker-compose.yml` o la porta di ascolto del servizio.

3. Dopo ogni modifica di port mapping, ricordarsi di aggiornare le configurazioni dei client (Postman, script di test) e le variabili di ambiente correlate (ad esempio `user_manager_base_url`, `data_collector_base_url` in Postman).

#### 11.3.3 Controllo dei log dei singoli container

1. Elencare lo stato di tutti i servizi definiti in `docker-compose.yml`:

   ```bash
   cd docker
   docker compose ps
   ```

   Verificare che tutti i container attesi siano in stato `running` o `healthy`.

2. In caso di problemi con un servizio specifico, esaminare i log dedicati:

   ```bash
   docker compose logs user-manager-service
   docker compose logs data-collector-service
   docker compose logs postgres
   ```

3. Per analizzare il comportamento in tempo reale, utilizzare l’opzione `-f`:

   ```bash
   docker compose logs -f user-manager-service
   ```

   e ripetere le operazioni che causano l’errore (avvio del sistema, chiamate API di test) osservando i messaggi che compaiono.

4. Prestare particolare attenzione a:

   * stack trace di eccezioni non gestite;
   * errori di connessione verso database o servizi esterni;
   * errori di validazione legati a input non conformi.

5. Se nonostante l’analisi dei log il problema rimane poco chiaro, è possibile aumentare temporaneamente il livello di log per determinati package o componenti (ad esempio impostando il livello `DEBUG` per i package di integrazione con OpenSky o con PostgreSQL) attraverso le proprietà Spring Boot dedicate o file di configurazione del logging.

## 12. Validation Scenarios

### 12.1 Scenario minimo di smoke test

Lo scenario di *smoke test* ha l’obiettivo di verificare che l’intera piattaforma sia in grado di:

* avviarsi correttamente;
* accettare richieste di gestione utenti;
* registrare interessi utente–aeroporto;
* raccogliere e rendere disponibili dati di volo di base.

#### 12.1.1 Avvio del sistema

1. Assicurarsi che i prerequisiti hardware e software siano soddisfatti e che le variabili d’ambiente critiche siano state configurate.

2. Posizionarsi nella directory `docker/` della repository:

   ```bash
   cd docker
   ```

3. Avviare l’intero sistema in modalità containerizzata:

   ```bash
   docker compose up --build -d
   ```

4. Verificare lo stato dei servizi:

   ```bash
   docker compose ps
   ```

   Tutti i container devono risultare in stato `running` o `healthy`.

5. Controllare rapidamente i log per accertarsi dell’assenza di errori gravi in fase di bootstrap:

   ```bash
   docker compose logs --tail=100
   ```

   In particolare, è opportuno verificare che:

   * PostgreSQL sia operativo e pronto ad accettare connessioni;
   * le migrazioni Flyway siano state applicate con successo;
   * i microservizi abbiano completato la fase di avvio ed espongano le porte previste.

#### 12.1.2 Creazione di un utente di test

1. Utilizzando Postman o `curl`, inviare una richiesta `POST` allo *User Manager Service* per creare un utente di test, ad esempio:

   ```bash
   curl -i -X POST "http://localhost:<HOST_PORT_UMS>/api/users" \
     -H "Content-Type: application/json" \
     -d '{
       "email": "smoke.user@example.com",
       "name": "Smoke User"
     }'
   ```

2. Verificare che la risposta abbia uno dei seguenti codici:

   * `201 Created` se l’utente viene creato ex novo;
   * `200 OK` se la logica applicativa prevede idempotenza sulla creazione e l’utente esiste già.

3. Accertarsi che il payload di risposta contenga almeno l’indirizzo e‑mail e gli altri campi essenziali dell’utente.

4. Facoltativamente, connettersi al database `userdb` e verificare la presenza dell’utente nella tabella `users`, ad esempio:

   ```sql
   SELECT * FROM users WHERE email = 'smoke.user@example.com';
   ```

#### 12.1.3 Registrazione di un interesse per un aeroporto

1. Se non già presente, scegliere un codice aeroporto valido e coerente con la configurazione del sistema (ad esempio un codice IATA o ICAO caricato nella tabella `airports`).

2. Invocare l’endpoint REST del *Data Collector Service* per creare un interesse utente–aeroporto, ad esempio:

   ```bash
   curl -i -X POST "http://localhost:<HOST_PORT_DCS>/api/interests" \
     -H "Content-Type: application/json" \
     -d '{
       "userEmail": "smoke.user@example.com",
       "airportCode": "LIRF"
     }'
   ```

3. Verificare che la risposta sia `201 Created` o `200 OK`, in base alla semantica implementata.

4. Controllare che un’eventuale risposta di errore (`404 Not Found`, `400 Bad Request`) sia coerente con la causa (utente non esistente, codice aeroporto non valido, ecc.) e che nei log del *Data Collector Service* non compaiano eccezioni inattese.

5. Facoltativamente, interrogare il database `datadb` per verificare la presenza dell’interesse nella tabella `user_airport_interest`, ad esempio:

   ```sql
   SELECT *
   FROM user_airport_interest
   WHERE user_email = 'smoke.user@example.com'
     AND airport_id = (SELECT id FROM airports WHERE code = 'LIRF');
   ```

#### 12.1.4 Verifica del popolamento dei dati di volo

1. Attendere che lo scheduler del *Data Collector Service* esegua almeno un ciclo di raccolta dei dati di volo per gli aeroporti con interessi attivi. L’intervallo di esecuzione è definito dalla configurazione del servizio (cron o intervallo fisso).

2. Monitorare i log del *Data Collector Service* per individuare messaggi che indicano:

   * l’avvenuta autenticazione verso OpenSky;
   * il successo delle chiamate alle API esterne;
   * l’inserimento di nuovi record nella tabella `flight_records`.

3. Interrogare il database `datadb` per verificare la presenza di record di volo associati all’aeroporto di test, ad esempio:

   ```sql
   SELECT COUNT(*)
   FROM flight_records
   WHERE airport_code = 'LIRF';
   ```

4. Utilizzare le API REST del *Data Collector Service* (ad esempio `GET /api/flights/last` o `GET /api/flights/average`) per controllare che i dati appena raccolti siano effettivamente esposti e interrogabili.

---

### 12.2 Scenario di test della politica at-most-once

Lo scenario di validazione dell’**at-most-once semantics** ha lo scopo di verificare che operazioni concettualmente idempotenti (come la registrazione di un utente o di un interesse utente–aeroporto) non generino duplicati né in memoria né a livello di database.

#### 12.2.1 Ripetizione di una registrazione utente

1. Assicurarsi che lo *User Manager Service* sia in esecuzione e che il database sia accessibile.

2. Inviare una prima richiesta di registrazione utente, ad esempio:

   ```bash
   curl -i -X POST "http://localhost:<HOST_PORT_UMS>/api/users" \
     -H "Content-Type: application/json" \
     -d '{
       "email": "idempotent.user@example.com",
       "name": "Idempotent User"
     }'
   ```

3. Verificare la risposta (`201 Created` o `200 OK`) e, facoltativamente, controllare che l’utente sia presente nella tabella `users`.

4. Ripetere la stessa richiesta **senza modificare** il payload.

5. Osservare il comportamento del servizio e dei log applicativi.

*Comportamento atteso*

* Non devono essere creati **nuovi record** nella tabella `users`.
* Il servizio può rispondere con:

  * `200 OK` restituendo l’utente già esistente;
  * oppure `409 Conflict` con un messaggio esplicito che segnali il tentativo di creare un duplicato.
* Nei log non devono comparire eccezioni di violazione di vincoli di unicità non gestite.

#### 12.2.2 Comportamento atteso (assenza di duplicati, codici HTTP attesi)

1. Eseguire una query sul database per verificare il numero di record per l’indirizzo e‑mail utilizzato nel test:

   ```sql
   SELECT COUNT(*)
   FROM users
   WHERE email = 'idempotent.user@example.com';
   ```

   Il risultato atteso è **1**.

2. Ripetere un test analogo per la registrazione di un interesse utente–aeroporto, ad esempio inviando due volte la stessa richiesta `POST /api/interests` con identico `userEmail` e `airportCode`.

3. Verificare che il numero di record nella tabella `user_airport_interest` per la coppia `(user_email, airport_id)` rimanga **1**, grazie al vincolo di unicità applicato a livello di schema e alla logica applicativa.

4. Controllare i codici di risposta HTTP:

   * la prima richiesta deve portare alla creazione dell’interesse (`201 Created` o `200 OK` in base alla semantica scelta);
   * le richieste successive devono produrre una risposta coerente con la politica di idempotenza, senza causare errori di violazione di vincoli non gestiti.

---

### 12.3 Scenario di interrogazione dei voli su intervalli temporali

Questo scenario verifica che le API di interrogazione dei voli su intervalli temporali gestiscano correttamente i parametri di input, filtrino in modo coerente i dati e restituiscano risposte strutturate e consistenti.

1. Assicurarsi che il *Data Collector Service* abbia già popolato la tabella `flight_records` con un numero significativo di record per almeno un aeroporto di interesse.

2. Identificare un intervallo temporale per cui si ha ragionevole certezza dell’esistenza di dati, ad esempio basandosi sulla finestra temporale utilizzata dallo scheduler di raccolta.

3. Invocare l’endpoint di interrogazione per intervallo temporale, ad esempio:

   ```bash
   curl -i "http://localhost:<HOST_PORT_DCS>/api/flights?airportCode=LIRF&direction=ARRIVAL&from=2025-11-29T00:00:00Z&to=2025-11-30T00:00:00Z"
   ```

4. Verificare che:

   * la risposta abbia codice `200 OK` in presenza di parametri validi;
   * il payload contenga una lista di voli con campi coerenti con il modello `FlightRecord` (identificativi, orari, direzione, aeroporto, ritardi, ecc.);
   * non siano restituiti voli al di fuori dell’intervallo `[from, to]` specificato.

5. Ripetere la chiamata variando i parametri di intervallo temporale per coprire i seguenti casi:

   * intervallo **senza dati** (ad esempio un periodo precedente all’avvio della raccolta): il comportamento atteso è una lista vuota con `200 OK`, non un errore;
   * inversione dei parametri (`from` successivo a `to`): il comportamento atteso è un errore di validazione (`400 Bad Request`) con un messaggio esplicito;
   * intervallo molto ampio: il sistema deve continuare a rispondere in tempi ragionevoli e con un payload coerente, eventualmente limitando i risultati se previsto.

6. Per un controllo aggiuntivo, eseguire query dirette sul database `datadb` per verificare la corrispondenza tra i record restituiti dall’API e i dati presenti nella tabella `flight_records`, ad esempio:

   ```sql
   SELECT *
   FROM flight_records
   WHERE airport_code = 'LIRF'
     AND direction = 'ARRIVAL'
     AND scheduled_time >= '2025-11-29T00:00:00Z'
     AND scheduled_time < '2025-11-30T00:00:00Z';
   ```

   I risultati della query devono essere coerenti con il contenuto della risposta JSON ottenuta dall’endpoint REST.

Questi scenari di validazione forniscono una base operativa per verificare rapidamente il corretto comportamento del sistema nelle principali aree funzionali: gestione utenti, registrazione degli interessi e interrogazione dei dati di volo.