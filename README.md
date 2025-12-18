# README — Build & Deploy Guide

## Indice

* [1. Project Overview](#1-project-overview)

  * [1.1 Descrizione sintetica del sistema di Flight Monitoring](#11-descrizione-sintetica-del-sistema-di-flight-monitoring)
  * [1.2 Microservizi coinvolti](#12-microservizi-coinvolti)

    * [1.2.1 User Manager Service](#121-user-manager-service)
    * [1.2.2 Data Collector Service](#122-data-collector-service)
    * [1.2.3 Alert System Service](#123-alert-system-service)
    * [1.2.4 Alert Notifier Service](#124-alert-notifier-service)
    * [1.2.5 API Gateway](#125-api-gateway)
    * [1.2.6 Kafka Broker](#126-kafka-broker)
  * [1.3 Componenti esterni](#13-componenti-esterni)

    * [1.3.1 OpenSky Network API](#131-opensky-network-api)
    * [1.3.2 Database PostgreSQL](#132-database-postgresql)
    * [1.3.3 Mailtrap (Email Testing SMTP)](#133-mailtrap-email-testing-smtp)
  * [1.4 Riferimenti alla documentazione di progetto](#14-riferimenti-alla-documentazione-di-progetto)

    * [1.4.1 Relazioni tecniche](#141-relazioni-tecniche)
    * [1.4.2 Diagrammi architetturali e di sequenza](#142-diagrammi-architetturali-e-di-sequenza)
    * [1.4.3 Diagramma Entity–Relationship (ER)](#143-diagramma-entityrelationship-er)
    * [1.4.4 Evoluzione del sistema per release](#144-evoluzione-del-sistema-per-release)

* [2. Repository Structure](#2-repository-structure)

  * [2.1 Root layout della repository](#21-root-layout-della-repository)
  * [2.2 Struttura delle cartelle principali](#22-struttura-delle-cartelle-principali)

    * [2.2.1 Servizi applicativi (User Manager, Data Collector, Alert System, Alert Notifier)](#221-servizi-applicativi-user-manager-data-collector-alert-system-alert-notifier)
    * [2.2.2 Infrastruttura Docker (PostgreSQL, Kafka, Zookeeper, Kafka UI, API Gateway)](#222-infrastruttura-docker-postgresql-kafka-zookeeper-kafka-ui-api-gateway)
    * [2.2.3 Documentazione e diagrammi](#223-documentazione-e-diagrammi)
    * [2.2.4 Postman collections](#224-postman-collections)
  * [2.3 File chiave per build & deploy](#23-file-chiave-per-build--deploy)

* [3. Prerequisites](#3-prerequisites)

  * [3.1 Requisiti hardware e sistema operativo](#31-requisiti-hardware-e-sistema-operativo)
  * [3.2 Software necessario](#32-software-necessario)

    * [3.2.1 Docker](#321-docker)
    * [3.2.2 Docker Compose](#322-docker-compose)
    * [3.2.3 JDK (per build/esecuzione locale senza Docker)](#323-jdk-per-buildesecuzione-locale-senza-docker)
    * [3.2.4 Maven (per build/esecuzione-locale-senza-docker)](#324-maven-per-buildesecuzione-locale-senza-docker)
  * [3.3 Account e credenziali esterne](#33-account-e-credenziali-esterne)

    * [3.3.1 Registrazione a OpenSky Network](#331-registrazione-a-opensky-network)
    * [3.3.2 Ottenimento delle credenziali OAuth2 (client id/secret)](#332-ottenimento-delle-credenziali-oauth2-client-idsecret)
    * [3.3.3 Configurazione di un account Mailtrap (SMTP testing)](#333-configurazione-di-un-account-mailtrap-smtp-testing)
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
  * [4.5 Configurazione del sistema di posta (Mailtrap SMTP)](#45-configurazione-del-sistema-di-posta-mailtrap-smtp)

    * [4.5.1 Variabili d’ambiente `MAIL_*` per l’Alert Notifier](#451-variabili-dambiente-mail_-per-lalert-notifier)
    * [4.5.2 Considerazioni su mittente, autenticazione e TLS](#452-considerazioni-su-mittente-autenticazione-e-tls)
  * [4.6 Profili e configurazioni Spring Boot](#46-profili-e-configurazioni-spring-boot)

    * [4.6.1 Uso del profilo di default e overriding tramite variabili d’ambiente](#461-uso-del-profilo-di-default-e-overriding-tramite-variabili-dambiente)
    * [4.6.2 Configurazioni per l’esecuzione in ambiente Docker (`application-docker.yml`)](#462-configurazioni-per-lesecuzione-in-ambiente-docker-application-dockeryml)
    * [4.6.3 Configurazione del Circuit Breaker Resilience4j verso OpenSky](#463-configurazione-del-circuit-breaker-resilience4j-verso-opensky)

* [5. Build Instructions](#5-build-instructions)

  * [5.1 Build tramite Docker (modalità raccomandata)](#51-build-tramite-docker-modalità-raccomandata)

    * [5.1.1 Posizionamento nella cartella corretta](#511-posizionamento-nella-cartella-corretta)
    * [5.1.2 Comando per buildare le immagini con Docker Compose](#512-comando-per-buildare-le-immagini-con-docker-compose)
    * [5.1.3 Descrizione dei Dockerfile dei microservizi (multi-stage build)](#513-descrizione-dei-dockerfile-dei-microservizi-multi-stage-build)
  * [5.2 Build locale senza Docker (opzionale)](#52-build-locale-senza-docker-opzionale)

    * [5.2.1 Build dello User Manager Service con Maven](#521-build-dello-user-manager-service-con-maven)
    * [5.2.2 Build del Data Collector Service con Maven](#522-build-del-data-collector-service-con-maven)
    * [5.2.3 Build dell’Alert System Service con Maven](#523-build-dellalert-system-service-con-maven)
    * [5.2.4 Build dell’Alert Notifier Service con Maven](#524-build-dellalert-notifier-service-con-maven)
    * [5.2.5 Differenze rispetto alla modalità Docker-based](#525-differenze-rispetto-alla-modalità-docker-based)

* [6. Deploy & Run with Docker Compose](#6-deploy--run-with-docker-compose)

  * [6.1 Prima esecuzione dello stack completo](#61-prima-esecuzione-dello-stack-completo)

    * [6.1.1 Preparazione dei file `.env`](#611-preparazione-dei-file-env)
    * [6.1.2 Avvio dei servizi (`docker compose up -d` / `docker compose up --build`)](#612-avvio-dei-servizi-docker-compose-up--d--docker-compose-up---build)
    * [6.1.3 Verifica che i container siano in esecuzione](#613-verifica-che-i-container-siano-in-esecuzione)
  * [6.2 Arresto del sistema](#62-arresto-del-sistema)

    * [6.2.1 Comando di stop (`docker compose down`)](#621-comando-di-stop-docker-compose-down)
    * [6.2.2 Rimozione volumi/persistenza (se necessario)](#622-rimozione-volumipersistenza-se-necessario)
  * [6.3 Comandi Docker utili](#63-comandi-docker-utili)

    * [6.3.1 Visualizzazione log di un singolo servizio](#631-visualizzazione-log-di-un-singolo-servizio)
    * [6.3.2 Accesso alla shell di un container](#632-accesso-alla-shell-di-un-container)
    * [6.3.3 Verifica delle porte esposte](#633-verifica-delle-porte-esposte)
  * [6.4 Accesso ai servizi infrastrutturali (Kafka UI, API Gateway)](#64-accesso-ai-servizi-infrastrutturali-kafka-ui-api-gateway)

* [7. Accessing the Services](#7-accessing-the-services)

  * [7.1 User Manager Service](#71-user-manager-service)

    * [7.1.1 Endpoint base (host, port)](#711-endpoint-base-host-port)
    * [7.1.2 Principali API REST esposte (registrazione, lettura, cancellazione utente)](#712-principali-api-rest-esposte-registrazione-lettura-cancellazione-utente)
    * [7.1.3 Codici di risposta attesi per le operazioni chiave](#713-codici-di-risposta-attesi-per-le-operazioni-chiave)
  * [7.2 Data Collector Service](#72-data-collector-service)

    * [7.2.1 Endpoint base (host, port)](#721-endpoint-base-host-port)
    * [7.2.2 API REST per la gestione degli aeroporti e degli interessi (incluse le soglie)](#722-api-rest-per-la-gestione-degli-aeroporti-e-degli-interessi-incluse-le-soglie)
    * [7.2.3 API REST per interrogare i voli](#723-api-rest-per-interrogare-i-voli)
  * [7.3 gRPC Interface](#73-grpc-interface)

    * [7.3.1 Panoramica del servizio gRPC esposto dallo User Manager](#731-panoramica-del-servizio-grpc-esposto-dallo-user-manager)
    * [7.3.2 Utilizzo interno da parte del Data Collector (non richiesto lato utente finale)](#732-utilizzo-interno-da-parte-del-data-collector-non-richiesto-lato-utente-finale)
  * [7.4 API Gateway](#74-api-gateway)

    * [7.4.1 Endpoint pubblici esposti dal gateway](#741-endpoint-pubblici-esposti-dal-gateway)
    * [7.4.2 Instradamento verso i microservizi interni](#742-instradamento-verso-i-microservizi-interni)
  * [7.5 Alert System & Alert Notifier](#75-alert-system--alert-notifier)

    * [7.5.1 Ruolo dei servizi nella pipeline di notifica](#751-ruolo-dei-servizi-nella-pipeline-di-notifica)
    * [7.5.2 Osservazione del flusso tramite log e Kafka UI](#752-osservazione-del-flusso-tramite-log-e-kafka-ui)

* [8. Using Postman Collections](#8-using-postman-collections)

  * [8.1 Localizzazione delle collection (`postman/`)](#81-localizzazione-delle-collection-postman)
  * [8.2 Import delle collection in Postman](#82-import-delle-collection-in-postman)

    * [8.2.1 User Manager API collection](#821-user-manager-api-collection)
    * [8.2.2 Data Collector API collection](#822-data-collector-api-collection)
  * [8.3 Configurazione delle variabili di ambiente in Postman (host, port, base URL)](#83-configurazione-delle-variabili-di-ambiente-in-postman-host-port-base-url)
  * [8.4 Esecuzione di scenari end-to-end tramite Postman](#84-esecuzione-di-scenari-end-to-end-tramite-postman)

    * [8.4.1 Registrazione di un nuovo utente](#841-registrazione-di-un-nuovo-utente)
    * [8.4.2 Registrazione interessi utente–aeroporto con soglie](#842-registrazione-interessi-utenteaeroporto-con-soglie)
    * [8.4.3 Interrogazione dello stato dei voli](#843-interrogazione-dello-stato-dei-voli)

* [9. Health Checks, Logs and Basic Diagnostics](#9-health-checks-logs-and-basic-diagnostics)

  * [9.1 Verifica della raggiungibilità dei servizi](#91-verifica-della-raggiungibilità-dei-servizi)

    * [9.1.1 Endpoint di health (se presenti) o semplice ping](#911-endpoint-di-health-se-presenti-o-semplice-ping)
  * [9.2 Log dei microservizi](#92-log-dei-microservizi)

    * [9.2.1 Accesso ai log via Docker (`docker compose logs`)](#921-accesso-ai-log-via-docker-docker-compose-logs)
    * [9.2.2 Principali messaggi informativi/di errore da tenere d’occhio](#922-principali-messaggi-informativodi-errore-da-tenere-docchio)
  * [9.3 Diagnostica del database](#93-diagnostica-del-database)

    * [9.3.1 Accesso a PostgreSQL (via CLI o client esterno)](#931-accesso-a-postgresql-via-cli-o-client-esterno)
    * [9.3.2 Verifica della creazione automatica di schemi e tabelle (Flyway)](#932-verifica-della-creazione-automatica-di-schemi-e-tabelle-flyway)
  * [9.4 Diagnostica di Kafka e del sistema di posta](#94-diagnostica-di-kafka-e-del-sistema-di-posta)

    * [9.4.1 Verifica dei topic e dei messaggi tramite Kafka UI](#941-verifica-dei-topic-e-dei-messaggi-tramite-kafka-ui)
    * [9.4.2 Verifica dell’invio email tramite Mailtrap](#942-verifica-dellinvio-email-tramite-mailtrap)

* [10. Troubleshooting](#10-troubleshooting)

  * [10.1 Problemi comuni in fase di build](#101-problemi-comuni-in-fase-di-build)

    * [10.1.1 Mancanza di JDK/Maven (in build locale)](#1011-mancanza-di-jdkmaven-in-build-locale)
    * [10.1.2 Errori di build delle immagini Docker](#1012-errori-di-build-delle-immagini-docker)
  * [10.2 Problemi comuni in fase di run](#102-problemi-comuni-in-fase-di-run)

    * [10.2.1 Il database non si avvia correttamente](#1021-il-database-non-si-avvia-correttamente)
    * [10.2.2 I servizi non riescono a connettersi a PostgreSQL](#1022-i-servizi-non-riescono-a-connettersi-a-postgresql)
    * [10.2.3 Errori di autenticazione verso OpenSky](#1023-errori-di-autenticazione-verso-opensky)
    * [10.2.4 Problemi di connessione a Kafka](#1024-problemi-di-connessione-a-kafka)
    * [10.2.5 Errori SMTP e mancato recapito delle email](#1025-errori-smtp-e-mancato-recapito-delle-email)
    * [10.2.6 Comportamento del Circuit Breaker verso OpenSky](#1026-comportamento-del-circuit-breaker-verso-opensky)
  * [10.3 Verifiche passo-passo per isolare gli errori](#103-verifiche-passo-passo-per-isolare-gli-errori)

    * [10.3.1 Verifica variabili d’ambiente](#1031-verifica-variabili-dambiente)
    * [10.3.2 Verifica delle porte occupate](#1032-verifica-delle-porte-occupate)
    * [10.3.3 Controllo dei log dei singoli container](#1033-controllo-dei-log-dei-singoli-container)

* [11. Validation Scenarios](#11-validation-scenarios)

  * [11.1 Scenario minimo di smoke test](#111-scenario-minimo-di-smoke-test)

    * [11.1.1 Avvio del sistema](#1111-avvio-del-sistema)
    * [11.1.2 Creazione di un utente di test](#1112-creazione-di-un-utente-di-test)
    * [11.1.3 Registrazione di un interesse per un aeroporto](#1113-registrazione-di-un-interesse-per-un-aeroporto)
    * [11.1.4 Verifica del popolamento dei dati di volo](#1114-verifica-del-popolamento-dei-dati-di-volo)
  * [11.2 Scenario di test della politica at-most-once](#112-scenario-di-test-della-politica-at-most-once)

    * [11.2.1 Ripetizione di una registrazione utente](#1121-ripetizione-di-una-registrazione-utente)
    * [11.2.2 Comportamento atteso (assenza di duplicati, codici HTTP attesi)](#1122-comportamento-atteso-assenza-di-duplicati-codici-http-attesi)
  * [11.3 Scenario di interrogazione dei voli su intervalli temporali](#113-scenario-di-interrogazione-dei-voli-su-intervalli-temporali)
  * [11.4 Scenario di configurazione e valutazione delle soglie](#114-scenario-di-configurazione-e-valutazione-delle-soglie)

    * [11.4.1 Creazione di un interesse con `highValue`/`lowValue`](#1141-creazione-di-un-interesse-con-highvalue-lowvalue)
    * [11.4.2 Generazione di un carico di voli che superi la soglia](#1142-generazione-di-un-carico-di-voli-che-superi-la-soglia)
  * [11.5 Scenario end-to-end della pipeline di notifica](#115-scenario-end-to-end-della-pipeline-di-notifica)

    * [11.5.1 Pubblicazione su `to-alert-system` e propagazione su `to-notifier`](#1151-pubblicazione-su-to-alert-system-e-propagazione-su-to-notifier)
    * [11.5.2 Verifica finale della ricezione email](#1152-verifica-finale-della-ricezione-email)
  * [11.6 Scenario con indisponibilità di OpenSky e Circuit Breaker attivo](#116-scenario-con-indisponibilità-di-opensky-e-circuit-breaker-attivo)

## 1. Project Overview

### 1.1 Descrizione sintetica del sistema di Flight Monitoring

Il sistema è una piattaforma di *flight monitoring* basata su microservizi, progettata per raccogliere, persistere ed esporre informazioni sui voli in arrivo e in partenza da uno o più aeroporti di interesse.

L’obiettivo principale è fornire a un client esterno un insieme di API **coerenti, stabili e facilmente integrabili** per:

* registrare e gestire utenti identificati univocamente tramite **indirizzo e‑mail**;
* associare a ciascun utente uno o più aeroporti di interesse;
* raccogliere periodicamente, da una sorgente esterna autorevole (OpenSky Network), i dati di volo in arrivo e in partenza per tali aeroporti;
* memorizzare i dati di volo in un **database relazionale** per consentire interrogazioni efficienti e consistenti;
* esporre API dedicate a interrogazioni di tipo operativo e analitico, ad esempio:

  * ultimo volo in arrivo o in partenza per un aeroporto;
  * interrogazioni su intervalli temporali arbitrari;
* valutare le informazioni raccolte rispetto a **soglie di interesse configurabili per utente e aeroporto** (ad esempio ritardi minimi o massimi);
* **generare notifiche asincrone via e‑mail** quando determinate condizioni sugli eventi di volo violano le soglie configurate.

L’architettura è pensata per essere **modulare**, **estendibile** e orientata a una chiara separazione dei confini di responsabilità: la gestione degli utenti, la raccolta dei dati di volo, la valutazione delle soglie e l’invio delle notifiche sono affidati a componenti distinti, orchestrati tramite un mix di comunicazioni sincrone (REST/gRPC) e asincrone (Kafka).

---

### 1.2 Microservizi coinvolti

L’applicazione è suddivisa in più microservizi Spring Boot **autonomi**, ciascuno responsabile di un sottoinsieme specifico del dominio applicativo e dotato di una sua logica, configurazione e ciclo di rilascio:

* lo *User Manager Service* governa il ciclo di vita degli utenti e la loro validazione;
* il *Data Collector Service* si occupa della gestione degli aeroporti di interesse, della registrazione degli interessi utente–aeroporto (comprensivi di soglie) e della raccolta periodica dei dati di volo;
* l’*Alert System Service* elabora gli eventi di volo raccolti, valuta le soglie configurate e individua i casi che richiedono una notifica;
* l’*Alert Notifier Service* riceve gli eventi di notifica e provvede all’invio delle e‑mail verso gli utenti finali;
* l’*API Gateway* centralizza l’esposizione delle API HTTP verso l’esterno e instrada le richieste verso i microservizi interni appropriati;
* il *Kafka Broker* fornisce l’infrastruttura di messaggistica per i flussi asincroni tra Data Collector, Alert System e Alert Notifier.

Ogni microservizio applicativo utilizza un proprio schema logico all’interno di un’istanza PostgreSQL condivisa, espone API o interfacce specializzate (REST, gRPC, consumer/producer Kafka) e incapsula la logica di dominio in servizi dedicati, mantenendo separato il livello di esposizione delle API dal livello di persistenza e dalle integrazioni infrastrutturali.

#### 1.2.1 User Manager Service

Lo **User Manager Service** gestisce il **sottodominio utente** e offre funzionalità di registrazione, consultazione e cancellazione. Le responsabilità principali sono:

* registrare un nuovo utente a partire da una richiesta contenente almeno e‑mail e nome;
* garantire l’unicità dell’utente tramite vincoli a livello di dominio e di database (chiave primaria sull’indirizzo e‑mail);
* rendere disponibili endpoint REST per la consultazione degli utenti registrati e per eventuali operazioni di amministrazione;
* esporre un servizio **gRPC** di validazione (*userExists*), utilizzato dagli altri microservizi per verificare la presenza di un indirizzo e‑mail nel *User DB*.

Il servizio è progettato per essere relativamente stabile nel tempo, fungendo da *authority* centrale per l’identità applicativa degli utenti e consentendo di mantenere distinto il ciclo di vita degli utenti rispetto alla gestione degli interessi e dei dati di volo.

#### 1.2.2 Data Collector Service

Il **Data Collector Service** governa il **sottodominio aeroporti, interessi e dati di volo**. Le responsabilità principali includono:

* la gestione del catalogo degli aeroporti monitorabili (creazione, aggiornamento, consultazione);
* la registrazione degli **interessi utente–aeroporto**, comprensivi di eventuali soglie *high_value* e *low_value* che caratterizzano il livello di attenzione desiderato;
* la schedulazione e l’esecuzione periodica delle chiamate verso le **OpenSky Network API** per recuperare i voli in arrivo e in partenza relativi agli aeroporti di interesse;
* la persistenza dei **flight records** nel *Data DB*, con tracciamento del momento di raccolta e degli eventuali ritardi;
* la pubblicazione, sul topic Kafka dedicato, degli eventi di volo che soddisfano le condizioni per una potenziale notifica (ad esempio superamento di determinate soglie);
* l’esposizione di API REST per interrogare i dati di volo e gli interessi registrati.

Il servizio utilizza il client HTTP dedicato a OpenSky, integra la logica di **Circuit Breaker** tramite Resilience4j per proteggere le chiamate verso il servizio esterno e si appoggia al servizio gRPC dello User Manager per validare l’esistenza degli utenti prima di registrare nuovi interessi.

#### 1.2.3 Alert System Service

L’**Alert System Service** è il componente incaricato di valutare le soglie configurate sugli interessi utente–aeroporto alla luce dei dati di volo effettivamente raccolti. Dal punto di vista architetturale:

* si comporta da **consumer Kafka** sul topic (ad esempio `to-alert-system`) su cui il Data Collector pubblica gli eventi di volo rilevanti;
* per ciascun evento ricevuto, applica la logica di confronto rispetto alle soglie *high_value* e *low_value* associate all’interesse corrispondente;
* identifica i casi in cui è necessario attivare una notifica (ad esempio ritardi superiori a una certa soglia configurata dall’utente);
* produce un nuovo messaggio su un secondo topic Kafka (ad esempio `to-notifier`), contenente tutte le informazioni necessarie all’invio dell’e‑mail.

In questo modo il servizio separa in modo netto la **logica di valutazione delle condizioni di alert** dalla raccolta dei dati e dall’invio effettivo delle notifiche, favorendo una maggiore manutenibilità e la possibilità di estendere le regole di alert in versioni successive.

#### 1.2.4 Alert Notifier Service

L’**Alert Notifier Service** è responsabile dell’**invio delle notifiche e‑mail** verso gli utenti interessati.

Le sue responsabilità principali sono:

* consumare i messaggi dal topic Kafka dedicato alle notifiche (ad esempio `to-notifier`);
* trasformare il contenuto degli eventi in **e‑mail leggibili** (oggetto e corpo del messaggio) che riassumono la condizione di alert verificatasi;
* interagire con il server SMTP configurato (nel contesto corrente, Mailtrap) tramite il supporto **Spring Mail**;
* tracciare nei log l’esito delle notifiche, evidenziando eventuali errori di consegna.

Il servizio non espone API REST verso l’esterno, ma opera come componente di back-end guidato dagli eventi presenti nella coda Kafka.

#### 1.2.5 API Gateway

L’**API Gateway** è implementato tramite **NGINX** e funge da **punto di ingresso unico** per il traffico HTTP verso il sistema. A livello logico:

* riceve le richieste in ingresso su una porta pubblica esposta dal container NGINX;
* instrada le richieste verso i microservizi interni pertinenti (principalmente User Manager e Data Collector), sulla base di regole di *routing* definite nel file di configurazione;
* consente di centralizzare alcuni aspetti trasversali, quali la gestione degli *path* di base, l’eventuale logging HTTP e la separazione tra rete esterna e rete interna Docker.

Questa componente permette di presentare verso l’esterno un **perimetro uniforme**, schermando i dettagli interni di deploy e degli indirizzi dei singoli microservizi.

#### 1.2.6 Kafka Broker

Il **Kafka Broker** fornisce l’infrastruttura di **messaggistica asincrona** alla base della pipeline di alerting. Nel contesto attuale:

* viene eseguito come servizio Docker dedicato, affiancato dai componenti di coordinamento necessari (ad esempio Zookeeper);
* espone i topic utilizzati dai microservizi applicativi, con particolare riferimento ai flussi `to-alert-system` e `to-notifier`;
* consente a Data Collector, Alert System e Alert Notifier di scambiarsi eventi in maniera decoupled, supportando l’elaborazione asincrona e una migliore resilienza rispetto a picchi di carico o temporanee indisponibilità.

---

### 1.3 Componenti esterni

Il sistema si appoggia su alcune dipendenze esterne fondamentali, considerate come **servizi infrastrutturali** o **provider terzi** rispetto ai microservizi applicativi.

#### 1.3.1 OpenSky Network API

Le **OpenSky Network API** rappresentano la fonte dati esterna da cui il sistema ricava le informazioni sui voli. Il Data Collector utilizza un client dedicato per:

* effettuare l’autenticazione **OAuth2 Client Credentials** verso l’endpoint di autorizzazione esposto da OpenSky;
* ottenere e gestire un **access token** valido, rispettando i tempi di scadenza indicati nelle risposte del provider;
* invocare gli endpoint HTTP REST per estrarre i voli relativi a un determinato aeroporto in un certo intervallo temporale;
* gestire eventuali errori di rete o di servizio, delegando al Circuit Breaker la protezione complessiva verso failure ripetute.

OpenSky è quindi il *system of record* esterno per le informazioni di volo, mentre la piattaforma applicativa si occupa di persistere e arricchire tali dati in funzione delle esigenze di monitoraggio e alerting.

#### 1.3.2 Database PostgreSQL

Il **Database PostgreSQL** è eseguito come container dedicato e ospita due database logici distinti:

* **User DB (`userdb`)**, utilizzato dallo User Manager Service per la persistenza degli utenti;
* **Data DB (`datadb`)**, utilizzato dal Data Collector Service per aeroporti, interessi utente–aeroporto e flight records.

L’inizializzazione degli schemi è delegata a script SQL e a **Flyway**, che si occupano di creare tabelle, vincoli e indici necessari al corretto funzionamento della piattaforma. Dal punto di vista dei microservizi, PostgreSQL è visto come un componente esterno rispetto al quale vengono definite connessioni dedicate e politiche di migrazione controllate.

#### 1.3.3 Mailtrap (Email Testing SMTP)

**Mailtrap** è il servizio SMTP esterno utilizzato per l’**invio controllato delle e‑mail di notifica** in ambienti di test e sviluppo. Nel contesto della piattaforma:

* fornisce le credenziali SMTP (host, porta, username, password) configurate tramite variabili d’ambiente e lette dall’Alert Notifier Service;
* riceve tutte le e‑mail generate dal sistema, permettendo di verificarne contenuto e formato senza inviarle a caselle reali;
* consente di validare end‑to‑end la pipeline di alerting (dai dati di volo fino alla notifica via e‑mail) in modo sicuro.

---

### 1.4 Riferimenti alla documentazione di progetto

La repository include una documentazione strutturata che integra il presente README con una vista approfondita sull’architettura, sul modello dei dati e sui flussi applicativi del sistema. La documentazione è organizzata per versione del sistema, in modo da mantenere chiara la distinzione tra la base consolidata e le successive evoluzioni.

#### 1.4.1 Relazioni tecniche e documentazione architetturale

Nella cartella `documentation/` sono presenti le **relazioni tecniche** che descrivono il sistema nelle diverse versioni:

* `documentation/homework-1/written_report.pdf`: documenta l’architettura di base centrata sui microservizi **User Manager** e **Data Collector**, l’integrazione con **OpenSky Network** e **PostgreSQL**, i requisiti funzionali e non funzionali iniziali e i flussi core di raccolta e interrogazione dei dati di volo.
* `documentation/homework-2/written_report.pdf`: estende la descrizione precedente introducendo i microservizi **Alert System** e **Alert Notifier**, l’**API Gateway** basato su NGINX, la pipeline event‑driven su **Kafka**, i meccanismi di **Circuit Breaker** verso OpenSky e l’evoluzione del modello dati per la gestione delle soglie.

Le due relazioni sono pensate per essere lette in modo complementare: la prima fornisce il contesto e le fondamenta architetturali del sistema, la seconda introduce lo strato evolutivo, indicando in modo esplicito quali componenti e sezioni della documentazione precedente restano pienamente valide e quali risultano integrate o sostituite.

#### 1.4.2 Diagrammi architetturali e di sequenza

All’interno di `documentation/homework-1/diagram_screenshots/` e `documentation/homework-2/diagram_screenshots/` sono disponibili i **diagrammi architetturali** e i **diagrammi di sequenza** principali.

* Il set di diagrammi associato alla prima versione illustra l’architettura centrata su User Manager, Data Collector, PostgreSQL e OpenSky, con i flussi sincroni di registrazione utente, registrazione degli interessi e raccolta/interrogazione dei voli.
* Il set di diagrammi associato alla versione corrente rappresenta l’architettura estesa con Alert System, Alert Notifier, API Gateway, Kafka e server SMTP, oltre ai flussi aggiuntivi di configurazione e aggiornamento delle soglie, pipeline di notifica asincrona (Data Collector → Alert System → Alert Notifier → Mailtrap) e gestione delle failure verso OpenSky tramite Circuit Breaker.

I diagrammi sono organizzati per facilitare il confronto tra le due versioni e permettere di seguire, anche visivamente, l’evoluzione delle responsabilità tra microservizi e componenti infrastrutturali.

#### 1.4.3 Diagramma Entity–Relationship (ER)

In ciascuna delle sottocartelle di documentazione è presente un **diagramma Entity–Relationship (ER)** che rappresenta lo schema logico dei database.

* Il diagramma della prima versione mostra le entità `User` nel *User DB* e `Airport`, `UserAirportInterest` e `FlightRecord` nel *Data DB*, insieme a chiavi primarie, vincoli di unicità e relazioni fra i domini utente e aeroporti–voli.
* Il diagramma aggiornato della versione corrente evidenzia l’estensione di `UserAirportInterest` con gli attributi di soglia (`high_value`, `low_value`) e l’impatto di tali modifiche sulle query e sui processi di raccolta e valutazione dei dati.

Questi artefatti costituiscono il riferimento principale per collegare modello concettuale, modello logico e implementazione JPA/Flyway nei diversi microservizi.

#### 1.4.4 Evoluzione del sistema per release

La documentazione è organizzata in modo da riflettere esplicitamente il ciclo evolutivo del sistema:

* la **prima versione** descrive il perimetro funzionale di raccolta e interrogazione dei dati di volo, con due microservizi principali e un’integrazione sincrona verso OpenSky;
* la **versione corrente** introduce la gestione di soglie configurabili, la pipeline di alerting basata su Kafka, i servizi dedicati all’elaborazione degli alert e alla notifica e‑mail, nonché la mediazione centralizzata delle richieste tramite API Gateway.

Le relazioni tecniche, i diagrammi architetturali, i diagrammi di sequenza e i diagrammi ER sono pertanto da considerare come livelli successivi di una stessa documentazione: il materiale della prima versione fornisce la base concettuale e architetturale, mentre quello della versione corrente ne rappresenta l’estensione e il raffinamento, preservando la coerenza complessiva del sistema.

## 2. Repository Structure

### 2.1 Root layout della repository

Nella directory radice sono presenti le cartelle e i file necessari per gestire l’intero ciclo di vita della piattaforma, dalla build al deploy in ambiente Docker. A livello logico, la root contiene:

* le directory dei quattro microservizi applicativi (`user-manager-service/`, `data-collector-service/`, `alert-system-service/`, `alert-notifier-service/`), ciascuna con il proprio codice applicativo e la propria configurazione;
* la directory `api-gateway/`, che raccoglie la configurazione del reverse proxy NGINX utilizzato come punto di ingresso unico verso i microservizi interni;
* la directory `docker/`, che raccoglie i file di orchestrazione e configurazione dell’infrastruttura (PostgreSQL, Kafka, Zookeeper, Kafka UI, API Gateway e servizi applicativi);
* la directory `documentation/`, che contiene le relazioni tecniche e i diagrammi di supporto;
* la directory `postman/`, che include le collection pronte all’uso per verificare rapidamente le API esposte dai servizi applicativi;
* i file di supporto generali, come ad esempio il `README.md` (questo documento) e gli eventuali file di configurazione per il versionamento.

Questa impostazione consente a chiunque acceda per la prima volta al repository di individuare rapidamente i microservizi, l’infrastruttura containerizzata e la documentazione tecnica.

---

### 2.2 Struttura delle cartelle principali

La struttura delle cartelle è organizzata in modo da separare in maniera netta il codice applicativo, l’infrastruttura di esecuzione e gli artefatti di documentazione e test. Le principali directory logiche sono descritte nei paragrafi seguenti.

#### 2.2.1 Servizi applicativi (User Manager, Data Collector, Alert System, Alert Notifier)

* **`user-manager-service/`**
  Contiene il codice sorgente, le risorse di configurazione e gli artefatti di build del microservizio **User Manager**, responsabile della gestione degli utenti (registrazione, lettura, cancellazione) e dell’esposizione del servizio **gRPC** per la validazione dell’esistenza di un utente.

* **`data-collector-service/`**
  Contiene il codice sorgente, le risorse di configurazione e gli artefatti di build del microservizio **Data Collector**, responsabile della gestione degli aeroporti di interesse, del salvataggio dei voli recuperati da OpenSky e dell’esposizione delle API per la consultazione dei dati di volo. In questa release il servizio è stato esteso per pubblicare sul broker Kafka gli eventi di superamento soglia destinati all’Alert System.

* **`alert-system-service/`**
  Contiene il codice sorgente, le risorse di configurazione e gli artefatti di build del microservizio **Alert System**, che sottoscrive il topic Kafka dedicato agli eventi di superamento soglia, valuta le condizioni di alert per ciascun interesse utente–aeroporto e produce i messaggi strutturati destinati al servizio di notifica.

* **`alert-notifier-service/`**
  Contiene il codice sorgente, le risorse di configurazione e gli artefatti di build del microservizio **Alert Notifier**, che consuma i messaggi dal topic Kafka dedicato e si occupa dell’invio delle email di notifica verso i destinatari finali, integrandosi con il servizio SMTP configurato (Mailtrap in ambiente di test).

Ciascun microservizio è organizzato come progetto **Spring Boot** basato su **Maven**, con una struttura omogenea che facilita la manutenzione e la configurazione coerente dell’intero sistema.

#### 2.2.2 Infrastruttura Docker (PostgreSQL, Kafka, Zookeeper, Kafka UI, API Gateway)

* **`docker/`**
  Raccoglie tutti i file necessari per l’orchestrazione dell’infrastruttura containerizzata. In particolare, al suo interno sono presenti:

  * `docker-compose.yml`, che definisce i servizi Docker per **PostgreSQL**, i quattro microservizi applicativi, il **Kafka broker**, **Zookeeper**, la **Kafka UI** e l’**API Gateway** basato su NGINX;
  * la directory `db/`, che contiene gli script SQL di inizializzazione del database (`db/init/`);
  * la directory `env/`, che raccoglie i file `.env` utilizzati per parametrizzare le credenziali del database e le variabili condivise tra i servizi;
  * la directory `nginx/`, che contiene il file di configurazione dell’API Gateway (`nginx.conf`).

Questa directory rappresenta il punto centrale di configurazione per l’esecuzione dell’intero sistema in ambiente containerizzato.

#### 2.2.3 Documentazione e diagrammi

* **`documentation/`**
  Contiene la documentazione tecnica del sistema e i diagrammi architetturali e di dettaglio, organizzati per versione della piattaforma.

  All’interno della directory sono presenti, in particolare:

  * **`documentation/homework-1/`**
    Raccoglie gli artefatti relativi alla prima versione del sistema di flight monitoring:

    * `documentation/homework-1/written_report.pdf`, che costituisce la relazione tecnica completa della versione di base, con la descrizione dei microservizi **User Manager** e **Data Collector**, del modello dati iniziale (User DB e Data DB), dei flussi sincroni di raccolta e interrogazione dei voli e dell’integrazione con **OpenSky Network**;
    * `documentation/homework-1/diagram_screenshots/`, che contiene gli screenshot dei principali diagrammi: diagramma architetturale complessivo, diagramma ER dei database, diagrammi di sequenza per registrazione utente, registrazione interessi via gRPC e raccolta/interrogazione dei voli.

  * **`documentation/homework-2/`**
    Raccoglie gli artefatti relativi alla versione estesa del sistema, che introduce nuove funzionalità di alerting e una pipeline event‑driven:

    * `documentation/homework-2/written_report.pdf`, che estende la relazione tecnica precedente con la descrizione dei microservizi **Alert System** e **Alert Notifier**, dell’**API Gateway** basato su NGINX, del broker **Kafka**, dei meccanismi di **Circuit Breaker** verso OpenSky e dell’evoluzione del modello dati per la gestione delle soglie di interesse;
    * `documentation/homework-2/diagram_screenshots/`, che contiene i diagrammi aggiornati: diagramma architetturale della versione estesa, diagramma ER con gli attributi di soglia, diagrammi di sequenza relativi alla configurazione e aggiornamento delle soglie, alla pipeline di notifica asincrona (Data Collector → Alert System → Alert Notifier → Email) e alla gestione delle failure verso OpenSky.

La struttura versionata della directory `documentation/` consente di mantenere separati, ma facilmente confrontabili, gli artefatti relativi alla versione di base e quelli relativi alla versione corrente del sistema.

#### 2.2.4 Postman collections

* **`postman/`**
  Contiene le **Postman collections** utilizzate per esercitare e validare le API esposte dai microservizi applicativi. Le collection sono organizzate per versione, in modo da riflettere l’evoluzione del sistema.

  In particolare sono presenti:

  * **`postman/homework-1/`**
    Include le collection utilizzate per testare la versione di base del sistema:

    * `postman/homework-1/hw1-user-manager-api.postman_collection.json`, che raggruppa le richieste preconfigurate relative allo *User Manager Service* (registrazione utente, consultazione, cancellazione, test della semantica *at-most-once*);
    * `postman/homework-1/hw1-data-collector-api.postman_collection.json`, che raccoglie le richieste preconfigurate verso il *Data Collector Service* (gestione aeroporti di interesse, registrazione interessi utente–aeroporto, interrogazioni dei voli su intervalli temporali e altre operazioni core sulla base dati dei voli).

  * **`postman/homework-2/`**
    Include le collection aggiornate per la versione corrente del sistema:

    * `postman/homework-2/hw2 - user-manager-api.postman_collection.json`, che mantiene e organizza le richieste relative allo *User Manager Service* (creazione, lettura, cancellazione utente e verifica dell’idempotenza), utilizzabili in continuità con la versione precedente;
    * `postman/homework-2/hw2 - data-collector-api.postman_collection.json`, che estende il set di richieste verso il *Data Collector Service* includendo, oltre alle operazioni già presenti nella versione di base, gli endpoint per la configurazione delle **soglie** (`highValue`/`lowValue`) sugli interessi e le interrogazioni funzionali a pilotare gli scenari di valutazione e di notifica.

Le collection possono essere importate direttamente in Postman per eseguire in modo controllato le richieste preconfigurate verso i microservizi, utilizzando gli endpoint esposti (direttamente o tramite API Gateway) e i parametri di configurazione descritti nel presente README.

---

### 2.3 File chiave per build & deploy

Alcuni file del repository rivestono un ruolo centrale nelle procedure di build e deploy e meritano una menzione esplicita.

* **`docker/docker-compose.yml`**
  Definisce l’orchestrazione completa dell’ambiente di esecuzione containerizzato. In questo file sono specificati:

  * i servizi Docker (`postgres`, `user-manager-service`, `data-collector-service`, `alert-system-service`, `alert-notifier-service`, `kafka`, `zookeeper`, `kafka-ui`, `api-gateway`);
  * le immagini da utilizzare o generare, i *build context* e i `Dockerfile` associati;
  * i volumi per la persistenza dei dati PostgreSQL e per eventuali mount di configurazione;
  * le reti interne utilizzate per la comunicazione tra i container;
  * le dipendenze di avvio tra i servizi, in modo che il database e il broker Kafka siano disponibili prima dei microservizi applicativi.

* **`docker/env/postgres.env`**
  Contiene le variabili d’ambiente utilizzate per configurare il servizio PostgreSQL (nome utente, password, nomi dei database logici), centralizzando la definizione dei parametri critici del database ed evitando la duplicazione di configurazioni nei vari servizi.

* **`docker/env/services.env`**
  Raccoglie le variabili d’ambiente comuni ai microservizi, tra cui le informazioni di connessione al database, le credenziali per l’accesso a OpenSky, i parametri di configurazione del broker Kafka e le impostazioni per l’invio delle email. In questo modo è possibile gestire in un unico punto i valori che devono essere condivisi tra più container, rendendo agevole l’adattamento del sistema a diversi ambienti.

* **`api-gateway/nginx/nginx.conf`**
  Descrive la configurazione del reverse proxy NGINX utilizzato come API Gateway. In questo file sono definiti i *virtual server*, le regole di routing delle richieste verso i vari microservizi interni, le porte esposte e le eventuali intestazioni aggiuntive necessarie per il corretto inoltro delle chiamate.

* **`user-manager-service/Dockerfile`**, **`data-collector-service/Dockerfile`**, **`alert-system-service/Dockerfile`** e **`alert-notifier-service/Dockerfile`**
  Descrivono il processo di build delle immagini Docker per i quattro microservizi applicativi. Ogni Dockerfile è strutturato come *multi-stage build* per separare la fase di compilazione Maven dalla fase di runtime, producendo un’immagine finale più leggera basata su una immagine JDK/JRE minimal.

* **`user-manager-service/pom.xml`**, **`data-collector-service/pom.xml`**, **`alert-system-service/pom.xml`** e **`alert-notifier-service/pom.xml`**
  Definiscono le dipendenze, i plugin e le configurazioni necessarie alla build Maven dei singoli microservizi. Questi file sono il riferimento principale per la build locale senza Docker e per integrare il progetto in pipeline CI/CD.

* **Script SQL di inizializzazione in `docker/db/init/`**
  Comprendono gli script per la creazione dei database logici e degli schemi necessari al corretto popolamento delle tabelle applicative. L’esecuzione automatica di questi script garantisce che l’ambiente dati sia sempre predisposto prima dell’esecuzione delle migrazioni applicative e dell’avvio dei microservizi.

Nel complesso, questi file costituiscono il nucleo operativo necessario per costruire ed eseguire l’intero sistema in modo riproducibile e controllato in diversi contesti di esecuzione.

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

Il sistema integra i dati di volo tramite le **OpenSky Network API**, che richiedono un’apposita registrazione e l’ottenimento di credenziali, e invia notifiche email tramite un provider SMTP di test (**Mailtrap**), anch’esso basato su credenziali dedicate.

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

#### 3.3.3 Configurazione di un account Mailtrap (SMTP testing)

Per testare l’invio delle notifiche email senza utilizzare destinatari reali, il sistema assume la presenza di un account presso un servizio di **SMTP testing**, come *Mailtrap*. La configurazione tipica prevede i seguenti passi:

1. Creare un account su Mailtrap e accedere all’area di gestione delle **Inbox** dedicate al testing SMTP.
2. Creare (o riutilizzare) una inbox dedicata alle notifiche generate dalla piattaforma.
3. Recuperare, dalla sezione di configurazione SMTP della inbox, i parametri necessari all’invio delle email, in particolare:

   * *SMTP host*;
   * *SMTP port*;
   * *username* e *password* SMTP generati dal provider;
   * eventuali flag di autenticazione (es. `auth = true`) e di sicurezza (es. `STARTTLS` abilitato).
4. Mappare tali valori sulle variabili d’ambiente previste dal sistema (ad esempio tramite il file `docker/env/services.env`), in modo che il microservizio **Alert Notifier** possa stabilire una connessione autenticata al server SMTP al momento dell’invio di ogni notifica.

È buona pratica utilizzare una inbox dedicata esclusivamente a questo sistema, così da poter monitorare facilmente il flusso di email generate dalle condizioni di alert e distinguere tali messaggi da eventuali altri progetti che utilizzano lo stesso account Mailtrap.

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

È opportuno infine validare che le variabili d’ambiente relative a OpenSky (client id, client secret, endpoint di authorization e API base URL) e al provider SMTP utilizzato per il testing (host, porta, credenziali e parametri di sicurezza della inbox Mailtrap) siano configurate correttamente nel contesto in cui verrà eseguito `docker compose` o i microservizi in locale.

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
* **`docker/env/services.env`**: fornisce ai microservizi i parametri comuni di connessione al database, i riferimenti alle OpenSky Network API, i parametri di connessione al broker Kafka e le impostazioni per l’integrazione con il sistema di posta SMTP utilizzato dall’Alert Notifier.

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

KAFKA_BOOTSTRAP_SERVERS=kafka:9092

MAIL_HOST=smtp.mailtrap.io
MAIL_PORT=2525
MAIL_USERNAME=your_mailtrap_username
MAIL_PASSWORD=your_mailtrap_password
MAIL_SMTP_AUTH=true
MAIL_SMTP_STARTTLS_ENABLE=true
```

Queste variabili permettono ai microservizi di:

* connettersi al database PostgreSQL tramite host e porta logici (`DB_HOST`, `DB_PORT`), riutilizzando gli stessi nomi per tutti i servizi che accedono all’istanza;
* accedere ai database corretti (`USER_DB_NAME`, `DATA_DB_NAME`) pur condividendo la stessa istanza PostgreSQL;
* ottenere le credenziali e gli endpoint necessari per interagire con le OpenSky Network API;
* disporre dei parametri di integrazione con il broker Kafka (`KAFKA_BOOTSTRAP_SERVERS`) e con il sistema di posta SMTP (`MAIL_*`) utilizzato per l’invio delle notifiche.

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

---

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

### 4.5 Configurazione del sistema di posta (Mailtrap SMTP)

L’invio delle notifiche email è gestito dal microservizio **Alert Notifier**, che si integra con un server SMTP esterno. In ambiente di sviluppo e test viene utilizzato **Mailtrap**, che fornisce una *sandbox* SMTP dedicata alla verifica delle email senza recapito verso destinatari reali. Anche in questo caso la configurazione è interamente **env-based**: le credenziali e gli endpoint del server SMTP non sono codificati nel codice sorgente, ma vengono forniti tramite variabili d’ambiente e file `.env`.

#### 4.5.1 Variabili d’ambiente `MAIL_*` per l’Alert Notifier

Il microservizio *Alert Notifier* legge i parametri necessari alla configurazione del `JavaMailSender` da un insieme di variabili d’ambiente con prefisso `MAIL_`, tipicamente definite in `docker/env/services.env` e iniettate nel container tramite Docker Compose. Le variabili principali sono:

* `MAIL_HOST`: hostname del server SMTP (ad esempio `smtp.mailtrap.io`);
* `MAIL_PORT`: porta di ascolto del server SMTP (tipicamente `2525` per Mailtrap);
* `MAIL_USERNAME`: username dell’account SMTP configurato su Mailtrap;
* `MAIL_PASSWORD`: password associata all’account SMTP;
* `MAIL_SMTP_AUTH`: flag booleano che abilita l’autenticazione SMTP (`true`/`false`);
* `MAIL_SMTP_STARTTLS_ENABLE`: flag booleano che abilita l’estensione **STARTTLS** per la cifratura del canale.

Queste variabili vengono lette dal componente di configurazione del microservizio (ad esempio una classe `MailConfig` annotata con `@Configuration`), che costruisce il `JavaMailSender` impostando host, porta, credenziali e proprietà del protocollo (`mail.transport.protocol`, `mail.smtp.auth`, `mail.smtp.starttls.enable`). In questo modo l’Alert Notifier può inviare email senza che i dettagli dell’account SMTP siano presenti nel codice.

L’indirizzo email del mittente e il nome visualizzato (*display name*) sono configurati tramite proprietà applicative del microservizio (ad esempio nel file `application-docker.yml`), eventualmente mappate a loro volta su variabili d’ambiente dedicate, in modo da poter personalizzare facilmente il mittente delle notifiche.

#### 4.5.2 Considerazioni su mittente, autenticazione e TLS

Per garantire un comportamento coerente e sicuro del sistema di notifica è opportuno:

* utilizzare un **mittente dedicato** alle notifiche applicative (ad esempio `no-reply@alerts.example.com`), evitando account personali;
* mantenere **attiva l’autenticazione SMTP**, impostando `MAIL_SMTP_AUTH=true` e conservando le credenziali in file `.env` non versionati o in secret manager esterni;
* abilitare **STARTTLS** quando supportato dal provider (`MAIL_SMTP_STARTTLS_ENABLE=true`), così da proteggere le credenziali e il contenuto delle notifiche durante il transito;
* utilizzare password robuste o, ove disponibili, **token di accesso** specifici per l’SMTP, evitando il riuso di credenziali generiche.

Nel contesto di test con Mailtrap, questi parametri consentono di simulare fedelmente le condizioni di produzione, mantenendo al contempo il recapito confinato alla *sandbox* del provider.

---

### 4.6 Profili e configurazioni Spring Boot

La configurazione dei microservizi è organizzata tramite i file di property Spring Boot (`application.yml` e, ove previsto, `application-docker.yml`), integrati con variabili d’ambiente fornite dall’infrastruttura (Docker e file `.env`). In assenza di profili espliciti, Spring Boot utilizza il **profilo di default**, che nel progetto è pensato per coprire i principali scenari di esecuzione, demandando ai parametri env-based la specializzazione per i singoli ambienti.

#### 4.6.1 Uso del profilo di default e overriding tramite variabili d’ambiente

Il profilo di default di Spring Boot è sufficiente per la maggior parte degli scenari supportati dal sistema. In particolare:

* i parametri relativi al **datasource** (URL JDBC, utente, password) sono definiti nei file di configurazione applicativa (`application.yml` per l’esecuzione locale, `application-docker.yml` per l’esecuzione in container) in forma generica, assumendo `localhost` e la porta standard `5432` come configurazione di base per l’esecuzione locale e delegando a variabili d’ambiente la risoluzione degli host/logical name in ambiente Docker;
* i parametri relativi ai **servizi esterni** (endpoint OpenSky, credenziali OAuth2, configurazione gRPC, integrazione con Kafka e sistema di posta) sono referenziati tramite placeholder e risolti al runtime utilizzando le variabili d’ambiente (`OPEN_SKY_AUTH_BASE_URL`, `OPEN_SKY_API_BASE_URL`, `OPEN_SKY_CLIENT_ID`, `OPEN_SKY_CLIENT_SECRET`, `KAFKA_BOOTSTRAP_SERVERS`, `MAIL_*`, ecc.).

In questo modello, i file di configurazione Spring fungono da **singola sorgente di verità** per la struttura della configurazione applicativa, mentre gli aspetti ambiente‑specifici (host, credenziali, URL esterni, porte) sono demandati alle variabili d’ambiente illustrate nelle sezioni precedenti. L’override dei parametri può avvenire sia tramite file `.env` caricati da Docker Compose, sia tramite variabili d’ambiente impostate direttamente nel sistema operativo o nel motore di orchestrazione.

Qualora in futuro si rendesse necessario introdurre una differenziazione più marcata tra ambienti (ad esempio **sviluppo**, **test**, **produzione**), è possibile estendere il modello attuale definendo profili Spring Boot dedicati, ad esempio:

* `application-dev.yml` per configurazioni specifiche di sviluppo (logging più verboso, feature flag, parametri di scheduler meno aggressivi);
* `application-prod.yml` per parametri più conservativi, time‑out più stringenti, livelli di log più restrittivi.

L’attivazione di tali profili può avvenire tramite:

* variabile d’ambiente `SPRING_PROFILES_ACTIVE` (ad esempio `SPRING_PROFILES_ACTIVE=prod`);
* oppure parametro da riga di comando, ad esempio:

  ```bash
  java -jar data-collector-service.jar --spring.profiles.active=prod
  ```

Nel contesto attuale, il profilo di default combinato con la configurazione env‑based descritta nelle sezioni precedenti è sufficiente e rappresenta la modalità consigliata per build & deploy del sistema.

#### 4.6.2 Configurazioni per l’esecuzione in ambiente Docker (`application-docker.yml`)

Per i microservizi eseguiti in ambiente containerizzato, la configurazione specifica è concentrata nei file **`application-docker.yml`**, collocati in `src/main/resources/` all’interno di ciascun servizio. Questi file definiscono, tra le altre cose:

* la configurazione del **datasource** basata sui logical name utilizzati nella rete Docker (`DB_HOST=postgres`, `DB_PORT=5432`, `USER_DB_NAME`, `DATA_DB_NAME`);
* i parametri di integrazione con il **broker Kafka** (bootstrap servers, gruppi di consumo, configurazione dei topic);
* gli endpoint e le credenziali dei **servizi esterni** (OpenSky, sistema di posta), mappati sui placeholder che risolvono le variabili d’ambiente;
* eventuali parametri specifici dell’ambiente containerizzato (time‑out, pool di connessioni, configurazioni di logging).

Nel caso dei microservizi che possono essere eseguiti anche localmente (*User Manager* e *Data Collector*), `application.yml` fornisce una configurazione adatta alla connessione verso un database PostgreSQL esposto su `localhost:5432`, mentre `application-docker.yml` contiene la variante pensata per l’esecuzione all’interno della rete Docker, con riferimenti all’host logico `postgres` e alle altre risorse containerizzate. Il passaggio da una configurazione all’altra avviene tramite l’ambiente di esecuzione (set di variabili d’ambiente e, se previsto, profili Spring attivati).

Per i microservizi introdotti in questa release (*Alert System* e *Alert Notifier*), l’esecuzione è pensata primariamente in ambiente Docker e la configurazione principale è concentrata direttamente in `application-docker.yml`, che assume la presenza delle variabili d’ambiente settate tramite i file `env` e il `docker-compose.yml`.

#### 4.6.3 Configurazione del Circuit Breaker Resilience4j verso OpenSky

Per aumentare la **resilienza** delle chiamate alle OpenSky Network API, il `Data Collector Service` utilizza un **Circuit Breaker** basato su *Resilience4j*. La configurazione è definita nel file di proprietà applicativo (tipicamente `application.yml` e/o `application-docker.yml`), tramite una sezione dedicata, ad esempio:

```yaml
resilience4j:
  circuitbreaker:
    instances:
      openSkyClient:
        sliding-window-size: 10
        minimum-number-of-calls: 5
        failure-rate-threshold: 50
        wait-duration-in-open-state: 30s
        permitted-number-of-calls-in-half-open-state: 5
        automatic-transition-from-open-to-half-open-enabled: true
```

Il client che invoca le OpenSky Network API viene decorato con il `CircuitBreaker` identificato dal nome `openSkyClient`. I parametri configurati definiscono:

* la dimensione della **finestra di osservazione** (`sliding-window-size`) e il numero minimo di chiamate su cui calcolare le statistiche (`minimum-number-of-calls`);
* la **soglia di failure** oltre la quale il circuito passa allo stato *open* (`failure-rate-threshold`);
* la durata di permanenza nello stato *open* prima del tentativo di ritorno allo stato *half-open* (`wait-duration-in-open-state`);
* il numero di chiamate consentite nello stato *half-open* (`permitted-number-of-calls-in-half-open-state`) e l’abilitazione della transizione automatica da *open* a *half-open*.

In presenza di errori ripetuti o di indisponibilità temporanea del servizio OpenSky, il Circuit Breaker evita di saturare l’endpoint esterno con richieste fallimentari, proteggendo il microservizio chiamante e contribuendo alla stabilità complessiva del sistema.

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

Per costruire le immagini Docker dei microservizi applicativi a partire dai rispettivi `Dockerfile` è possibile utilizzare **Docker Compose** in uno dei seguenti modi.

Build esplicita delle immagini, senza avviare i container:

```bash
docker compose build
```

Questo comando:

* analizza il file `docker-compose.yml`;
* esegue la build delle immagini per i servizi che la richiedono (i quattro microservizi applicativi: `user-manager-service`, `data-collector-service`, `alert-system-service`, `alert-notifier-service`);
* effettua il pull delle immagini infrastrutturali (ad esempio **PostgreSQL**, **Kafka**, **Zookeeper**, **Kafka UI** e **API Gateway** basato su NGINX), se non già presenti in locale.

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

Ciascun microservizio applicativo dispone di un proprio **`Dockerfile`** alla radice della rispettiva cartella (`user-manager-service/Dockerfile`, `data-collector-service/Dockerfile`, `alert-system-service/Dockerfile`, `alert-notifier-service/Dockerfile`). Tutti adottano una strategia di **multi-stage build** con l’obiettivo di:

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

In `docker-compose.yml`, ogni servizio applicativo referenzia il proprio `Dockerfile` indicando il contesto di build e il valore di `Dockerfile`, ad esempio:

```yaml
services:
  user-manager-service:
    build:
      context: ./user-manager-service
      dockerfile: Dockerfile
    # .

  data-collector-service:
    build:
      context: ./data-collector-service
      dockerfile: Dockerfile
    # .

  alert-system-service:
    build:
      context: ./alert-system-service
      dockerfile: Dockerfile
    # .

  alert-notifier-service:
    build:
      context: ./alert-notifier-service
      dockerfile: Dockerfile
    # .
```

Gli altri servizi definiti nello stack (database, broker Kafka, Zookeeper, Kafka UI, API Gateway) utilizzano immagini già pronte reperite da registry pubblici e non richiedono un `Dockerfile` specifico all’interno del repository.

Questa configurazione permette di mantenere i microservizi indipendenti, garantendo al contempo una pipeline di build coerente e ripetibile.

---

### 5.2 Build locale senza Docker (opzionale)

È possibile costruire i microservizi anche in modalità **non containerizzata**, utilizzando direttamente Maven. Questa modalità è utile durante lo sviluppo, per l’esecuzione da IDE o per scenari di debug approfondito, mantenendo comunque una pipeline di build allineata a quella utilizzata negli image Docker. In pratica, l’esecuzione locale è pensata soprattutto per i servizi **User Manager** e **Data Collector**; per **Alert System** e **Alert Notifier** la build locale produce comunque gli artefatti jar necessari, ma l’esecuzione runtime viene normalmente effettuata in container, poiché dipende dalla disponibilità di un broker **Kafka** e di un server **SMTP** (ad esempio Mailtrap) correttamente configurati.

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

#### 5.2.3 Build dell’Alert System Service con Maven

Per costruire l’artefatto del **Alert System Service** in modalità non containerizzata è possibile eseguire:

```bash
cd alert-system-service
mvn clean package
```

Il comando esegue una sequenza di operazioni analoga a quella descritta per gli altri microservizi:

* rimuove eventuali artefatti di build precedenti;
* risolve le dipendenze dichiarate nel `pom.xml` (inclusi i moduli per l’integrazione con Kafka e il circuito di comunicazione con il broker);
* compila il codice sorgente ed esegue gli eventuali test configurati;
* genera un jar eseguibile in `target/`, ad esempio:

  ```text
  target/alert-system-service-<version>.jar
  ```

Per questo microservizio la configurazione principale è definita nel file `application-docker.yml`, pensato per l’esecuzione in ambiente containerizzato, dove le variabili d’ambiente vengono fornite da Docker Compose. Il jar prodotto viene tipicamente utilizzato come artefatto di riferimento negli **stage runtime** dei Dockerfile multi-stage. Un’eventuale esecuzione stand‑alone richiede la predisposizione manuale di un ambiente con **PostgreSQL** e **Kafka** raggiungibili e una configurazione esplicita delle proprietà Spring Boot (ad esempio tramite variabili d’ambiente o file di configurazione esterni).

#### 5.2.4 Build dell’Alert Notifier Service con Maven

La procedura di build per il **Alert Notifier Service** segue gli stessi passi:

```bash
cd alert-notifier-service
mvn clean package
```

Maven:

* pulisce gli artefatti di build precedenti (`clean`);
* risolve le dipendenze dichiarate nel `pom.xml` (inclusi i moduli di integrazione con Kafka e con il sistema di posta);
* compila il codice sorgente ed esegue gli eventuali test;
* produce un jar eseguibile in `target/`, ad esempio:

  ```text
  target/alert-notifier-service-<version>.jar
  ```

Anche per questo microservizio la configurazione runtime è principalmente descritta in `application-docker.yml` e si appoggia alle variabili d’ambiente `MAIL_*` e ai parametri di connessione a Kafka forniti in fase di esecuzione containerizzata. Il jar generato viene utilizzato come base per l’immagine Docker costruita tramite multi-stage build; l’esecuzione diretta via `java -jar` è possibile ma presuppone la disponibilità di un server SMTP configurato (ad esempio **Mailtrap**) e di un broker **Kafka** raggiungibile, oltre alla corretta impostazione delle variabili d’ambiente.

#### 5.2.5 Differenze rispetto alla modalità Docker-based

La build locale tramite Maven consente di ottenere rapidamente gli artefatti jar dei singoli microservizi ed è particolarmente adatta per attività di sviluppo e debug puntuale, soprattutto per **User Manager** e **Data Collector**, che possono essere eseguiti anche al di fuori dell’ambiente containerizzato, a patto di predisporre un’istanza PostgreSQL accessibile.

La modalità **Docker-based** estende questo modello includendo nel ciclo di build anche l’ambiente infrastrutturale: il database PostgreSQL, il broker **Kafka**, **Zookeeper**, la **Kafka UI** e l’**API Gateway** NGINX vengono orchestrati insieme ai microservizi applicativi, con le rispettive variabili d’ambiente e le dipendenze di rete gestite in modo centralizzato da Docker Compose. In questo scenario, le immagini costruite a partire dai Dockerfile multi-stage incapsulano sia gli artefatti jar sia le configurazioni necessarie per l’esecuzione, riducendo il rischio di discrepanze tra ambienti diversi e semplificando la riproduzione dello stesso setup su macchine differenti.

Dal punto di vista operativo, la build locale offre maggiore flessibilità in fase di sviluppo, mentre la build ed esecuzione Docker-based rappresenta la modalità di riferimento per l’esecuzione completa del sistema di flight monitoring e per la validazione end-to-end del flusso di raccolta dati, valutazione delle soglie e invio delle notifiche di alert.

## 6. Deploy & Run with Docker Compose

### 6.1 Prima esecuzione del sistema

La prima esecuzione in modalità completamente containerizzata richiede la corretta preparazione dei file di configurazione **env-based** e l’avvio coordinato di database, componenti infrastrutturali e microservizi applicativi.

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
   * `services.env` per la configurazione dei microservizi e dei componenti infrastrutturali.

3. Aprire `env/postgres.env` e impostare, se necessario, i valori desiderati per:

   * `POSTGRES_USER` e `POSTGRES_PASSWORD`;
   * `POSTGRES_DB` (database di bootstrap);
   * `USER_DB_NAME` e `DATA_DB_NAME` (database logici dei due domini).

4. Aprire `env/services.env` e impostare:

   * i parametri di connessione al database (`DB_HOST`, `DB_PORT`, `DB_USERNAME`, `DB_PASSWORD`, `USER_DB_NAME`, `DATA_DB_NAME`);
   * gli endpoint e le credenziali per l’integrazione con OpenSky (`OPEN_SKY_AUTH_BASE_URL`, `OPEN_SKY_API_BASE_URL`, `OPEN_SKY_CLIENT_ID`, `OPEN_SKY_CLIENT_SECRET`, `OPEN_SKY_SCOPE`);
   * i parametri di configurazione del cluster Kafka utilizzato per la propagazione degli eventi (`KAFKA_BOOTSTRAP_SERVERS`, `KAFKA_SECURITY_PROTOCOL`, `KAFKA_SASL_MECHANISM`, `KAFKA_SASL_JAAS_CONFIG`, `KAFKA_TOPIC_TO_ALERT_SYSTEM`, `KAFKA_TOPIC_TO_NOTIFIER`);
   * i parametri per il server SMTP su cui si appoggia l’*Alert Notifier* (`MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_SMTP_AUTH`, `MAIL_SMTP_STARTTLS_ENABLE`, `MAIL_FROM`, `MAIL_FROM_NAME`);
   * l’eventuale profilo Spring attivo (`SPRING_PROFILES_ACTIVE`), tipicamente impostato a `docker` per l’esecuzione in container.

È consigliabile utilizzare valori di default significativi per l’ambiente di sviluppo e sostituire solo i placeholder sensibili (ad esempio le credenziali di accesso, le chiavi OpenSky, le credenziali Kafka e SMTP) prima del primo avvio.

#### 6.1.2 Avvio dei servizi (`docker compose up -d` / `docker compose up --build`)

Una volta preparati i file `.env`, è possibile avviare l’intero sistema tramite **Docker Compose**.

Per eseguire una prima build delle immagini e avviare i container in foreground, con log aggregati:

```bash
docker compose up --build
```

Questo comando:

* costruisce o aggiorna le immagini dei microservizi sulla base dei rispettivi `Dockerfile`;
* scarica le immagini dei servizi infrastrutturali (PostgreSQL, Kafka, ZooKeeper, Kafka UI, NGINX API Gateway) se non sono già presenti in locale;
* avvia i servizi definiti nel `docker-compose.yml` (database, broker di messaggistica, interfacce di supporto e microservizi applicativi).

Per avviare il sistema in **background**, lasciando i container attivi ma senza mantenere il terminale bloccato, è possibile usare:

```bash
docker compose up --build -d
```

Dopo la prima esecuzione, se non sono intervenute modifiche al codice o ai `Dockerfile`, è sufficiente:

```bash
docker compose up -d
```

per riavviare i servizi utilizzando le immagini già costruite.

Durante il primo avvio è normale che il servizio PostgreSQL impieghi alcuni secondi per completare l’inizializzazione, compresa l’esecuzione degli script SQL in `db/init/` e la preparazione dei database logici. I microservizi e i componenti di integrazione (Kafka, Alert System, Alert Notifier) si connetteranno progressivamente alle rispettive dipendenze una volta che queste risulteranno operative.

#### 6.1.3 Verifica che i container siano in esecuzione

Dopo l’avvio, è opportuno verificare che tutti i servizi previsti siano effettivamente in esecuzione.

Per elencare i container attivi associati allo stack:

```bash
docker compose ps
```

L’output deve riportare, con stato `running` o `healthy`, almeno:

* il container dell’istanza PostgreSQL (ad esempio `postgres` o nome equivalente configurato nel `docker-compose.yml`);
* i container dei microservizi applicativi: *User Manager Service* (`user-manager-service`), *Data Collector Service* (`data-collector-service`), *Alert System* (`alert-system-service`), *Alert Notifier* (`alert-notifier-service`);
* i container dei componenti infrastrutturali di messaggistica: `zookeeper`, `kafka`, `kafka-ui`;
* il container dell’*API Gateway* (`api-gateway`) basato su NGINX.

Per esaminare rapidamente i log complessivi:

```bash
docker compose logs
```

o, per seguire i log in tempo reale:

```bash
docker compose logs -f
```

È utile verificare nei log dei microservizi e dei servizi infrastrutturali che:

* la connessione al database venga stabilita correttamente;
* le migrazioni Flyway siano applicate senza errori;
* i client Kafka risultino connessi e sottoscritti ai topic configurati (`to-alert-system`, `to-notifier`);
* il client SMTP dell’*Alert Notifier* riesca a collegarsi al server configurato senza errori di autenticazione o di trasporto;
* gli endpoint REST e gRPC risultino esposti sulle porte attese (come configurate nel `docker-compose.yml` e nei file di configurazione Spring).

---

### 6.2 Arresto del sistema

La terminazione controllata dello stack containerizzato consente di liberare le risorse dell’host mantenendo, se desiderato, la persistenza dei dati applicativi.

#### 6.2.1 Comando di stop (`docker compose down`)

Per arrestare tutti i servizi e rilasciare le risorse allocate è possibile utilizzare, dalla cartella `docker/`:

```bash
docker compose down
```

Il comando interrompe i container associati allo stack e rimuove le relative definizioni di rete, mantenendo però intatti i volumi di persistenza (ad esempio quelli associati a PostgreSQL), così che i dati restino disponibili ai successivi riavvii.

#### 6.2.2 Rimozione volumi/persistenza (se necessario)

Qualora si desideri **ripartire da zero**, eliminando i dati persistenti (ad esempio per effettuare test di bootstrap o verificare gli script di inizializzazione), è possibile utilizzare:

```bash
docker compose down -v
```

L’opzione `-v` forza la rimozione dei volumi associati ai servizi definiti nel `docker-compose.yml` (tipicamente i volumi di PostgreSQL). Dopo questo comando, un successivo `docker compose up --build` ricreerà database e schemi a partire dagli script di inizializzazione.

---

### 6.3 Comandi Docker utili

Oltre alle operazioni standard di avvio e arresto dello stack, alcuni comandi Docker risultano particolarmente utili per attività di diagnosi e manutenzione puntuale dei singoli servizi.

#### 6.3.1 Visualizzazione log di un singolo servizio

Per concentrare l’analisi su un singolo container è possibile utilizzare `docker compose logs` specificando il nome del servizio. Ad esempio, per il *User Manager Service*:

```bash
docker compose logs user-manager-service
```

Per il *Data Collector Service*:

```bash
docker compose logs data-collector-service
```

Per l’*Alert System*:

```bash
docker compose logs alert-system-service
```

Per l’*Alert Notifier*:

```bash
docker compose logs alert-notifier-service
```

È possibile aggiungere l’opzione `-f` per seguire i log in streaming:

```bash
docker compose logs -f alert-notifier-service
```

In questo modo è possibile osservare in tempo reale la produzione degli eventi Kafka e l’invio delle notifiche email.

#### 6.3.2 Accesso alla shell di un container

Per effettuare verifiche di dettaglio all’interno di un container (ad esempio sul database o sugli strumenti di sistema disponibili nell’immagine) si può utilizzare `docker compose exec`. Per PostgreSQL:

```bash
docker compose exec postgres bash
```

All’interno della shell del container PostgreSQL è ad esempio possibile utilizzare il client `psql` per ispezionare i database, le tabelle o gli schemi creati dalle migrazioni applicative.

In modo analogo, si può accedere alla shell dei container dei microservizi (se l’immagine lo consente) o dei servizi infrastrutturali per effettuare verifiche aggiuntive, come il controllo della connettività verso Kafka o verso il server SMTP.

#### 6.3.3 Verifica delle porte esposte

Per verificare le porte esposte dai container verso l’host si può utilizzare il comando standard Docker:

```bash
docker ps
```

La colonna `PORTS` mostra le associazioni del tipo `host_port:container_port` per ciascun servizio. Alcuni esempi tipici, in una configurazione di default, sono:

* `8081:8081` per lo *User Manager Service*;
* `8082:8082` per il *Data Collector Service*;
* `8083:8083` per l’*Alert System*;
* `8084:8084` per l’*Alert Notifier*;
* `8085:8080` per l’interfaccia web di *Kafka UI*;
* `80:80` per l’*API Gateway* NGINX.

Le porte host riportate in questa colonna sono quelle da utilizzare per accedere alle API REST e ai servizi di supporto dai client esterni (browser, Postman, sistemi di integrazione).

Qualora si modifichino le mappature delle porte nel file `docker-compose.yml`, è necessario rieseguire il ciclo di:

```bash
docker compose down
```

seguito da

```bash
docker compose up --build -d
```

per applicare le modifiche alla configurazione dell’ambiente di esecuzione.

---

### 6.4 Accesso ai servizi infrastrutturali (Kafka UI, API Gateway)

Alcuni servizi infrastrutturali espongono interfacce HTTP utili per attività di monitoraggio, debugging e integrazione con client esterni.

**Kafka UI** è raggiungibile, in configurazione predefinita, all’indirizzo:

```text
http://localhost:8085
```

Una volta autenticati (se richiesto dalla configurazione dell’immagine), l’interfaccia consente di:

* verificare lo stato del cluster Kafka e del broker configurato;
* ispezionare i topic utilizzati dalla piattaforma, in particolare `to-alert-system` e `to-notifier`;
* consultare i messaggi prodotti dal *Data Collector Service* e consumati dall’*Alert System* e dall’*Alert Notifier*;
* eseguire operazioni di diagnostica puntuale (ad esempio filtrare i messaggi in base alle chiavi o agli header) in caso di anomalie nel flusso eventi–notifiche.

L’**API Gateway** basato su NGINX è esposto sulla porta HTTP standard dell’host:

```text
http://localhost
```

Il gateway funge da punto di ingresso unico per i client HTTP/REST, instradando le richieste verso i microservizi applicativi secondo le regole definite nel file di configurazione `docker/nginx/nginx.conf`. In configurazione di default sono previsti, tra gli altri:

* un *upstream* verso lo *User Manager Service*, raggiungibile tramite path prefissati (ad esempio `/api/users/...`);
* un *upstream* verso il *Data Collector Service*, raggiungibile tramite path prefissati (ad esempio `/api/flights/...`, `/api/interests/...`);
* un endpoint di *health check* (`/health`) esposto direttamente dal gateway, che restituisce una risposta semplice per verificare rapidamente la raggiungibilità del front-end HTTP.

L’utilizzo dell’API Gateway consente di centralizzare la gestione degli endpoint applicativi, delle politiche di routing e di eventuali estensioni future (rate limiting, autenticazione, osservabilità), mantenendo al contempo i microservizi isolati dietro un livello di astrazione coerente.

## 7. Accessing the Services

### 7.1 User Manager Service

Lo *User Manager Service* espone un set di API REST dedicate alla gestione del ciclo di vita degli utenti applicativi. Le operazioni consentono di registrare, consultare e cancellare utenti identificati univocamente tramite indirizzo e‑mail, mantenendo un contratto stabile e facilmente integrabile da client esterni.

#### 7.1.1 Endpoint base (host, port)

Il servizio può essere raggiunto in due modalità principali: tramite **API Gateway** (modalità consigliata per i client esterni) oppure in modo diretto, indirizzando il container del microservizio.

*Accesso tramite API Gateway*

Quando lo stack è eseguito tramite Docker Compose, l’API Gateway NGINX espone un endpoint pubblico che instrada le richieste verso lo *User Manager Service*:

* **Base URL (gateway)**: `http://localhost/api/users`

Tutte le operazioni descritte nelle sezioni successive sono accessibili prefissando i path indicati con questo endpoint.

*Accesso diretto al microservizio*

Per scenari di sviluppo o debug, è possibile invocare direttamente il microservizio, bypassando il gateway.

* **Esecuzione locale (senza Docker)**
  Il servizio espone le API HTTP sulla porta configurata nel `application.yml`:

  * **Base URL**: `http://localhost:8081/api/users`

* **Esecuzione in ambiente Docker (rete interna)**
  All’interno della rete Docker, gli altri container possono raggiungere il servizio tramite il suo hostname logico:

  * **Base URL interno**: `http://user-manager-service:8081/api/users`

* **Accesso dall’host tramite port‑mapping**
  Docker Compose effettua il mapping della porta esposta dal container verso l’host. Nel file `docker-compose.yml` è tipicamente configurato un mapping del tipo:

  ```yaml
  ports:
    - "8081:8081"
  ```

  In questo caso, dall’host è possibile invocare il servizio direttamente tramite:

  * **Base URL host**: `http://localhost:8081/api/users`

#### 7.1.2 Principali API REST esposte (registrazione, lettura, cancellazione utente)

Le principali operazioni REST esposte dallo *User Manager Service* sono le seguenti.

**Registrazione di un nuovo utente**

* **Metodo**: `POST`
* **URL**: `/api/users`
* **Body (JSON)**: contiene almeno `email` e `name` dell’utente.

Esempio di payload:

```json
{
  "email": "alice@example.com",
  "name": "Alice Rossi"
}
```

L’endpoint esegue la validazione dei dati in ingresso (in particolare del formato dell’e‑mail) e verifica l’assenza di duplicati nel database.

**Lettura di un utente per e‑mail**

* **Metodo**: `GET`
* **URL**: `/api/users/{email}`

Esempio di richiesta:

```text
GET /api/users/alice@example.com
```

L’endpoint restituisce i dati dell’utente associato all’e‑mail indicata, se presente nel sistema.

**Elenco completo degli utenti registrati**

* **Metodo**: `GET`
* **URL**: `/api/users`

L’endpoint restituisce la lista completa degli utenti registrati, eventualmente paginata o filtrata in base alle opzioni implementate.

**Cancellazione di un utente**

* **Metodo**: `DELETE`
* **URL**: `/api/users/{email}`

Esempio di richiesta:

```text
DELETE /api/users/alice@example.com
```

L’endpoint rimuove l’utente associato all’e‑mail indicata, se esistente.

#### 7.1.3 Codici di risposta attesi per le operazioni chiave

Le principali convenzioni sui codici di stato HTTP restituite dallo *User Manager Service* sono le seguenti.

* **Registrazione utente (`POST /api/users`)**

  * `201 Created` se l’utente è stato creato correttamente;
  * `400 Bad Request` in presenza di errori di validazione sui dati di input (ad esempio e‑mail malformata o campi obbligatori mancanti);
  * `409 Conflict` se esiste già un utente registrato con la stessa e‑mail.

* **Lettura utente (`GET /api/users/{email}`)**

  * `200 OK` se l’utente è stato trovato e i suoi dati sono restituiti nel body della risposta;
  * `404 Not Found` se non esiste alcun utente associato all’e‑mail indicata.

* **Elenco utenti (`GET /api/users`)**

  * `200 OK` con la lista (possibilmente vuota) degli utenti correnti.

* **Cancellazione utente (`DELETE /api/users/{email}`)**

  * `204 No Content` se l’utente è stato cancellato correttamente;
  * `404 Not Found` se non esiste alcun utente associato all’e‑mail indicata.

---

### 7.2 Data Collector Service

Il *Data Collector Service* espone le API REST per la gestione degli interessi utente–aeroporto e per l’interrogazione dei dati di volo raccolti dalle OpenSky Network API. In questa release, gli **interessi** includono anche la configurazione di soglie *high_value* e *low_value* per l’attivazione di notifiche di alert.

#### 7.2.1 Endpoint base (host, port)

Analogamente allo *User Manager*, il *Data Collector Service* può essere invocato tramite API Gateway oppure in modo diretto.

*Accesso tramite API Gateway*

L’API Gateway espone verso l’esterno i principali endpoint del *Data Collector* sotto i seguenti prefissi:

* **Interessi utente–aeroporto**: `http://localhost/api/interests`
* **Interrogazioni sui voli**: `http://localhost/api/flights`

Gli esempi di richiesta riportati nelle sezioni successive assumono questi prefissi come base URL in ambiente containerizzato.

*Accesso diretto al microservizio*

* **Esecuzione locale (senza Docker)**
  Il servizio utilizza la porta configurata nel `application.yml`:

  * **Base URL**: `http://localhost:8082/api`

  Gli endpoint effettivi risultano quindi, ad esempio, `http://localhost:8082/api/interests` e `http://localhost:8082/api/flights`.

* **Esecuzione in ambiente Docker (rete interna)**
  All’interno della rete Docker, gli altri container raggiungono il servizio tramite:

  * **Base URL interno**: `http://data-collector-service:8082/api`

* **Accesso dall’host tramite port‑mapping**
  Il file `docker-compose.yml` definisce il mapping della porta del container verso l’host, tipicamente:

  ```yaml
  ports:
    - "8082:8082"
  ```

  In questo caso, dall’host è possibile invocare direttamente il servizio tramite:

  * **Base URL host**: `http://localhost:8082/api`

#### 7.2.2 API REST per la gestione degli aeroporti e degli interessi (incluse le soglie)

Gli aeroporti monitorabili sono mantenuti nel *Data DB* e possono essere precaricati tramite script SQL o migrazioni Flyway. La gestione dinamica del catalogo aeroporti può essere estesa in futuro con API dedicate; nella configurazione corrente, l’attenzione è focalizzata sulla gestione degli **interessi utente–aeroporto**, comprensivi di soglie.

**Registrazione di un interesse utente–aeroporto con soglie**

* **Metodo**: `POST`
* **URL**: `/api/interests`
* **Body (JSON)**: contiene e‑mail utente, codice aeroporto e parametri di soglia.

Esempio di payload:

```json
{
  "userEmail": "alice@example.com",
  "airportCode": "LIRF",
  "lowValue": 15,
  "highValue": 60
}
```

I campi `lowValue` e `highValue` rappresentano le soglie di interesse, espresse in minuti di ritardo, che verranno utilizzate dall’*Alert System* per determinare quando generare notifiche.

**Aggiornamento di un interesse esistente (incluse le soglie)**

* **Metodo**: `PUT`
* **URL**: `/api/interests/{userEmail}/{airportCode}`

Esempio di richiesta:

```text
PUT /api/interests/alice@example.com/LIRF
```

con body JSON analogo a quello utilizzato per la creazione, in cui è possibile modificare `lowValue` e `highValue`.

**Cancellazione di un interesse**

* **Metodo**: `DELETE`
* **URL**: `/api/interests/{userEmail}/{airportCode}`

Esempio di richiesta:

```text
DELETE /api/interests/alice@example.com/LIRF
```

**Elenco degli interessi per un utente**

* **Metodo**: `GET`
* **URL**: `/api/interests`
* **Query parameters**:

  * `userEmail`: e‑mail dell’utente (obbligatorio).

Esempio di richiesta:

```text
GET /api/interests?userEmail=alice@example.com
```

Il servizio restituisce la lista degli interessi configurati per l’utente indicato, comprensivi dei valori di soglia configurati.

#### 7.2.3 API REST per interrogare i voli

Il *Data Collector Service* espone inoltre API dedicate all’interrogazione dei dati di volo raccolti da OpenSky e memorizzati nel *Data DB*.

**Ultimo volo per aeroporto e direzione**

* **Metodo**: `GET`
* **URL**: `/api/flights/last`
* **Query parameters**:

  * `airportCode`: codice dell’aeroporto (es. `LIRF`);
  * `direction`: direzione del volo, tipicamente `ARRIVAL` o `DEPARTURE`.

Esempio di richiesta:

```text
GET /api/flights/last?airportCode=LIRF&direction=ARRIVAL
```

L’endpoint restituisce l’ultimo volo registrato per la combinazione indicata, includendo le principali informazioni operative (identificativo del volo, orari schedulati ed effettivi, eventuali ritardi, stato, timestamp di raccolta).

**Intervallo temporale di interesse**

* **Metodo**: `GET`
* **URL**: `/api/flights`
* **Query parameters**:

  * `airportCode`: codice dell’aeroporto (obbligatorio);
  * `direction`: direzione del volo (`ARRIVAL`/`DEPARTURE`);
  * `from`: istante iniziale dell’intervallo, espresso in epoch seconds;
  * `to`: istante finale dell’intervallo, espresso in epoch seconds.

L’endpoint restituisce tutti i voli che soddisfano i criteri di ricerca indicati.

Ulteriori endpoint possono essere presenti per interrogazioni più specifiche (ad esempio medie di ritardo su intervalli temporali), mantenendo la stessa convenzione sui parametri principali (`airportCode`, `direction`, intervallo temporale).

---

### 7.3 gRPC Interface

Lo *User Manager Service* espone, oltre alle API REST, un’interfaccia **gRPC** che consente agli altri microservizi di verificare in modo efficiente l’esistenza di un utente a partire dal suo indirizzo e‑mail. Questa interfaccia è utilizzata internamente dal *Data Collector Service* durante la gestione degli interessi.

#### 7.3.1 Panoramica del servizio gRPC esposto dallo User Manager

L’interfaccia gRPC prevede un servizio logico, ad esempio `UserValidationService`, con un metodo principale:

* `rpc userExists(UserEmailRequest) returns (UserExistsResponse)`

in cui:

* `UserEmailRequest` contiene un singolo campo `email`;
* `UserExistsResponse` contiene un campo booleano che indica se l’utente è presente nel database degli utenti gestito dallo *User Manager*.

Il servizio gRPC è pubblicato sulla porta configurata nelle proprietà del microservizio e non è destinato al consumo diretto da parte di client esterni HTTP, ma esclusivamente ad uso interno tra microservizi.

#### 7.3.2 Utilizzo interno da parte del Data Collector (non richiesto lato utente finale)

Il *Data Collector Service* utilizza il metodo `userExists` prima di registrare un nuovo interesse utente–aeroporto. La sequenza tipica è la seguente:

1. Il client invia una richiesta REST a `POST /api/interests` specificando `userEmail` e `airportCode`.
2. Il *Data Collector* costruisce una richiesta gRPC `UserEmailRequest` e invoca `userExists` sullo *User Manager*.
3. Se la risposta indica che l’utente esiste (`exists = true`), il *Data Collector* procede a registrare o aggiornare l’interesse nel proprio database;
4. Se l’utente non esiste, il *Data Collector* restituisce un errore applicativo (ad esempio `400 Bad Request` o `404 Not Found`), evitando di registrare interessi per utenti non validi.

Questa interazione permette di mantenere il *User Manager* come *source of truth* per l’identità applicativa, evitando duplicazioni di responsabilità.

---

### 7.4 API Gateway

L’**API Gateway** è implementato tramite NGINX e costituisce il punto di ingresso unico per le richieste HTTP verso i microservizi interni. Il suo scopo principale è centralizzare la pubblicazione delle API, semplificare la configurazione dei client e schermare i dettagli di rete dei singoli servizi.

#### 7.4.1 Endpoint pubblici esposti dal gateway

Quando lo stack è in esecuzione tramite Docker Compose, l’API Gateway è esposto sull’host tramite il mapping di porta definito nel `docker-compose.yml`:

```yaml
api-gateway:
  ports:
    - "80:80"
```

L’accesso esterno avviene quindi tramite:

* **Base URL gateway**: `http://localhost`

I principali path pubblici sono:

* **Gestione utenti** (proxy verso *User Manager Service*):

  * `http://localhost/api/users` (lista e creazione utenti);
  * `http://localhost/api/users/{email}` (lettura e cancellazione di un utente specifico).

* **Gestione interessi e interrogazione voli** (proxy verso *Data Collector Service*):

  * `http://localhost/api/interests` (creazione, elenco e aggiornamento degli interessi);
  * `http://localhost/api/interests/{userEmail}/{airportCode}` (gestione di un singolo interesse);
  * `http://localhost/api/flights/...` (interrogazioni sui voli, ad esempio `/last`, intervalli temporali, ecc.).

I client HTTP possono utilizzare esclusivamente questi endpoint senza doversi preoccupare dei nomi dei container e delle porte interne.

#### 7.4.2 Instradamento verso i microservizi interni

La configurazione di NGINX instrada i path pubblici verso i microservizi interni tramite direttive `location` e `proxy_pass`. A titolo esemplificativo:

```nginx
location /api/users/ {
    proxy_pass http://user-manager-service:8081;
}

location /api/interests/ {
    proxy_pass http://data-collector-service:8082;
}

location /api/flights/ {
    proxy_pass http://data-collector-service:8082;
}
```

In questo modo, un client che invoca `http://localhost/api/users` viene automaticamente instradato verso il container `user-manager-service` sulla porta `8081`, mentre le richieste a `/api/interests` e `/api/flights` vengono indirizzate al container `data-collector-service` sulla porta `8082`.

---

### 7.5 Alert System & Alert Notifier

Gli ultimi due microservizi applicativi sono dedicati alla pipeline di *alerting*: l’*Alert System Service* elabora gli eventi di volo e valuta le soglie configurate sugli interessi utente–aeroporto, mentre l’*Alert Notifier Service* si occupa dell’invio delle notifiche e‑mail verso gli utenti finali.

#### 7.5.1 Ruolo dei servizi nella pipeline di notifica

L’*Alert System Service* opera come **consumer Kafka** sul topic a cui il *Data Collector* pubblica gli eventi relativi ai voli che superano le condizioni di interesse (ad esempio ritardi oltre `lowValue` o `highValue`). Per ogni messaggio, il servizio:

1. recupera dal *Data DB* le informazioni sull’interesse associato;
2. valuta le soglie configurate per determinare se la condizione richiede una notifica;
3. in caso positivo, produce un nuovo messaggio su un topic Kafka dedicato alle notifiche (ad esempio `to-notifier`), arricchito con i dati necessari all’invio dell’e‑mail (indirizzo del destinatario, aeroporto, dettaglio del volo, ritardo registrato, ecc.).

L’*Alert Notifier Service* è a sua volta un **consumer Kafka** sul topic delle notifiche. Per ogni evento ricevuto, esso:

1. costruisce il contenuto dell’e‑mail (oggetto e corpo del messaggio) sulla base delle informazioni contenute nell’evento di notifica;
2. utilizza il `JavaMailSender` configurato tramite variabili `MAIL_*` per inviare l’e‑mail verso il server SMTP (ad esempio Mailtrap);
3. registra nei log l’esito dell’operazione, includendo eventuali errori restituiti dal server SMTP.

Questi microservizi non espongono **API REST** pubbliche destinate ai client esterni: il loro ruolo è interamente guidato dal consumo e dalla produzione di messaggi Kafka nella pipeline asincrona.

#### 7.5.2 Osservazione del flusso tramite log e Kafka UI

La verifica del corretto funzionamento della pipeline di alerting può essere effettuata principalmente tramite:

* **log dei microservizi**;
* **Kafka UI** esposta dallo stack Docker.

Per ispezionare i log è possibile utilizzare, ad esempio:

```bash
docker compose logs -f alert-system-service
```

e, in un secondo terminale:

```bash
docker compose logs -f alert-notifier-service
```

In questo modo è possibile osservare in tempo reale:

* la ricezione dei messaggi da parte dell’*Alert System*;
* la generazione di eventi di notifica sui topic Kafka;
* l’elaborazione dei messaggi da parte dell’*Alert Notifier* e il tentativo di invio delle e‑mail.

La **Kafka UI** è esposta sull’host tramite il mapping di porta definito nel `docker-compose.yml` (tipicamente `8080:8080`) ed è raggiungibile all’indirizzo:

* `http://localhost:8080`

All’interno della Kafka UI è possibile:

* verificare la presenza dei topic utilizzati dal sistema (ad esempio `to-alert-system`, `to-notifier`);
* ispezionare i messaggi prodotti dal *Data Collector* e consumati dall’*Alert System*;
* controllare i messaggi di notifica destinati all’*Alert Notifier*.

L’osservazione combinata dei log dei microservizi, dei topic in Kafka UI e delle e‑mail recapitate dal provider SMTP di test consente di validare end‑to‑end il comportamento della pipeline di alerting.

## 8. Using Postman Collections

### 8.1 Localizzazione delle collection (`postman/`)

Le **Postman collections** sono collocate nella directory `postman/` della repository e sono organizzate per versione funzionale del sistema. La struttura principale è la seguente:

```text
postman/
  ├── homework-1/
  │   ├── hw1-user-manager-api.postman_collection.json
  │   └── hw1-data-collector-api.postman_collection.json
  └── homework-2/
      ├── hw2 - user-manager-api.postman_collection.json
      └── hw2 - data-collector-api.postman_collection.json
```

Le collection presenti in `postman/homework-1/` rappresentano il set originario di richieste per l’esercizio delle API di **User Manager** e **Data Collector** nella configurazione di base. Le collection in `postman/homework-2/` estendono tale set includendo:

* casi di test addizionali per la gestione delle **soglie** sugli interessi utente–aeroporto;
* scenari di errore e validazione allineati alle nuove regole applicative;
* richieste dedicate alle nuove API esposte dal **Data Collector** per interrogazioni analitiche (ad esempio media dei voli su intervalli temporali).

Per verificare il comportamento della versione corrente del sistema è consigliabile utilizzare in via preferenziale le collection collocate in `postman/homework-2/`, mantenendo le collection di `homework-1` come riferimento storico per la versione base.

---

### 8.2 Import delle collection in Postman

Per utilizzare le collection è sufficiente importare i file `.json` corrispondenti all’interno di Postman. La procedura tipica è la seguente:

1. Avviare **Postman**.
2. Selezionare il pulsante **Import** (in alto a sinistra) oppure usare il menu *File → Import*.
3. Nella scheda **File** della finestra di import, selezionare tramite *drag & drop* o pulsante **Upload Files** i file `.postman_collection.json` desiderati dalla directory `postman/` della repository.
4. Confermare l’operazione: ciascun file verrà importato come **nuova collection**, visibile nel pannello di sinistra.

Le collection importate possono essere duplicate, rinominate o modificate senza impattare i file presenti nel repository.

#### 8.2.1 User Manager API collection

La collection **User Manager API** corrisponde ai file:

* `postman/homework-1/hw1-user-manager-api.postman_collection.json`;
* `postman/homework-2/hw2 - user-manager-api.postman_collection.json`.

La versione in `homework-2` è quella di riferimento per la versione corrente del sistema. Essa contiene un insieme di richieste preconfigurate verso il microservizio **User Manager**, che espone le API responsabili del ciclo di vita degli utenti. In particolare, sono presenti richieste per:

* **registrare un nuovo utente** (`UM-01 – Create user (201 Created)`), con corpo JSON contenente almeno `email` e `name`;
* **tentare la registrazione di un utente duplicato** (`UM-02 – Create user (409 Conflict)`), utile per verificare la gestione dei vincoli di unicità;
* **recuperare la lista degli utenti** (`UM-03 – Get all users`);
* **recuperare un utente per email** (`UM-04 – Get user by email`);
* **cancellare un utente esistente** (`UM-05 – Delete user (204 No Content)`);
* **gestire casi di errore sulla cancellazione** (`UM-06 – Delete user (404 Not Found)`).

Tutte le richieste della collection fanno riferimento, in configurazione predefinita, all’endpoint HTTP del microservizio User Manager esposto in ambiente Docker su:

```text
http://localhost:8081/api/users
```

La porta `8081` è quella configurata per il container `user-manager-service` all’interno del `docker-compose.yml`.

#### 8.2.2 Data Collector API collection

La collection **Data Collector API** corrisponde ai file:

* `postman/homework-1/hw1-data-collector-api.postman_collection.json`;
* `postman/homework-2/hw2 - data-collector-api.postman_collection.json`.

La versione in `homework-2` include l’insieme aggiornato di richieste verso il microservizio **Data Collector**, esteso con la gestione delle soglie sugli interessi e con nuove API di interrogazione analitica. Le richieste coprono, tra le altre, le seguenti aree funzionali:

* **gestione degli aeroporti** (creazione, lettura, cancellazione), con richieste dedicate al popolamento del catalogo degli aeroporti monitorabili;
* **gestione degli interessi utente–aeroporto con soglie**: creazione, aggiornamento, lettura e cancellazione degli interessi che associano un utente a un aeroporto, comprensivi dei valori `highValue` e `lowValue` utilizzati per l’alerting;
* **interrogazione dei voli** associati a uno specifico aeroporto o interesse, inclusi gli endpoint per:

  * recuperare l’ultimo volo in arrivo o in partenza per un aeroporto (`DC-23 – Recupero ultimo volo in arrivo (caso positivo)`, `DC-24 – Recupero ultimo volo in partenza (caso positivo)`);
  * calcolare la **media dei voli** in un determinato numero di giorni per direzione (`DC-29`–`DC-32`);
* **gestione dei casi di errore** (utente inesistente, aeroporto inesistente, soglie non valide, direzioni non valide, ecc.), tramite richieste esplicite che consentono di verificare il comportamento del sistema in condizioni non corrette.

In configurazione predefinita, le richieste puntano al microservizio Data Collector esposto in ambiente Docker su:

```text
http://localhost:8082/api
```

Le API per aeroporti, interessi e voli sono organizzate su path coerenti con il modello di dominio, ad esempio:

* `http://localhost:8082/api/airports` per la gestione del catalogo aeroporti;
* `http://localhost:8082/api/interests` per la gestione degli interessi utente–aeroporto;
* `http://localhost:8082/api/flights/...` per le interrogazioni relative ai voli.

---

### 8.3 Configurazione delle variabili di ambiente in Postman (host, port, base URL)

Le collection fornite sono già configurate per funzionare con la configurazione predefinita descritta nel `docker-compose.yml`, ossia:

* **User Manager** raggiungibile su `http://localhost:8081`;
* **Data Collector** raggiungibile su `http://localhost:8082`.

Gli URL presenti nelle collection utilizzano direttamente questi valori. Se si desidera rendere i test più portabili (ad esempio per eseguire il sistema su host o porte differenti), è possibile introdurre un semplice layer di parametrizzazione tramite **environment variables** di Postman.

Un setup tipico prevede la creazione di un ambiente Postman con le seguenti variabili:

| Variabile                 | Valore predefinito      | Descrizione                                |
| ------------------------- | ----------------------- | ------------------------------------------ |
| `user_manager_base_url`   | `http://localhost:8081` | Base URL del microservizio User Manager    |
| `data_collector_base_url` | `http://localhost:8082` | Base URL del microservizio Data Collector  |
| `api_gateway_base_url`    | `http://localhost`      | Base URL dell’API Gateway NGINX (porta 80) |

Dopo aver creato l’ambiente, è possibile **adattare le request esistenti** sostituendo il prefisso fisso degli URL con i placeholder, ad esempio:

* `http://localhost:8081/api/users` → `{{user_manager_base_url}}/api/users`;
* `http://localhost:8082/api/interests` → `{{data_collector_base_url}}/api/interests`.

In questo modo, un eventuale cambiamento di host o porta potrà essere gestito modificando unicamente i valori nell’ambiente Postman, senza intervenire su ogni singola richiesta.

Qualora si preferisca utilizzare direttamente la configurazione di default, è sufficiente importare le collection e assicurarsi che lo stack Docker sia in esecuzione con le porte `8081` e `8082` esposte come definito nel `docker-compose.yml`.

---

### 8.4 Esecuzione di scenari end-to-end tramite Postman

Le collection fornite consentono di esercitare in modo sistematico le principali funzionalità esposte dal sistema, simulando scenari end‑to‑end che coinvolgono sia lo **User Manager** sia il **Data Collector**. I paragrafi seguenti illustrano tre scenari tipici, che possono essere utilizzati come riferimento per la validazione funzionale.

#### 8.4.1 Registrazione di un nuovo utente

Per registrare un nuovo utente tramite Postman è possibile utilizzare la request `UM-01 – Registrazione utente (caso positivo)` nella collection **User Manager API** (versione `homework-2`). La procedura operativa è la seguente:

1. Assicurarsi che lo stack Docker sia in esecuzione e che il microservizio User Manager sia raggiungibile su `http://localhost:8081`.

2. Aprire la collection **User Manager API** e selezionare la request `UM-01 – Registrazione utente (caso positivo)`.

3. Verificare il corpo JSON della richiesta, che deve contenere almeno i campi obbligatori, ad esempio:

   ```json
   {
     "email": "mario.rossi@example.com",
     "name": "Mario Rossi"
   }
   ```

4. Premere **Send** per inviare la richiesta.

5. Verificare che la risposta restituisca lo **status code** `201 Created` e un payload JSON coerente con il modello utente (incluso l’indirizzo email utilizzato come chiave primaria).

6. (Opzionale) Utilizzare la request `UM-06 – Recupero utente esistente` per verificare che l’utente appena creato sia stato correttamente persistito nel database `userdb`.

Questo scenario permette di verificare sia la corretta esposizione delle API REST di User Manager sia il rispetto dei vincoli di unicità sull’indirizzo email.

#### 8.4.2 Registrazione interessi utente–aeroporto con soglie

Per registrare un **interesse utente–aeroporto** comprensivo di soglie è possibile utilizzare la request `DC-01 – Registrazione interesse (caso positivo)` nella collection **Data Collector API** (versione `homework-2`). Lo scenario tipico prevede i seguenti passi:

1. Verificare che:

   * l’utente sia già registrato nel sistema tramite User Manager (ad esempio con email `mario.rossi@example.com`);
   * il catalogo aeroporti contenga l’aeroporto di interesse. Se necessario, utilizzare una delle request di creazione aeroporto presenti nella collection Data Collector.
2. Aprire la collection **Data Collector API** e selezionare la request `DC-01 – Registrazione interesse (caso positivo)`.
3. Verificare il corpo JSON della richiesta, che associa l’utente a un aeroporto e definisce le **soglie** per l’alerting. Un esempio semplificato di payload è il seguente:

   ```json
   {
     "userEmail": "mario.rossi@example.com",
     "airportCode": "LIMC",
     "lowValue": 15,
     "highValue": 60
   }
   ```

   In questo esempio, `lowValue` e `highValue` rappresentano i minuti di ritardo minimo e massimo oltre i quali il sistema considera l’evento candidato a generare una notifica.
4. Premere **Send** e verificare che la risposta restituisca uno **status code** di successo (tipicamente `201 Created`) con il dettaglio dell’interesse salvato.
5. Utilizzare le request dedicate alla **lettura degli interessi** (ad esempio "Get interests by user and airport") per verificare che l’interesse appena creato sia correttamente persistito nel database `datadb` con le soglie configurate.

La collection include anche richieste addizionali (ad esempio `DC-02`–`DC-04`) che permettono di verificare la gestione dei casi di errore, come la registrazione di un interesse con utente inesistente, aeroporto inesistente o combinazioni di soglie non valide.

#### 8.4.3 Interrogazione dello stato dei voli

Una volta configurati gli interessi e avviato il sistema, il **Data Collector** raccoglie periodicamente i dati di volo dagli endpoint OpenSky, popolando il database `datadb`. Tramite la collection **Data Collector API** è possibile interrogare tali informazioni per analisi operative e di monitoraggio.

Uno scenario tipico di interrogazione tramite Postman è il seguente:

1. Assicurarsi che lo stack Docker sia in esecuzione e che il **Data Collector** abbia avuto il tempo di eseguire almeno un ciclo di raccolta dai servizi OpenSky (secondo la schedulazione configurata).
2. Nella collection **Data Collector API**, selezionare una delle request dedicate alle interrogazioni dei voli, ad esempio:

   * `DC-23 – Recupero ultimo volo in arrivo (caso positivo)`;
   * `DC-24 – Recupero ultimo volo in partenza (caso positivo)`;
   * una delle request `DC-29`–`DC-32` per il **calcolo della media dei voli** negli ultimi *X* giorni.
3. Verificare i parametri della richiesta (ad esempio codice IATA dell’aeroporto, direzione `ARRIVAL`/`DEPARTURE`, numero di giorni `X`) nel path o nella query string, in funzione dell’endpoint utilizzato.
4. Premere **Send** e controllare che la risposta restituisca:

   * uno **status code** di successo (tipicamente `200 OK`);
   * un corpo JSON contenente i dati del volo o il valore aggregato atteso (ad esempio il numero medio di voli nell’intervallo richiesto).
5. (Opzionale) Ripetere l’interrogazione variando i parametri o utilizzando le request che simulano errori (aeroporto inesistente, direzione non valida, numero di giorni non valido) per verificare la gestione delle condizioni anomale da parte del servizio.

Questi scenari Postman permettono di esercitare le funzionalità principali esposte dai microservizi **User Manager** e **Data Collector**, validando il corretto comportamento del sistema nelle operazioni di registrazione utenti, gestione degli interessi con soglie e interrogazione dei dati di volo persi.

## 9. Health Checks, Logs and Basic Diagnostics

### 9.1 Verifica della raggiungibilità dei servizi

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

Oltre ai due microservizi core, nella versione corrente dell'architettura sono presenti ulteriori componenti esposti tramite HTTP, fra cui l'**API Gateway** e i microservizi *Alert System Service* e *Alert Notifier Service*. Per la sola verifica di raggiungibilità si possono utilizzare comandi analoghi basati su `curl` verso le porte pubblicate nel `docker-compose.yml`, ad esempio:

```bash
curl -i http://localhost:<HOST_PORT_API_GATEWAY>/
curl -i http://localhost:<HOST_PORT_ALERT_SYSTEM>/
curl -i http://localhost:<HOST_PORT_ALERT_NOTIFIER>/
```

In tutti i casi, una risposta `2xx`/`3xx` o anche un `4xx` coerente con l'endpoint invocato conferma che il container è in esecuzione e che lo stack di rete è correttamente instradato.

#### 9.1.1 Endpoint di health (se presenti) o semplice ping

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

### 9.2 Log dei microservizi

La seconda fonte di diagnostica è rappresentata dai **log applicativi** prodotti dai microservizi. In ambiente Docker, ogni container instrada l’output standard verso il demone Docker, rendendo possibile la consultazione centralizzata dei log via `docker compose logs`.

#### 9.2.1 Accesso ai log via Docker (`docker compose logs`)

Per visualizzare i log di tutti i servizi definiti nello stack:

```bash
cd docker
docker compose logs -f
```

Il flag `-f` (*follow*) consente di seguire in tempo reale l’evoluzione dei log, utile soprattutto durante la fase di avvio o mentre si eseguono chiamate di test.

Per concentrarsi sui log di un singolo servizio è possibile specificarne il nome, ad esempio:

```bash
docker compose logs -f user-manager-service
```

o, in alternativa:

```bash
docker compose logs -f data-collector-service
```

In questo modo si possono isolare più facilmente gli errori relativi a un singolo microservizio (problemi di connessione al database, errori nelle chiamate alle OpenSky Network API, eccezioni applicative, ecc.).

Per i servizi introdotti nella pipeline di alerting è possibile utilizzare comandi analoghi, ad esempio:

```bash
docker compose logs -f alert-system-service

docker compose logs -f alert-notifier-service
```

In questo modo è possibile correlare i log di produzione e consumo dei messaggi Kafka con gli eventi applicativi (rilevamento delle violazioni di soglia e invio delle notifiche email).

#### 9.2.2 Principali messaggi informativi/di errore da tenere d’occhio

Tra i messaggi più rilevanti per la diagnostica si segnalano:

* i log di **avvio del contesto Spring Boot**, che indicano l’avvenuta inizializzazione del microservizio e l’apertura della porta HTTP di ascolto;
* i log relativi alle **migrazioni Flyway**, che segnalano l’esecuzione corretta degli script di migrazione del database o eventuali problemi di schema;
* i log di **connessione al database**, che evidenziano errori di autenticazione, indisponibilità dell’host o problemi di rete;
* i log relativi alle **invocazioni verso OpenSky**, con particolare attenzione allo stato delle risposte (codici HTTP, messaggi di errore, time‑out);
* eventuali **stack trace** di eccezioni non gestite, che possono fornire indicazioni preziose sulla causa di errori logici o di configurazione.

Nella versione estesa del sistema assumono particolare rilevanza anche:

* i log del **producer Kafka** nel *Data Collector Service*, che confermano la pubblicazione degli eventi di aggiornamento delle finestre temporali verso il topic di input dell’Alert System;
* i log di **consumo ed elaborazione** nel *Alert System Service*, che riportano la ricezione degli eventi, il calcolo delle statistiche di ritardo e l’eventuale generazione delle notifiche di superamento soglia;
* i log relativi all’**invio delle notifiche e‑mail** nel *Alert Notifier Service*, che esplicitano l’indirizzo del destinatario, l’oggetto del messaggio e l’esito della consegna verso il server SMTP configurato.

### 9.3 Diagnostica del database

La diagnostica del database PostgreSQL è utile per verificare che gli **schemi applicativi** siano stati creati correttamente e che le tabelle principali vengano popolati come previsto a seguito dell’esecuzione dei microservizi.

#### 9.3.1 Accesso a PostgreSQL (via CLI o client esterno)

Per accedere al database in ambiente Docker è possibile utilizzare la CLI `psql` all’interno del container PostgreSQL oppure un client esterno connesso alla porta esposta sul host.

Esempio di accesso via CLI dal container:

```bash
cd docker
docker compose exec postgres psql -U ${POSTGRES_USER} -d ${USER_DB_NAME}
```

Una volta connessi al *User DB* (`userdb`), è possibile elencare le tabelle disponibili:

```sql
\dt
```

Analogamente, per connettersi al *Data DB* (`datadb`):

```bash
cd docker
docker compose exec postgres psql -U ${POSTGRES_USER} -d ${DATA_DB_NAME}
```

All’interno di ciascun database è possibile eseguire query di verifica sul contenuto delle tabelle principali, ad esempio:

```sql
SELECT * FROM users;
SELECT * FROM airports;
SELECT * FROM user_airport_interest;
SELECT * FROM flight_records;
```

Queste interrogazioni permettono di verificare se gli utenti, gli aeroporti, gli interessi e i record di volo risultano correttamente inseriti in seguito alle chiamate effettuate tramite le API esposte dai microservizi.

#### 9.3.2 Verifica della creazione automatica di schemi e tabelle (Flyway)

La creazione e l’evoluzione degli schemi del database sono gestite tramite **Flyway**, configurato all’interno dei microservizi. Le migrazioni vengono applicate automaticamente all’avvio dei servizi:

* lo *User Manager Service* applica le migrazioni relative allo schema utenti nel database `userdb`;
* il *Data Collector Service* applica le migrazioni relative a aeroporti, interessi e voli nel database `datadb`.

Nei log di avvio dei microservizi è possibile individuare i messaggi di Flyway che indicano l’esecuzione delle migrazioni, ad esempio:

```text
Flyway Community Edition x.x.x by Redgate
Successfully validated n migrations (execution time ...)
Current version of schema "public": n
Successfully applied n migrations to schema "public" (execution time ...)
```

In caso di errori (script non applicabili, conflitti di versionamento, problemi di permessi), Flyway riporterà dettagli specifici nei log, consentendo di intervenire rapidamente sulla correzione degli script o sulla configurazione del database.

Una ulteriore verifica può essere effettuata confrontando le tabelle e le colonne presenti nel database con quanto previsto dagli script di migrazione (file `Vx__*.sql` e/o migrazioni Java), ad esempio controllando che siano presenti le tabelle `users`, `airports`, `user_airport_interest`, `flight_records` con le relative chiavi primarie, chiavi esterne e vincoli di unicità.

### 9.4 Diagnostica di Kafka e del sistema di posta

La pipeline di alerting introdotta nella versione corrente si basa sull’utilizzo congiunto del **broker Kafka** e del sistema di posta SMTP. Una diagnostica puntuale di questi componenti consente di verificare il corretto flusso degli eventi di superamento soglia e la consegna delle relative notifiche email.

#### 9.4.1 Verifica dei topic e dei messaggi tramite Kafka UI

Il broker Kafka è affiancato da una **Kafka UI** esposta come servizio Docker dedicato (`kafka-ui`), che permette di ispezionare cluster, topic, partizioni e consumer group tramite interfaccia web.

Per prima cosa è opportuno verificare che i container coinvolti siano in esecuzione:

```bash
cd docker
docker compose ps kafka

docker compose ps zookeeper

docker compose ps kafka-ui
```

La colonna delle *ports* nel comando `docker compose ps kafka-ui` indica la porta host su cui è pubblicata la Kafka UI (ad esempio `localhost:<HOST_PORT_KAFKA_UI>`). Una volta individuata la porta, l’interfaccia è raggiungibile da browser all’indirizzo:

```text
http://localhost:<HOST_PORT_KAFKA_UI>
```

Dalla dashboard è possibile:

* verificare l’elenco dei **topic** configurati, inclusi il topic di input su cui il *Data Collector Service* pubblica gli eventi di aggiornamento delle finestre temporali e il topic di output su cui l’*Alert System Service* pubblica le notifiche di superamento soglia destinate all’*Alert Notifier Service*;
* controllare, per ciascun topic, il numero di messaggi presenti, la ripartizione per partizione e lo stato dei **consumer group** associati (in particolare i consumer utilizzati dai servizi *Alert System* e *Alert Notifier*);
* ispezionare il contenuto dei messaggi più recenti per verificare che il payload JSON corrisponda alla struttura attesa (ad esempio eventi di tipo `FlightCollectionWindowUpdateEvent`, `ThresholdBreachDetectionEvent`, `ThresholdBreachNotificationEvent`).

Durante l’esecuzione di scenari di test è possibile osservare in tempo quasi reale l’aumento del numero di messaggi sui topic coinvolti e l’avanzamento degli offset dei consumer. Se i messaggi vengono prodotti ma non consumati, è probabile che vi sia un problema nella configurazione dei listener Kafka o nell’avvio dei microservizi *Alert System* e *Alert Notifier*; viceversa, se i topic rimangono vuoti anche dopo l’esecuzione di chiamate che dovrebbero generare eventi, conviene verificare la parte di integrazione Kafka del *Data Collector Service*.

#### 9.4.2 Verifica dell’invio email tramite Mailtrap

L’invio delle notifiche email è gestito dal microservizio **Alert Notifier**, che utilizza un server SMTP esterno (Mailtrap) configurato tramite le variabili d’ambiente `MAIL_*`. Per verificare il corretto funzionamento di questa integrazione sono consigliati i seguenti passaggi:

1. Accedere all’account Mailtrap utilizzato per il progetto e selezionare la **Inbox** dedicata alle notifiche generate dal sistema.
2. Eseguire uno scenario applicativo che porti alla generazione di una violazione di soglia (ad esempio configurando un interesse con soglia di ritardo particolarmente bassa e inizializzando opportunamente i dati di volo).
3. Monitorare i log del microservizio *Alert Notifier* per verificare che venga emesso un messaggio di invio email verso il destinatario atteso, con indicazione dell’host SMTP, della porta e dell’esito della consegna.
4. Verificare, all’interno della Inbox Mailtrap, la presenza di uno o più messaggi corrispondenti alla violazione di soglia, controllando:

   * il **mittente** configurato nel sistema di notifica;
   * il **destinatario** (indirizzo email associato all’utente che ha configurato l’interesse);
   * l’**oggetto** del messaggio, che riassume la natura della violazione (es. ritardo superiore alla soglia configurata);
   * il **corpo** dell’email, che include i dettagli del volo e i valori di soglia coinvolti.

Se le email non risultano recapitate nella Inbox Mailtrap, è opportuno:

* verificare che le variabili `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, `MAIL_SMTP_AUTH` e `MAIL_SMTP_STARTTLS_ENABLE` siano correttamente valorizzate nel file `docker/env/services.env` e propagate al container dell’*Alert Notifier Service*;
* controllare eventuali errori nei log dell’Alert Notifier relativi alla connessione SMTP (host non raggiungibile, credenziali non valide, problemi di TLS);
* assicurarsi che i messaggi Kafka di notifica siano effettivamente consumati dal microservizio, utilizzando congiuntamente i log applicativi e la Kafka UI descritta nel paragrafo precedente.

## 10. Troubleshooting

### 10.1 Problemi comuni in fase di build

La fase di build può fallire per prerequisiti mancanti o per errori nella costruzione delle immagini Docker. Questa sezione elenca i casi più frequenti e le azioni consigliate per risolverli.

#### 10.1.1 Mancanza di JDK/Maven (in build locale)

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

#### 10.1.2 Errori di build delle immagini Docker

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

### 10.2 Problemi comuni in fase di run

Una volta completata la build, la fase di esecuzione può essere ostacolata da problemi legati al database, alla connettività tra servizi o alle integrazioni esterne.

#### 10.2.1 Il database non si avvia correttamente

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

#### 10.2.2 I servizi non riescono a connettersi a PostgreSQL

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

#### 10.2.3 Errori di autenticazione verso OpenSky

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

#### 10.2.4 Problemi di connessione a Kafka

*Sintomi tipici*

* Nei log di *Alert System* o *Alert Notifier* compaiono errori del tipo:

  ```text
  org.apache.kafka.common.errors.TimeoutException: Failed to update metadata after ...
  ```

  oppure

  ```text
  org.apache.kafka.common.errors.BrokerNotAvailableException
  ```

* Gli eventi di *threshold breach* non vengono consumati da *Alert Notifier* e non vengono inviate email.

*Verifiche e rimedi*

1. Verificare che i container del broker Kafka e, se previsto, di Zookeeper siano in stato `running` o `healthy`:

   ```bash
   cd docker
   docker compose ps
   ```

2. Controllare che la variabile d’ambiente `KAFKA_BOOTSTRAP_SERVERS` (o equivalente) sia coerente con il nome del servizio Kafka definito in `docker-compose.yml` (ad esempio `kafka:9092`) e che non faccia riferimento a `localhost` dal punto di vista dei container.

3. Verificare che i topic necessari (ad esempio il topic dedicato agli eventi di notifica) siano stati creati correttamente. In presenza di Kafka UI, controllare la sezione *Topics* e accertarsi che:

   * il topic esista;
   * partizioni e replication factor siano compatibili con la configurazione del cluster.

4. In caso di errori sporadici di connessione, verificare eventuali problemi di rete tra i container (ad esempio conflitti sulla network Docker) e considerare un riavvio selettivo di Kafka, Alert System e Alert Notifier:

   ```bash
   docker compose restart kafka alert-system-service alert-notifier-service
   ```

#### 10.2.5 Errori SMTP e mancato recapito delle email

*Sintomi tipici*

* Nei log di *Alert Notifier* compaiono errori di tipo:

  ```text
  org.springframework.mail.MailSendException
  ```

  o messaggi che indicano l’impossibilità di connettersi al server SMTP.

* Le email attese non compaiono nella casella di destinazione (ad esempio in Mailtrap).

*Verifiche e rimedi*

1. Verificare che i parametri SMTP (host, porta, username, password, eventuali flag `MAIL_SMTP_AUTH` e `MAIL_SMTP_STARTTLS_ENABLE`) siano correttamente valorizzati nei file `.env` e coerenti con la configurazione del provider (ad esempio Mailtrap).

2. Controllare, tramite i log di *Alert Notifier*, se l’errore è legato alla connessione (es. *connection timed out*), all’autenticazione (es. *535 Authentication failed*) o al rifiuto del messaggio da parte del server (es. *550 Message rejected*). Queste informazioni orientano la correzione:

   * in caso di problemi di connessione, verificare la connettività in uscita e l’eventuale presenza di proxy/firewall;
   * in caso di errori di autenticazione, verificare credenziali e permessi dell’account SMTP;
   * in caso di rifiuto del messaggio, controllare indirizzo mittente/destinatario e policy del provider.

3. Se si utilizza Mailtrap, verificare dalla dashboard web che:

   * le credenziali SMTP siano aggiornate;
   * la casella di posta di test selezionata sia quella configurata in applicazione.

4. In presenza di errori persistenti, abilitare un livello di logging più dettagliato per il package di posta (`org.springframework.mail` e affini) e ripetere l’invio di una notifica di test, analizzando con attenzione il dettaglio dello stack trace.

#### 10.2.6 Comportamento del Circuit Breaker verso OpenSky

*Sintomi tipici*

* Nei log del *Data Collector Service* compaiono messaggi che indicano lo stato del *circuit breaker* (ad esempio `OPEN`, `HALF_OPEN`, `CLOSED`).
* Le richieste verso OpenSky vengono "saltate" immediatamente con errori applicativi interni, senza che venga effettuata una chiamata esterna effettiva.

*Verifiche e rimedi*

1. Interpretare correttamente lo stato del *circuit breaker*:

   * **CLOSED** – il traffico verso OpenSky è normale;
   * **OPEN** – il traffico viene bloccato a causa di un numero eccessivo di errori recenti;
   * **HALF_OPEN** – vengono consentite solo alcune richieste di prova per verificare se il servizio esterno è tornato disponibile.

2. Se il circuito è in stato `OPEN` per un periodo prolungato, verificare:

   * la disponibilità effettiva delle API OpenSky (ad esempio eseguendo una chiamata manuale con `curl` o Postman utilizzando le stesse credenziali);
   * la correttezza delle variabili d’ambiente e degli endpoint di autenticazione e API.

3. Controllare la configurazione del *circuit breaker* (soglie di errore, finestra temporale, tempo di attesa prima del passaggio a `HALF_OPEN`). Valori troppo aggressivi possono portare ad aprire il circuito troppo spesso anche in presenza di errori temporanei.

4. In fase di diagnosi, è possibile aumentare il livello di log per i componenti responsabili dell’integrazione con OpenSky e del *circuit breaker*, così da osservare in dettaglio:

   * le eccezioni che contribuiscono all’apertura del circuito;
   * il momento esatto in cui avviene il cambio di stato.

---

### 10.3 Verifiche passo-passo per isolare gli errori

Le seguenti verifiche guidano un percorso **step-by-step** per isolare i problemi più comuni, partendo dallo strato di configurazione fino ai singoli container.

#### 10.3.1 Verifica variabili d’ambiente

1. Controllare il contenuto dei file `.env` nella cartella `docker/env/` (`postgres.env`, `services.env`) e assicurarsi che non contengano valori evidentemente errati o placeholder.

2. Dal terminale, verificare che le variabili critiche siano effettivamente visibili nel contesto da cui verrà lanciato `docker compose`. Ad esempio:

   ```bash
   echo "$DB_HOST"
   echo "$DB_USERNAME"
   echo "$OPEN_SKY_CLIENT_ID"
   ```

   Se i valori non risultano impostati, è possibile che vengano letti esclusivamente dai file `.env` a livello di Docker Compose (in tal caso il controllo va effettuato leggendo direttamente tali file) o che sia necessario esportarli manualmente per l’esecuzione locale dei microservizi.

3. Verificare eventuali errori di digitazione nei nomi delle variabili, tanto nei file `.env` quanto nelle proprietà Spring Boot che le referenziano (ad esempio `${DB_HOST}` vs `${DB_HOSTNAME}`).

4. Per i servizi introdotti nella seconda release, controllare in particolare:

   * le variabili di configurazione di Kafka (ad esempio `KAFKA_BOOTSTRAP_SERVERS` e i nomi dei topic utilizzati da Alert System e Alert Notifier);
   * le variabili relative al sistema di posta (`MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, eventuali flag SMTP).

   Anche in questo caso è importante che i nomi utilizzati nei file `.env` coincidano con quelli referenziati nelle property Spring Boot.

#### 10.3.2 Verifica delle porte occupate

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

3. Dopo ogni modifica di port mapping, ricordarsi di aggiornare le configurazioni dei client (Postman, script di test) e le variabili di ambiente correlate (ad esempio `user_manager_base_url`, `data_collector_base_url` in Postman). In presenza di nuovi servizi esposti (come l’API Gateway o Kafka UI), verificare che anche le relative porte non vadano in conflitto con processi locali.

#### 10.3.3 Controllo dei log dei singoli container

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

6. Per la nuova pipeline di notifica, analizzare in particolare i log di:

   ```bash
   docker compose logs alert-system-service
   docker compose logs alert-notifier-service
   docker compose logs kafka
   ```

   verificando la corretta produzione e consumazione dei messaggi, nonché la presenza di eventuali errori persistenti relativi alla serializzazione degli eventi, alla connessione con Kafka o all’invio delle email.

## 11. Validation Scenarios

### 11.1 Scenario minimo di smoke test

Lo scenario di *smoke test* ha l’obiettivo di verificare che l’intera piattaforma sia in grado di:

* avviarsi correttamente;
* accettare richieste di gestione utenti;
* registrare interessi utente–aeroporto;
* raccogliere e rendere disponibili dati di volo di base.

#### 11.1.1 Avvio del sistema

1. Assicurarsi che i prerequisiti hardware e software siano soddisfatti (Docker e Docker Compose installati, risorse macchina adeguate) e che le variabili d’ambiente critiche siano state configurate.

2. Posizionarsi nella directory `docker/` della repository:

   ```bash
   cd docker
   ```

3. Avviare l’intero stack tramite Docker Compose:

   ```bash
   docker compose up -d --build
   ```

   L’opzione `--build` forza la ricostruzione delle immagini Docker dei microservizi nel caso in cui siano state apportate modifiche al codice.

4. Verificare lo stato dei container:

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
   * i microservizi Spring Boot abbiano esposto le rispettive porte HTTP senza errori di binding.

#### 11.1.2 Creazione di un utente di test

1. Identificare la porta di esposizione del *User Manager Service* sull’host (ad esempio `<HOST_PORT_UMS>`, tipicamente `8081` in ambiente Docker Compose).

2. Inviare una richiesta `POST` all’endpoint di creazione utente, ad esempio tramite `curl` o Postman:

   ```bash
   curl -i -X POST "http://localhost:<HOST_PORT_UMS>/api/users" \
     -H "Content-Type: application/json" \
     -d '{
       "email": "smoke.user@example.com",
       "name": "Smoke Test User"
     }'
   ```

3. Verificare che la risposta sia `201 Created` alla prima invocazione.

4. Ripetere la stessa richiesta una seconda volta, con identico payload, per accertarsi che la semantica *at-most-once* sia rispettata:

   * la risposta attesa è `200 OK` con il medesimo utente;
   * nel database `userdb` deve risultare **una sola** riga per l’indirizzo email indicato.

5. Controllare la tabella `users` sul database `userdb` per verificare la presenza dell’utente:

   ```sql
   SELECT id, email, name
   FROM users
   WHERE email = 'smoke.user@example.com';
   ```

   Deve risultare un singolo record coerente con i dati inviati.

#### 11.1.3 Registrazione di un interesse per un aeroporto

1. Identificare la porta di esposizione del *Data Collector Service* sull’host (ad esempio `<HOST_PORT_DCS>`, tipicamente `8082` in ambiente Docker Compose).

2. Se necessario, elencare gli aeroporti registrati nella tabella `airports` per scegliere un codice valido (ad esempio `LICC`, `LIMC`, `LIRF`, `LIML`).

3. Inviare una richiesta `POST` all’endpoint di registrazione dell’interesse:

   ```bash
   curl -i -X POST "http://localhost:<HOST_PORT_DCS>/api/interests" \
     -H "Content-Type: application/json" \
     -d '{
       "userEmail": "smoke.user@example.com",
       "airportCode": "LICC"
     }'
   ```

4. Verificare che la risposta sia `201 Created` e che il payload restituito contenga l’associazione utente–aeroporto attesa.

5. Controllare sul database `datadb` l’effettiva creazione dell’interesse nella tabella `user_airport_interest`:

   ```sql
   SELECT user_email, airport_code
   FROM user_airport_interest
   WHERE user_email = 'smoke.user@example.com'
     AND airport_code = 'LICC';
   ```

   Deve risultare un singolo record coerente con i dati appena inseriti.

#### 11.1.4 Verifica del popolamento dei dati di volo

1. Verificare, nei log del *Data Collector Service*, che siano attivi i job schedulati di raccolta dei voli da OpenSky e di persistenza nel database `datadb`.

2. Attendere alcuni cicli di raccolta (in funzione della configurazione dell’intervallo di scheduling).

3. Interrogare la tabella `flight_records` del database `datadb` per verificare che siano stati inseriti record relativi all’aeroporto di interesse:

   ```sql
   SELECT id, airport_code, callsign, departure_airport, arrival_airport, departure_time, arrival_time
   FROM flight_records
   WHERE airport_code = 'LICC'
   ORDER BY departure_time DESC
   LIMIT 10;
   ```

   Devono risultare presenti uno o più record coerenti con la semantica di raccolta implementata.

4. In alternativa o in aggiunta, utilizzare l’API REST di interrogazione dei voli (descritta nello scenario 11.3) per ottenere i dati di volo relativi all’aeroporto monitorato, su una finestra temporale recente.

### 11.2 Scenario di test della politica at-most-once

Questo scenario verifica che la creazione di utenti sia *idempotente* rispetto all’indirizzo email, ovvero che più richieste di registrazione con gli stessi dati non producano duplicati nel database.

#### 11.2.1 Ripetizione di una registrazione utente

1. Assicurarsi che il *User Manager Service* sia avviato e raggiungibile sull’host, come nello scenario 11.1.2.

2. Inviare una prima richiesta `POST` di creazione utente:

   ```bash
   curl -i -X POST "http://localhost:<HOST_PORT_UMS>/api/users" \
     -H "Content-Type: application/json" \
     -d '{
       "email": "atmost.user@example.com",
       "name": "AtMostOnce User"
     }'
   ```

   La risposta attesa è `201 Created` e nel database `userdb` deve essere creato un nuovo record.

3. Ripetere la richiesta con **lo stesso** payload:

   ```bash
   curl -i -X POST "http://localhost:<HOST_PORT_UMS>/api/users" \
     -H "Content-Type: application/json" \
     -d '{
       "email": "atmost.user@example.com",
       "name": "AtMostOnce User"
     }'
   ```

4. Verificare che la seconda risposta sia `200 OK` e che il corpo contenga i dati dell’utente già esistente, senza creare un nuovo record.

#### 11.2.2 Comportamento atteso (assenza di duplicati, codici HTTP attesi)

1. Interrogare la tabella `users` del database `userdb` per verificare il numero di record associati all’indirizzo email di test:

   ```sql
   SELECT COUNT(*) AS cnt
   FROM users
   WHERE email = 'atmost.user@example.com';
   ```

   Il valore di `cnt` deve essere pari a **1**.

2. Verificare che il comportamento del servizio rispetti le seguenti regole:

   * `201 Created` quando viene creato un nuovo utente;
   * `200 OK` quando l’utente esiste già ed è restituito in modo idempotente.

3. Facoltativamente, ripetere il test con altri indirizzi email per verificare che la semantica sia applicata in modo uniforme.

### 11.3 Scenario di interrogazione dei voli su intervalli temporali

Questo scenario verifica il corretto funzionamento dell’API di interrogazione dei voli su intervalli temporali, con particolare attenzione alla coerenza tra i dati restituiti e quelli memorizzati nel database `datadb`.

1. Assicurarsi che siano stati raccolti dati di volo per almeno un aeroporto (ad esempio `LICC`), come descritto nello scenario 11.1.4.

2. Identificare una finestra temporale di interesse (ad esempio le ultime due ore), espressa in UNIX timestamp o nel formato richiesto dall’API.

3. Inviare una richiesta `GET` all’endpoint di interrogazione dei voli del *Data Collector Service*:

   ```bash
   curl -s "http://localhost:<HOST_PORT_DCS>/api/flights?airport=LICC&from=<FROM_TS>&to=<TO_TS>"
   ```

4. Verificare che la risposta contenga una lista di voli coerente con le aspettative, ad esempio controllando:

   * la presenza di voli con `departure_time` e/o `arrival_time` compresi nell’intervallo [`from`, `to`];
   * la correttezza del `airport_code` rispetto a quello richiesto;
   * l’assenza di duplicati evidenti.

5. Confrontare un sottoinsieme dei voli restituiti con i dati presenti nella tabella `flight_records` del database `datadb`, ad esempio:

   ```sql
   SELECT id, airport_code, callsign, departure_time, arrival_time
   FROM flight_records
   WHERE airport_code = 'LICC'
     AND departure_time >= <FROM_TS>
     AND departure_time <= <TO_TS>
   ORDER BY departure_time;
   ```

   I record restituiti dall’API devono essere coerenti con quelli presenti nel database, sia per numero sia per contenuto.

6. Verificare, infine, che l’API gestisca correttamente i casi limite, ad esempio:

   * intervalli temporali vuoti (nessun volo registrato);
   * valori di `from`/`to` non validi o invertiti (con la restituzione di errori HTTP adeguati);
   * codice aeroporto non presente nella tabella `airports`.

### 11.4 Scenario di configurazione e valutazione delle soglie

Questo scenario ha l’obiettivo di verificare che il sistema gestisca correttamente la configurazione delle soglie di traffico associate agli interessi utente–aeroporto e che tali soglie vengano effettivamente considerate nella valutazione dei volumi di volo aggregati sulle finestre temporali.

#### 11.4.1 Creazione di un interesse con `highValue`/`lowValue`

1. Assicurarsi che lo stack applicativo sia in esecuzione, con in particolare attivi:

   * il *User Manager Service*;
   * il *Data Collector Service*;
   * l’*Alert System Service*;
   * il broker **Kafka**;
   * il database `datadb`.

2. Creare (o riutilizzare) un utente dedicato ai test di soglia, ad esempio:

   ```bash
   curl -i -X POST "http://localhost:<HOST_PORT_UMS>/api/users" \\
     -H "Content-Type: application/json" \\
     -d '{
       "email": "threshold.user@example.com",
       "name": "Threshold User"
     }'
   ```

   La risposta attesa è `201 Created` alla prima invocazione ed `200 OK` alle eventuali invocazioni successive, in linea con la semantica *at-most-once* descritta nello scenario 11.2.

3. Scegliere un aeroporto tra quelli presenti nella tabella `airports`, ad esempio `LIMC` (Milano Malpensa) o `LIRF` (Roma Fiumicino).

4. Creare un interesse utente–aeroporto impostando esplicitamente i campi di soglia `highValue` e/o `lowValue`. A titolo di esempio, per configurare una soglia *alta* molto bassa (in modo da facilitare il superamento):

   ```bash
   curl -i -X POST "http://localhost:<HOST_PORT_DCS>/api/interests" \\
     -H "Content-Type: application/json" \\
     -d '{
       "userEmail": "threshold.user@example.com",
       "airportCode": "LIMC",
       "highValue": 1,
       "lowValue": null
     }'
   ```

   In alternativa, è possibile configurare anche una soglia *bassa*:

   ```bash
   curl -i -X POST "http://localhost:<HOST_PORT_DCS>/api/interests" \\
     -H "Content-Type: application/json" \\
     -d '{
       "userEmail": "threshold.user@example.com",
       "airportCode": "LIMC",
       "highValue": 10,
       "lowValue": 2
     }'
   ```

   I campi `highValue` e `lowValue`, se presenti, devono essere **maggiore o uguale a zero**, in accordo con le regole di validazione applicate nel *Data Collector Service*.

5. Verificare che l’interesse sia stato creato correttamente tramite l’API di lettura:

   ```bash
   curl -s "http://localhost:<HOST_PORT_DCS>/api/interests?userEmail=threshold.user@example.com"
   ```

   Il payload di risposta deve contenere l’interesse associato all’aeroporto scelto, con i campi `highValue` e `lowValue` valorizzati secondo la configurazione inviata.

6. Facoltativamente, verificare sul database `datadb` il contenuto della tabella `user_airport_interest`, ad esempio:

   ```sql
   SELECT user_email, airport_code, high_value, low_value
   FROM user_airport_interest
   WHERE user_email  = 'threshold.user@example.com'
     AND airport_code = 'LIMC';
   ```

   I valori presenti nelle colonne `high_value` e `low_value` devono essere coerenti con quelli specificati nel payload REST.

#### 11.4.2 Generazione di un carico di voli che superi la soglia

1. Assicurarsi che esistano uno o più interessi con soglie impostate per l’aeroporto scelto, come descritto nel punto precedente.

2. Verificare che il *Flight Collection Scheduler* del *Data Collector Service* sia in esecuzione, controllando i log del container:

   ```bash
   cd docker
   docker compose logs -f data-collector-service
   ```

   Nei log devono comparire periodicamente messaggi che indicano l’avvio e il completamento dei cicli di raccolta, con l’indicazione della finestra temporale elaborata.

3. Monitorare, tramite **Kafka UI**, il topic `to-alert-system` su cui il *Data Collector Service* pubblica gli eventi `FlightCollectionWindowUpdateEvent`:

   * aprire la Kafka UI all’indirizzo `http://localhost:8085`;
   * selezionare il cluster configurato;
   * individuare il topic `to-alert-system`;
   * ispezionare i messaggi più recenti.

4. Per ciascun messaggio di tipo `FlightCollectionWindowUpdateEvent`, analizzare il campo `airports`, che contiene una lista di snapshot `AirportFlightsWindowSnapshot`. Per l’aeroporto configurato (ad esempio `LIMC`) verificare i campi:

   * `arrivalsCount`;
   * `departuresCount`.

   Il **numero totale di voli** considerato dall’Alert System è dato da:

   ```text
   totalFlights = arrivalsCount + departuresCount
   ```

5. Scegliere un messaggio in cui `totalFlights` risulti coerente con le soglie impostate. Ad esempio:

   * per testare una violazione *HIGH*, è opportuno che `totalFlights` sia **maggiore** di `highValue`;
   * per testare una violazione *LOW*, è opportuno che `totalFlights` sia **minore** di `lowValue`.

6. Osservare i log dell’*Alert System Service*:

   ```bash
   docker compose logs -f alert-system-service
   ```

   In corrispondenza dell’elaborazione di un evento con `totalFlights` fuori soglia devono essere presenti log di pubblicazione di una notifica di superamento soglia verso Kafka, con indicazione del tipo di violazione (`HIGH` o `LOW`), dell’aeroporto e dell’utente interessato.

7. Facoltativamente, verificare nuovamente da Kafka UI che sul topic `to-notifier` siano presenti messaggi `ThresholdBreachNotificationEvent` coerenti con le soglie e con i conteggi osservati sul topic `to-alert-system`.

### 11.5 Scenario end-to-end della pipeline di notifica

Questo scenario ha lo scopo di verificare il funzionamento end-to-end della pipeline di notifica, dalla pubblicazione degli aggiornamenti di traffico sul topic Kafka `to-alert-system` fino all’invio delle email di alert tramite l’*Alert Notifier Service* e l’infrastruttura SMTP configurata (ad esempio Mailtrap).

#### 11.5.1 Pubblicazione su `to-alert-system` e propagazione su `to-notifier`

1. Assicurarsi che siano soddisfatte le seguenti condizioni:

   * esista almeno un interesse con soglie configurate per un aeroporto, come descritto nello scenario 11.4;
   * lo *User Manager Service*, il *Data Collector Service*, l’*Alert System Service* e il broker Kafka siano in esecuzione;
   * il *Flight Collection Scheduler* del *Data Collector Service* sia attivo.

2. Verificare, tramite Kafka UI, che il topic `to-alert-system` riceva periodicamente eventi `FlightCollectionWindowUpdateEvent`:

   * controllare che ogni messaggio contenga i campi `windowBegin`, `windowEnd` e la collezione di `airports`;
   * individuare gli snapshot relativi all’aeroporto per cui è stata configurata la soglia.

3. In presenza di un messaggio in cui `totalFlights` (somma di `arrivalsCount` e `departuresCount`) superi `highValue` o sia inferiore a `lowValue` per almeno un interesse configurato:

   * l’*Alert System Service* deve elaborare l’evento e generare uno o più `ThresholdBreachNotificationEvent`;
   * tali eventi devono essere pubblicati sul topic `to-notifier`.

4. Utilizzando nuovamente Kafka UI, ispezionare il topic `to-notifier`:

   * verificare la presenza di messaggi con chiave pari all’indirizzo email dell’utente (`userEmail`);
   * controllare il payload JSON e verificare che i valori di:

     * `airportCode`;
     * `breachType` (`HIGH` o `LOW`);
     * `actualValue` (numero di voli osservati);
     * `thresholdValue` (valore di soglia);
     * `windowBegin` e `windowEnd`;
       siano coerenti con i dati pubblicati in precedenza sul topic `to-alert-system`.

5. Monitorare in parallelo i log di *Alert System Service* e *Alert Notifier Service*:

   ```bash
   docker compose logs -f alert-system-service
   docker compose logs -f alert-notifier-service
   ```

   Nei log dell’Alert System devono comparire messaggi informativi relativi alla pubblicazione delle notifiche di superamento soglia; nei log dell’Alert Notifier devono comparire messaggi che attestano il consumo delle notifiche da Kafka e l’inoltro verso il servizio di posta.

#### 11.5.2 Verifica finale della ricezione email

1. Configurare correttamente le variabili d’ambiente SMTP (ad esempio Mailtrap) nel file `docker/env/services.env`, assicurandosi che i parametri:

   * `MAIL_HOST`;
   * `MAIL_PORT`;
   * `MAIL_USERNAME`;
   * `MAIL_PASSWORD`;
   * `MAIL_SMTP_AUTH`;
   * `MAIL_SMTP_STARTTLS_ENABLE`;
   * `MAIL_FROM`;

   siano coerenti con l’account di test utilizzato.

2. Accedere all’interfaccia web del provider SMTP di test (ad esempio Mailtrap) e selezionare la inbox configurata per l’ambiente di sviluppo.

3. Generare un carico di voli che porti al superamento di almeno una soglia (`HIGH` o `LOW`) per uno degli interessi configurati, come descritto nello scenario 11.4.2:

   * attendere l’esecuzione di uno o più cicli del *Flight Collection Scheduler*;
   * verificare, se necessario, da Kafka UI che sul topic `to-notifier` siano presenti nuove notifiche di superamento soglia.

4. Controllare la inbox del provider SMTP di test e verificare la presenza di una o più email indirizzate all’utente configurato (ad esempio `threshold.user@example.com`). Per ciascuna email verificare che:

   * l’oggetto contenga un riferimento esplicito al tipo di violazione (`HIGH`/`LOW`) e all’aeroporto interessato;
   * il corpo del messaggio riporti in modo chiaro:

     * il codice dell’aeroporto;
     * il tipo di violazione;
     * il valore osservato (`actualValue`);
     * il valore di soglia (`thresholdValue`);
     * la finestra temporale (`windowBegin`–`windowEnd`) a cui si riferisce la valutazione.

5. Mantenendo aperti i log dell’*Alert Notifier Service*, verificare che l’invio delle email sia accompagnato da messaggi di log coerenti (costruzione del subject/body, tentativo di invio, eventuale gestione di errori SMTP).

### 11.6 Scenario con indisponibilità di OpenSky e Circuit Breaker attivo

Questo scenario verifica il comportamento del sistema in presenza di indisponibilità o forte degrado del servizio OpenSky, con particolare attenzione al corretto funzionamento del **Circuit Breaker** configurato sull’`OpenSkyClient` del *Data Collector Service* e all’impatto sulla pipeline di raccolta dati e di alerting.

1. Identificare la configurazione corrente dell’endpoint OpenSky nel file `docker/env/services.env`, in corrispondenza delle variabili:

   * `OPENSKY_AUTH_URL`;
   * `OPENSKY_API_URL`;
   * `OPENSKY_CLIENT_ID`;
   * `OPENSKY_CLIENT_SECRET`.

2. Per simulare l’indisponibilità di OpenSky, modificare temporaneamente il valore di `OPENSKY_API_URL` impostando un host non raggiungibile o un endpoint fittizio, ad esempio:

   ```env
   OPENSKY_API_URL=https://invalid-opensky-endpoint.local/api
   ```

   Salvare il file e riavviare almeno il *Data Collector Service*:

   ```bash
   cd docker
   docker compose up -d --build data-collector-service
   ```

3. Monitorare i log del *Data Collector Service*:

   ```bash
   docker compose logs -f data-collector-service
   ```

   Dopo alcuni cicli del *Flight Collection Scheduler* devono comparire messaggi che indicano errori nelle chiamate verso OpenSky (errori HTTP o di rete) e l’attivazione del **fallback** associato al Circuit Breaker, con evidenza del fatto che viene restituita una lista vuota di voli per la finestra considerata.

4. Verificare che, nonostante i fallimenti verso OpenSky, il processo schedulato continui a essere eseguito regolarmente e che il microservizio non vada in crash. Nei log non devono comparire eccezioni non gestite né arresti del contesto Spring Boot.

5. Controllare tramite Kafka UI il topic `to-alert-system` durante il periodo di indisponibilità simulata:

   * in assenza di nuovi dati di volo per gli aeroporti monitorati, il *Data Collector Service* potrebbe non pubblicare nuovi eventi `FlightCollectionWindowUpdateEvent`;
   * qualora vengano comunque pubblicati eventi, gli snapshot `AirportFlightsWindowSnapshot` per gli aeroporti interessati dovranno avere conteggi `arrivalsCount` e `departuresCount` pari a zero, coerentemente con il fallback del Circuit Breaker.

6. Verificare i log dell’*Alert System Service*:

   ```bash
   docker compose logs -f alert-system-service
   ```

   In corrispondenza degli eventuali eventi ricevuti da `to-alert-system`, il servizio deve:

   * evitare eccezioni legate a dati mancanti;
   * non generare notifiche di superamento soglia spurie (in assenza di traffico reale);
   * loggare l’assenza di interessi con soglie associate, ove applicabile, o l’assenza di violazioni di soglia per gli snapshot ricevuti.

7. Interrogare l’API di lettura dei voli per uno degli aeroporti monitorati, su una finestra temporale ricadente nel periodo di indisponibilità simulata:

   ```bash
   curl -s "http://localhost:<HOST_PORT_DCS>/api/flights?airport=LIMC&from=...&to=..."
   ```

   La risposta deve contenere un numero di record coerente con il comportamento del fallback (tipicamente zero nuovi voli registrati nel periodo di fault).

8. Ripristinare la configurazione corretta di `OPENSKY_API_URL` nel file `services.env` e riavviare il *Data Collector Service*:

   ```bash
   OPENSKY_API_URL=https://opensky-network.org/api

   cd docker
   docker compose up -d --build data-collector-service
   ```

   Dopo il ripristino, verificare che i log del *Data Collector Service* tornino a mostrare chiamate verso OpenSky concluse con successo e che la raccolta periodica dei voli riprenda a popolare la tabella `flight_records` e, se configurate soglie adeguate, a generare nuovamente eventi di aggiornamento verso Kafka.