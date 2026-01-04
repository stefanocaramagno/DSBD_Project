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
    * [1.2.7 Prometheus](#127-prometheus)

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
    * [2.2.2 Infrastruttura applicativa (API Gateway)](#222-infrastruttura-applicativa-api-gateway)
    * [2.2.3 Infrastruttura di messaging (Kafka, Zookeeper, Kafka UI)](#223-infrastruttura-di-messaging-kafka-zookeeper-kafka-ui)
    * [2.2.4 Infrastruttura di monitoring (Prometheus)](#224-infrastruttura-di-monitoring-prometheus)
    * [2.2.5 Manifest Kubernetes (`k8s/`) e Kustomize](#225-manifest-kubernetes-k8s-e-kustomize)
    * [2.2.6 Script di supporto (build, kind, deploy)](#226-script-di-supporto-build-kind-deploy)
    * [2.2.7 Documentazione e diagrammi](#227-documentazione-e-diagrammi)
    * [2.2.8 Postman collections](#228-postman-collections)

  * [2.3 File chiave per build & deploy](#23-file-chiave-per-build--deploy)

* [3. Prerequisites](#3-prerequisites)

  * [3.1 Requisiti hardware e sistema operativo](#31-requisiti-hardware-e-sistema-operativo)

  * [3.2 Software necessario](#32-software-necessario)

    * [3.2.1 Docker](#321-docker)
    * [3.2.2 kubectl](#322-kubectl)
    * [3.2.3 kind](#323-kind)
    * [3.2.4 Kustomize (opzionale: alternativa a `kubectl apply -k`)](#324-kustomize-opzionale-alternativa-a-kubectl-apply--k)
    * [3.2.5 JDK (per build/esecuzione locale senza Docker)](#325-jdk-per-buildesecuzione-locale-senza-docker)
    * [3.2.6 Maven (per build/esecuzione locale senza Docker)](#326-maven-per-buildesecuzione-locale-senza-docker)

  * [3.3 Account e credenziali esterne](#33-account-e-credenziali-esterne)

    * [3.3.1 Registrazione a OpenSky Network](#331-registrazione-a-opensky-network)
    * [3.3.2 Ottenimento delle credenziali OAuth2 (client id/secret)](#332-ottenimento-delle-credenziali-oauth2-client-idsecret)
    * [3.3.3 Configurazione di un account Mailtrap (SMTP testing)](#333-configurazione-di-un-account-mailtrap-smtp-testing)

  * [3.4 Verifica installazione dei prerequisiti](#34-verifica-installazione-dei-prerequisiti)

* [4. Configuration](#4-configuration)

  * [4.1 Strategie di configurazione (env-based configuration)](#41-strategie-di-configurazione-env-based-configuration)

  * [4.2 ConfigMap e Secret (Kubernetes)](#42-configmap-e-secret-kubernetes)

    * [4.2.1 ConfigMap: variabili non sensibili](#421-configmap-variabili-non-sensibili)
    * [4.2.2 Secret: credenziali e dati sensibili](#422-secret-credenziali-e-dati-sensibili)
    * [4.2.3 Gestione sicura delle credenziali (placeholder vs valori reali)](#423-gestione-sicura-delle-credenziali-placeholder-vs-valori-reali)

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
    * [4.6.2 Configurazione del Circuit Breaker Resilience4j verso OpenSky](#462-configurazione-del-circuit-breaker-resilience4j-verso-opensky)

  * [4.7 Configurazione del monitoring (Actuator/Micrometer/Prometheus)](#47-configurazione-del-monitoring-actuatormicrometerprometheus)

    * [4.7.1 Esposizione endpoint `/actuator/prometheus`](#471-esposizione-endpoint-actuatorprometheus)
    * [4.7.2 Convenzioni di naming e labeling delle metriche](#472-convenzioni-di-naming-e-labeling-delle-metriche)

* [5. Build Instructions](#5-build-instructions)

  * [5.1 Build immagini Docker (modalità raccomandata)](#51-build-immagini-docker-modalità-raccomandata)

    * [5.1.1 Posizionamento nella cartella corretta](#511-posizionamento-nella-cartella-corretta)
    * [5.1.2 Comando per buildare le immagini Docker](#512-comando-per-buildare-le-immagini-docker)
    * [5.1.3 Descrizione dei Dockerfile dei microservizi (multi-stage build)](#513-descrizione-dei-dockerfile-dei-microservizi-multi-stage-build)

  * [5.2 Build locale senza Docker (opzionale)](#52-build-locale-senza-docker-opzionale)

    * [5.2.1 Build dello User Manager Service con Maven](#521-build-dello-user-manager-service-con-maven)
    * [5.2.2 Build del Data Collector Service con Maven](#522-build-del-data-collector-service-con-maven)
    * [5.2.3 Build dell’Alert System Service con Maven](#523-build-dellalert-system-service-con-maven)
    * [5.2.4 Build dell’Alert Notifier Service con Maven](#524-build-dellalert-notifier-service-con-maven)
    * [5.2.5 Differenze rispetto alla modalità container-based](#525-differenze-rispetto-alla-modalità-docker-based)

  * [5.3 Caricamento immagini nel cluster kind](#53-caricamento-immagini-nel-cluster-kind)

    * [5.3.1 Verifica immagini disponibili localmente](#531-verifica-immagini-disponibili-localmente)
    * [5.3.2 Comando `kind load docker-image`](#532-comando-kind-load-docker-image)
    * [5.3.3 Verifica del caricamento nel cluster](#533-verifica-del-caricamento-nel-cluster)

* [6. Deploy & Run on Kubernetes (kind)](#6-deploy--run-on-kubernetes-kind)

  * [6.1 Prima esecuzione dello stack completo](#61-prima-esecuzione-dello-stack-completo)

    * [6.1.1 Creazione del cluster kind](#611-creazione-del-cluster-kind)
    * [6.1.2 Preparazione ConfigMap e Secret](#612-preparazione-configmap-e-secret)
    * [6.1.3 Deploy tramite Kustomize (`kubectl apply -k`)](#613-deploy-tramite-kustomize-kubectl-apply--k)
    * [6.1.4 Verifica che i Pod siano in esecuzione](#614-verifica-che-i-pod-siano-in-esecuzione)

  * [6.2 Arresto del sistema](#62-arresto-del-sistema)

    * [6.2.1 Eliminazione delle risorse Kubernetes (`kubectl delete -k`)](#621-eliminazione-delle-risorse-kubernetes-kubectl-delete--k)
    * [6.2.2 Eliminazione del cluster kind](#622-eliminazione-del-cluster-kind)

  * [6.3 Comandi Kubernetes utili](#63-comandi-kubernetes-utili)

    * [6.3.1 Visualizzazione risorse (`kubectl get`) e stato](#631-visualizzazione-risorse-kubectl-get-e-stato)
    * [6.3.2 Accesso ai log di un Pod (`kubectl logs`)](#632-accesso-ai-log-di-un-pod-kubectl-logs)
    * [6.3.3 Describe e debugging (`kubectl describe`)](#633-describe-e-debugging-kubectl-describe)

  * [6.4 Deploy dei servizi infrastrutturali (overview)](#64-deploy-dei-servizi-infrastrutturali-overview)

    * [6.4.1 PostgreSQL](#641-postgresql)
    * [6.4.2 Kafka e servizi correlati](#642-kafka-e-servizi-correlati)
    * [6.4.3 Kafka UI](#643-kafka-ui)
    * [6.4.4 Prometheus](#644-prometheus)

* [7. Accessing the Services](#7-accessing-the-services)

  * [7.1 Accesso tramite port-forward (approccio raccomandato)](#71-accesso-tramite-port-forward-approccio-raccomandato)

    * [7.1.1 Port-forward API Gateway](#711-port-forward-api-gateway)
    * [7.1.2 Port-forward Kafka UI](#712-port-forward-kafka-ui)
    * [7.1.3 Port-forward Prometheus](#713-port-forward-prometheus)

  * [7.2 User Manager Service](#72-user-manager-service)

    * [7.2.1 Endpoint base (host, port)](#721-endpoint-base-host-port)
    * [7.2.2 Principali API REST esposte](#722-principali-api-rest-esposte)
    * [7.2.3 Codici di risposta attesi](#723-codici-di-risposta-attesi)

  * [7.3 Data Collector Service](#73-data-collector-service)

    * [7.3.1 Endpoint base (host, port)](#731-endpoint-base-host-port)
    * [7.3.2 API REST per aeroporti e interessi (incluse le soglie)](#732-api-rest-per-aeroporti-e-interessi-incluse-le-soglie)
    * [7.3.3 API REST per interrogare i voli](#733-api-rest-per-interrogare-i-voli)

  * [7.4 gRPC Interface](#74-grpc-interface)

    * [7.4.1 Panoramica del servizio gRPC esposto dallo User Manager](#741-panoramica-del-servizio-grpc-esposto-dallo-user-manager)
    * [7.4.2 Utilizzo interno da parte del Data Collector](#742-utilizzo-interno-da-parte-del-data-collector)

  * [7.5 API Gateway](#75-api-gateway)

    * [7.5.1 Endpoint pubblici esposti dal gateway](#751-endpoint-pubblici-esposti-dal-gateway)
    * [7.5.2 Instradamento verso i microservizi interni](#752-instradamento-verso-i-microservizi-interni)

  * [7.6 Alert System & Alert Notifier](#76-alert-system--alert-notifier)

    * [7.6.1 Ruolo dei servizi nella pipeline di notifica](#761-ruolo-dei-servizi-nella-pipeline-di-notifica)
    * [7.6.2 Osservazione del flusso tramite log e Kafka UI](#762-osservazione-del-flusso-tramite-log-e-kafka-ui)

  * [7.7 Kafka UI](#77-kafka-ui)

    * [7.7.1 Endpoint di accesso (host, port)](#771-endpoint-di-accesso-host-port)
    * [7.7.2 Verifica dei topic e ispezione messaggi](#772-verifica-dei-topic-e-ispezione-messaggi)

  * [7.8 Prometheus UI](#78-prometheus-ui)

    * [7.8.1 Endpoint di accesso (host, port)](#781-endpoint-di-accesso-host-port)
    * [7.8.2 Verifica scraping (Targets) e query base](#782-verifica-scraping-targets-e-query-base)

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

* [9. Monitoring & Metrics (Prometheus)](#9-monitoring--metrics-prometheus)

  * [9.1 Endpoint `/actuator/prometheus` e metriche applicative](#91-endpoint-actuatorprometheus-e-metriche-applicative)
  * [9.2 Tipologie richieste: COUNTER e GAUGE](#92-tipologie-richieste-counter-e-gauge)
  * [9.3 Labeling (service, node) e convenzioni adottate](#93-labeling-service-node-e-convenzioni-adottate)
  * [9.4 Verifica dei target in Prometheus (Status > Targets)](#94-verifica-dei-target-in-prometheus-status--targets)
  * [9.5 Query PromQL di riferimento](#95-query-promql-di-riferimento)

    * [9.5.1 Query COUNTER (rate/increase)](#951-query-counter-rateincrease)
    * [9.5.2 Query GAUGE (valori istantanei e aggregazioni)](#952-query-gauge-valori-istantanei-e-aggregazioni)
    * [9.5.3 Query per label (service/node)](#953-query-per-label-servicenode)

* [10. Health Checks, Logs and Basic Diagnostics](#10-health-checks-logs-and-basic-diagnostics)

  * [10.1 Verifica della raggiungibilità dei servizi](#101-verifica-della-raggiungibilità-dei-servizi)

    * [10.1.1 Endpoint di health (se presenti) o semplice ping](#1011-endpoint-di-health-se-presenti-o-semplice-ping)

  * [10.2 Log dei microservizi](#102-log-dei-microservizi)

    * [10.2.1 Accesso ai log via Kubernetes (`kubectl logs`)](#1021-accesso-ai-log-via-kubernetes-kubectl-logs)
    * [10.2.2 Principali messaggi informativi/di errore da tenere d’occhio](#1022-principali-messaggi-informativodi-errore-da-tenere-docchio)

  * [10.3 Diagnostica del database](#103-diagnostica-del-database)

    * [10.3.1 Accesso a PostgreSQL (via CLI o client esterno)](#1031-accesso-a-postgresql-via-cli-o-client-esterno)
    * [10.3.2 Verifica della creazione automatica di schemi e tabelle (Flyway)](#1032-verifica-della-creazione-automatica-di-schemi-e-tabelle-flyway)

  * [10.4 Diagnostica di Kafka, Mailtrap e Prometheus](#104-diagnostica-di-kafka-mailtrap-e-prometheus)

    * [10.4.1 Verifica dei topic e dei messaggi tramite Kafka UI](#1041-verifica-dei-topic-e-dei-messaggi-tramite-kafka-ui)
    * [10.4.2 Verifica dell’invio email tramite Mailtrap](#1042-verifica-dellinvio-email-tramite-mailtrap)
    * [10.4.3 Verifica scraping e metriche su Prometheus](#1043-verifica-scraping-e-metriche-su-prometheus)

* [11. Troubleshooting](#11-troubleshooting)

  * [11.1 Problemi comuni in fase di build](#111-problemi-comuni-in-fase-di-build)

    * [11.1.1 Mancanza di JDK/Maven (in build locale)](#1111-mancanza-di-jdkmaven-in-build-locale)
    * [11.1.2 Errori di build delle immagini Docker](#1112-errori-di-build-delle-immagini-docker)
    * [11.1.3 Immagini non disponibili nel cluster kind (mancato `kind load`)](#1113-immagini-non-disponibili-nel-cluster-kind-mancato-kind-load)

  * [11.2 Problemi comuni in fase di run](#112-problemi-comuni-in-fase-di-run)

    * [11.2.1 Pod in CrashLoopBackOff / ConfigMap-Secret mancanti](#1121-pod-in-crashloopbackoff--configmap-secret-mancanti)
    * [11.2.2 Il database non si avvia correttamente](#1122-il-database-non-si-avvia-correttamente)
    * [11.2.3 I servizi non riescono a connettersi a PostgreSQL](#1123-i-servizi-non-riescono-a-connettersi-a-postgresql)
    * [11.2.4 Problemi di connessione a Kafka](#1124-problemi-di-connessione-a-kafka)
    * [11.2.5 Errori di autenticazione verso OpenSky](#1125-errori-di-autenticazione-verso-opensky)
    * [11.2.6 Errori SMTP e mancato recapito delle email](#1126-errori-smtp-e-mancato-recapito-delle-email)
    * [11.2.7 Prometheus non mostra metriche / target DOWN](#1127-prometheus-non-mostra-metriche--target-down)

  * [11.3 Verifiche passo-passo per isolare gli errori](#113-verifiche-passo-passo-per-isolare-gli-errori)

    * [11.3.1 Verifica variabili d’ambiente (ConfigMap/Secret)](#1131-verifica-variabili-dambiente-configmapsecret)
    * [11.3.2 Verifica delle porte occupate (port-forward)](#1132-verifica-delle-porte-occupate-port-forward)
    * [11.3.3 Controllo dei log dei singoli Pod](#1133-controllo-dei-log-dei-singoli-pod)
    * [11.3.4 Verifica risorse Kubernetes (get/describe/events)](#1134-verifica-risorse-kubernetes-getdescribeevents)

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

  * [12.4 Scenario di configurazione e valutazione delle soglie](#124-scenario-di-configurazione-e-valutazione-delle-soglie)

    * [12.4.1 Creazione di un interesse con `highValue`/`lowValue`](#1241-creazione-di-un-interesse-con-highvalue-lowvalue)
    * [12.4.2 Generazione di un carico di voli che superi la soglia](#1242-generazione-di-un-carico-di-voli-che-superi-la-soglia)

  * [12.5 Scenario end-to-end della pipeline di notifica](#125-scenario-end-to-end-della-pipeline-di-notifica)

    * [12.5.1 Pubblicazione su `to-alert-system` e propagazione su `to-notifier`](#1251-pubblicazione-su-to-alert-system-e-propagazione-su-to-notifier)
    * [12.5.2 Verifica finale della ricezione email](#1252-verifica-finale-della-ricezione-email)

  * [11.6 Scenario con indisponibilità di OpenSky e Circuit Breaker attivo](#126-scenario-con-indisponibilità-di-opensky-e-circuit-breaker-attivo)

  * [12.7 Scenario di verifica del monitoring (metriche e label)](#127-scenario-di-verifica-del-monitoring-metriche-e-label)

    * [12.7.1 Verifica target UP in Prometheus](#1271-verifica-target-up-in-prometheus)
    * [12.7.2 Verifica COUNTER (incremento su workload)](#1272-verifica-counter-incremento-su-workload)
    * [12.7.3 Verifica GAUGE (valore istantaneo)](#1273-verifica-gauge-valore-istantaneo)
    * [12.7.4 Verifica label `service` e `node`](#1274-verifica-label-service-e-node)

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
* **generare notifiche asincrone via e‑mail** quando determinate condizioni sugli eventi di volo violano le soglie configurate;
* esporre **metriche tecniche e applicative** in formato Prometheus per attività di monitoring e troubleshooting.

L’architettura è pensata per essere **modulare**, **estendibile** e orientata a una chiara separazione dei confini di responsabilità: la gestione degli utenti, la raccolta dei dati di volo, la valutazione delle soglie e l’invio delle notifiche sono affidati a componenti distinti, orchestrati tramite un mix di comunicazioni sincrone (REST/gRPC) e asincrone (Kafka).

---

### 1.2 Microservizi coinvolti

L’applicazione è suddivisa in più microservizi Spring Boot **autonomi**, ciascuno responsabile di un sottoinsieme specifico del dominio applicativo e dotato di una sua logica, configurazione e ciclo di rilascio:

* lo *User Manager Service* governa il ciclo di vita degli utenti e la loro validazione;
* il *Data Collector Service* si occupa della gestione degli aeroporti di interesse, della registrazione degli interessi utente–aeroporto (comprensivi di soglie) e della raccolta periodica dei dati di volo;
* l’*Alert System Service* elabora gli eventi di volo raccolti, valuta le soglie configurate e individua i casi che richiedono una notifica;
* l’*Alert Notifier Service* riceve gli eventi di notifica e provvede all’invio delle e‑mail verso gli utenti finali;
* l’*API Gateway* centralizza l’esposizione delle API HTTP verso l’esterno e instrada le richieste verso i microservizi interni appropriati;
* il *Kafka Broker* fornisce l’infrastruttura di messaggistica per i flussi asincroni tra Data Collector, Alert System e Alert Notifier;
* *Prometheus* costituisce il sottosistema di monitoring per la raccolta e consultazione delle metriche esposte dai microservizi.

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

Il servizio integra inoltre **Spring Boot Actuator** e **Micrometer**, esponendo metriche in formato Prometheus tramite l’endpoint `/actuator/prometheus`.

#### 1.2.3 Alert System Service

L’**Alert System Service** è il componente incaricato di valutare le soglie configurate sugli interessi utente–aeroporto alla luce dei dati di volo effettivamente raccolti. Dal punto di vista architetturale:

* si comporta da **consumer Kafka** sul topic (ad esempio `to-alert-system`) su cui il Data Collector pubblica gli eventi di volo rilevanti;
* per ciascun evento ricevuto, applica la logica di confronto rispetto alle soglie *high_value* e *low_value* associate all’interesse corrispondente;
* identifica i casi in cui è necessario attivare una notifica (ad esempio ritardi superiori a una certa soglia configurata dall’utente);
* produce un nuovo messaggio su un secondo topic Kafka (ad esempio `to-notifier`), contenente tutte le informazioni necessarie all’invio dell’e‑mail.

In questo modo il servizio separa in modo netto la **logica di valutazione delle condizioni di alert** dalla raccolta dei dati e dall’invio effettivo delle notifiche, favorendo una maggiore manutenibilità e la possibilità di estendere le regole di alert in versioni successive.

Il servizio integra inoltre **Spring Boot Actuator** e **Micrometer**, esponendo metriche in formato Prometheus tramite l’endpoint `/actuator/prometheus`.

#### 1.2.4 Alert Notifier Service

L’**Alert Notifier Service** è responsabile dell’**invio delle notifiche e‑mail** verso gli utenti interessati.

Le sue responsabilità principali sono:

* consumare i messaggi dal topic Kafka dedicato alle notifiche (ad esempio `to-notifier`);
* trasformare il contenuto degli eventi in **e‑mail leggibili** (oggetto e corpo del messaggio) che riassumono la condizione di alert verificatasi;
* interagire con il server SMTP configurato (nel contesto corrente, Mailtrap) tramite il supporto **Spring Mail**;
* tracciare nei log l’esito delle notifiche, evidenziando eventuali errori di consegna.

Il servizio non espone API REST verso l’esterno, ma opera come componente di back-end guidato dagli eventi presenti nella coda Kafka.

Il servizio integra inoltre **Spring Boot Actuator** e **Micrometer**, esponendo metriche in formato Prometheus tramite l’endpoint `/actuator/prometheus`.

#### 1.2.5 API Gateway

L’**API Gateway** è implementato tramite **NGINX** e funge da **punto di ingresso unico** per il traffico HTTP verso il sistema. A livello logico:

* riceve le richieste in ingresso su una porta pubblica esposta dal container NGINX;
* instrada le richieste verso i microservizi interni pertinenti (principalmente User Manager e Data Collector), sulla base di regole di *routing* definite nel file di configurazione;
* consente di centralizzare alcuni aspetti trasversali, quali la gestione degli *path* di base, l’eventuale logging HTTP e la separazione tra traffico esterno e rete interna del cluster.

Questa componente permette di presentare verso l’esterno un **perimetro uniforme**, schermando i dettagli interni di deploy e degli indirizzi dei singoli microservizi.

#### 1.2.6 Kafka Broker

Il **Kafka Broker** fornisce l’infrastruttura di **messaggistica asincrona** alla base della pipeline di alerting. Nel contesto attuale:

* viene eseguito come componente dedicato, affiancato dai componenti di coordinamento necessari (ad esempio Zookeeper);
* espone i topic utilizzati dai microservizi applicativi, con particolare riferimento ai flussi `to-alert-system` e `to-notifier`;
* consente a Data Collector, Alert System e Alert Notifier di scambiarsi eventi in maniera decoupled, supportando l’elaborazione asincrona e una migliore resilienza rispetto a picchi di carico o temporanee indisponibilità.

#### 1.2.7 Prometheus

**Prometheus** è il componente adottato per il **monitoring** del sistema tramite metriche. In particolare:

* effettua lo *scraping* periodico degli endpoint `/actuator/prometheus` esposti dai microservizi strumentati;
* indicizza le serie temporali raccolte, rendendole interrogabili tramite **PromQL**;
* consente di osservare sia metriche **tecniche** (runtime, richieste, tempi di risposta) sia metriche **applicative** (chiamate a OpenSky, messaggi elaborati nella pipeline di alerting, invii e‑mail).

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
* `documentation/homework-3/written_report.pdf`: completa la descrizione del sistema introducendo il sottosistema di **monitoring** basato su **Prometheus**, la strumentazione tramite **Actuator/Micrometer** e gli aspetti di deployment su cluster, con particolare attenzione alla raccolta e consultazione delle metriche applicative.

Le tre relazioni sono pensate per essere lette in modo complementare: la prima fornisce il contesto e le fondamenta architetturali del sistema, la seconda introduce lo strato evolutivo relativo ad alerting e notifiche, la terza aggiunge osservabilità e monitoring tramite metriche.

#### 1.4.2 Diagrammi architetturali e di sequenza

All’interno di `documentation/homework-1/diagram_screenshots/`, `documentation/homework-2/diagram_screenshots/` e `documentation/homework-3/diagram_screenshots/` sono disponibili i **diagrammi architetturali** e i **diagrammi di sequenza** principali.

* Il set di diagrammi associato alla prima versione illustra l’architettura centrata su User Manager, Data Collector, PostgreSQL e OpenSky, con i flussi sincroni di registrazione utente, registrazione degli interessi e raccolta/interrogazione dei voli.
* Il set di diagrammi associato alla seconda versione rappresenta l’architettura estesa con Alert System, Alert Notifier, API Gateway, Kafka e server SMTP, oltre ai flussi aggiuntivi di configurazione e aggiornamento delle soglie, pipeline di notifica asincrona (Data Collector → Alert System → Alert Notifier → Email) e gestione delle failure verso OpenSky tramite Circuit Breaker.
* Il set di diagrammi associato alla terza versione evidenzia l’integrazione del sottosistema di monitoring con Prometheus, includendo la raccolta delle metriche applicative e lo scraping periodico degli endpoint esposti dai microservizi.

I diagrammi sono organizzati per facilitare il confronto tra le versioni e permettere di seguire, anche visivamente, l’evoluzione delle responsabilità tra microservizi e componenti infrastrutturali.

#### 1.4.3 Diagramma Entity–Relationship (ER)

Nelle sottocartelle `documentation/homework-1/diagram_screenshots/` e `documentation/homework-2/diagram_screenshots/` è presente un **diagramma Entity–Relationship (ER)** che rappresenta lo schema logico dei database.

* Il diagramma della prima versione mostra le entità `User` nel *User DB* e `Airport`, `UserAirportInterest` e `FlightRecord` nel *Data DB*, insieme a chiavi primarie, vincoli di unicità e relazioni fra i domini utente e aeroporti–voli.
* Il diagramma aggiornato della seconda versione evidenzia l’estensione di `UserAirportInterest` con gli attributi di soglia (`high_value`, `low_value`) e l’impatto di tali modifiche sulle query e sui processi di raccolta e valutazione dei dati.

Questi artefatti costituiscono il riferimento principale per collegare modello concettuale, modello logico e implementazione JPA/Flyway nei diversi microservizi.

#### 1.4.4 Evoluzione del sistema per release

La documentazione è organizzata in modo da riflettere esplicitamente il ciclo evolutivo del sistema:

* la **prima versione** descrive il perimetro funzionale di raccolta e interrogazione dei dati di volo, con due microservizi principali e un’integrazione sincrona verso OpenSky;
* la **seconda versione** introduce la gestione di soglie configurabili, la pipeline di alerting basata su Kafka, i servizi dedicati all’elaborazione degli alert e alla notifica e‑mail, nonché la mediazione centralizzata delle richieste tramite API Gateway;
* la **terza versione** aggiunge un sottosistema di monitoring basato su Prometheus, con metriche tecniche e applicative esposte dai microservizi per supportare analisi e troubleshooting.

Le relazioni tecniche, i diagrammi architetturali, i diagrammi di sequenza e i diagrammi ER sono pertanto da considerare come livelli successivi di una stessa documentazione: il materiale della prima versione fornisce la base concettuale e architetturale, quello della seconda ne rappresenta l’estensione funzionale per alerting e notifiche, mentre quello della terza consolida gli aspetti di osservabilità tramite metriche.

## 2. Repository Structure

### 2.1 Root layout della repository

Nella directory radice sono presenti le cartelle e i file necessari per gestire l’intero ciclo di vita della piattaforma, dalla **build delle immagini container** al **deploy su cluster Kubernetes** (con supporto opzionale per l’avvio locale tramite Docker Compose). A livello logico, la root contiene:

* le directory dei quattro microservizi applicativi (`user-manager-service/`, `data-collector-service/`, `alert-system-service/`, `alert-notifier-service/`), ciascuna con il proprio codice applicativo e la propria configurazione;
* la directory `docker/`, che raccoglie i file di orchestrazione e configurazione dell’infrastruttura in modalità containerizzata (PostgreSQL, Kafka, Zookeeper, Kafka UI, Prometheus, API Gateway e servizi applicativi), principalmente a supporto di esecuzioni locali;
* la directory `k8s/`, che contiene i manifest Kubernetes e le configurazioni **Kustomize** per il deploy dello stack completo (servizi applicativi, infrastruttura dati/messaging e sottosistema di monitoring);
* la directory `scripts/`, che include script di supporto per **build**, creazione/gestione di cluster **kind** e caricamento delle immagini nel cluster;
* la directory `documentation/`, che contiene le relazioni tecniche e i diagrammi di supporto;
* la directory `postman/`, che include le collection pronte all’uso per verificare rapidamente le API esposte dai servizi applicativi;
* i file di supporto generali, come ad esempio il `README.md` (questo documento), il `pom.xml` di aggregazione Maven e gli eventuali file di configurazione per il versionamento.

Questa impostazione consente a chiunque acceda per la prima volta al repository di individuare rapidamente i microservizi, i manifest di deploy, le risorse infrastrutturali e la documentazione tecnica.

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

In questa release, i microservizi **Data Collector**, **Alert System** e **Alert Notifier** integrano inoltre **Spring Boot Actuator** e **Micrometer** con registry Prometheus, esponendo l’endpoint `*/actuator/prometheus` per la raccolta delle metriche applicative.

#### 2.2.2 Infrastruttura applicativa (API Gateway)

L’**API Gateway** è realizzato tramite **NGINX** con configurazione esplicita delle regole di reverse proxy verso i servizi interni.

* La configurazione NGINX per l’esecuzione containerizzata è mantenuta in `docker/nginx/nginx.conf`.
* La configurazione NGINX per l’esecuzione su Kubernetes è gestita come **ConfigMap** in `k8s/apps/api-gateway/01-nginx-configmap.yaml`, affiancata dai manifest di **Deployment** e **Service** nella stessa directory (`k8s/apps/api-gateway/`).

Questa separazione consente di mantenere coerente la logica di routing (path e upstream) nei diversi ambienti di esecuzione, adattando soltanto i dettagli infrastrutturali (service discovery e porte esposte).

#### 2.2.3 Infrastruttura di messaging (Kafka, Zookeeper, Kafka UI)

Il sottosistema di messaging è basato su **Kafka** (con **Zookeeper** per il coordinamento) ed espone una **Kafka UI** per l’osservabilità dei topic e dei messaggi.

* **`k8s/infra/kafka/`**
  Contiene i manifest Kubernetes per:

  * **Zookeeper** (Deployment/Service);
  * **Kafka broker** (Deployment/Service);
  * **Kafka UI** (Deployment/Service).

  La directory include inoltre il relativo `kustomization.yaml`, per l’inclusione modulare nello stack.

* **`docker/docker-compose.yml`**
  Definisce, in modalità containerizzata, gli stessi componenti di messaging (Kafka, Zookeeper, Kafka UI) a supporto di avvii locali.

#### 2.2.4 Infrastruttura di monitoring (Prometheus)

Il monitoring è basato su **Prometheus**, configurato per lo scraping delle metriche esposte dai microservizi strumentati.

* **`k8s/observability/prometheus/`**
  Contiene i manifest Kubernetes del sottosistema Prometheus, includendo:

  * **ServiceAccount** e regole **RBAC** (permessi minimi necessari per la service discovery);
  * **ConfigMap** con `prometheus.yml`, che definisce gli *scrape_configs* verso gli endpoint `*/actuator/prometheus` dei servizi applicativi;
  * **Deployment** e **Service** per l’esposizione dell’interfaccia Prometheus.

  La directory include inoltre il relativo `kustomization.yaml`, per l’inclusione modulare nello stack complessivo.

* **`docker/docker-compose.yml`**
  Include anche un servizio Prometheus per l’esecuzione containerizzata locale.

* **`docker/prometheus/prometheus.yaml`**
  Definisce la configurazione di Prometheus utilizzata in modalità Docker Compose (in particolare gli *scrape_configs*), e viene **montata** dal servizio Prometheus nel `docker-compose.yml` come file di configurazione, in modo da mantenere lo scraping allineato agli endpoint `*/actuator/prometheus` dei servizi strumentati.

#### 2.2.5 Manifest Kubernetes (`k8s/`) e Kustomize

La directory **`k8s/`** rappresenta il punto centrale per il deploy su Kubernetes ed è strutturata per componenti, secondo un approccio dichiarativo e componibile tramite **Kustomize**:

* **`k8s/00-namespace.yaml`**
  Definisce il namespace dedicato (`dsbd`) e le label applicative.

* **`k8s/config/`**
  Contiene risorse di configurazione condivise (ConfigMap/Secret) e il relativo `kustomization.yaml`.

* **`k8s/infra/`**
  Include i componenti infrastrutturali necessari allo stack:

  * `k8s/infra/postgres/` (ConfigMap di init, PVC, Deployment, Service);
  * `k8s/infra/kafka/` (Zookeeper, Kafka, Kafka UI).

* **`k8s/apps/`**
  Contiene i manifest applicativi per:

  * `user-manager-service/`, `data-collector-service/`, `alert-system-service/`, `alert-notifier-service/` (Deployment/Service);
  * `api-gateway/` (ConfigMap NGINX, Deployment, Service).

* **`k8s/stack/`**
  Aggrega, tramite `kustomization.yaml`, tutte le risorse applicative e infrastrutturali (config, database, messaging, microservizi, gateway) in un unico entry-point di deploy.

* **`k8s/observability/`**
  Contiene i componenti di osservabilità; in particolare `observability/prometheus/` per il monitoring.

* **`k8s/kustomization.yaml`**
  Definisce la composizione complessiva includendo lo stack (`k8s/stack/`) e l’osservabilità (`k8s/observability/prometheus/`).

* **`k8s/kind/`**
  Contiene la configurazione del cluster **kind** (`kind-cluster.yaml`) utilizzata dagli script di bootstrap dell’ambiente locale Kubernetes.

Questa organizzazione consente di mantenere separati i concern applicativi e infrastrutturali, favorendo riuso, portabilità e riproducibilità del deploy.

#### 2.2.6 Script di supporto (build, kind, deploy)

* **`scripts/`**
  Contiene script di supporto per automatizzare le operazioni ricorrenti. In particolare:

  * `scripts/build-images.sh` e `scripts/build-images.ps1` eseguono la build delle immagini Docker dei microservizi con tag parametrico;
  * `scripts/kind/create-cluster.*` e `scripts/kind/delete-cluster.*` creano ed eliminano un cluster **kind** locale, applicando il namespace dedicato;
  * `scripts/kind/load-images.*` carica nel cluster kind le immagini locali buildate (utile in assenza di registry);
  * `scripts/kind/dev-loop.*` implementa un *development loop* (build → load → rollout restart) per rendere effettive le nuove immagini su Kubernetes anche in presenza di tag fissi.

La presenza di versioni **`.sh`** e **`.ps1`** consente l’utilizzo su ambienti Linux/macOS e Windows.

#### 2.2.7 Documentazione e diagrammi

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

  * **`documentation/homework-3/`**
    Raccoglie gli artefatti relativi alla versione che introduce il deploy su **Kubernetes** e il sottosistema di monitoring:

    * `documentation/homework-3/written_report.pdf`, che descrive l’estensione architetturale con manifest Kubernetes/Kustomize, l’esecuzione su cluster locale (kind) e l’integrazione di **Prometheus** per la raccolta delle metriche;
    * `documentation/homework-3/diagram_screenshots/`, che contiene i diagrammi aggiornati (architettura su Kubernetes con Prometheus e diagrammi di sequenza relativi a pipeline di notifica e scraping delle metriche).

La struttura versionata della directory `documentation/` consente di mantenere separati, ma facilmente confrontabili, gli artefatti relativi alle diverse evoluzioni del sistema.

#### 2.2.8 Postman collections

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

  * **`postman/homework-3/`**
    Include le collection di test allineate alla versione che introduce il deploy su Kubernetes. In questa versione, l’esposizione delle API applicative rimane coerente con la release precedente; pertanto le collection sono mantenute (ed eventualmente riutilizzate) per pilotare gli stessi scenari funzionali attraverso gli endpoint pubblicati dal gateway.

Le collection possono essere importate direttamente in Postman per eseguire in modo controllato le richieste preconfigurate verso i microservizi, utilizzando gli endpoint esposti (direttamente o tramite API Gateway) e i parametri di configurazione descritti nel presente README.

---

### 2.3 File chiave per build & deploy

Alcuni file del repository rivestono un ruolo centrale nelle procedure di build e deploy e meritano una menzione esplicita.

* **`k8s/kustomization.yaml`** e **`k8s/stack/kustomization.yaml`**
  Definiscono, tramite **Kustomize**, la composizione delle risorse Kubernetes necessarie al deploy dello stack. In particolare, `k8s/stack/` aggrega configurazione, database, messaging, microservizi e gateway, mentre `k8s/kustomization.yaml` include anche il sottosistema Prometheus.

* **`k8s/config/01-configmap.yaml`** e **`k8s/config/02-secret.yaml`**
  Centralizzano la configurazione applicativa e le credenziali (in forma di Secret) condivise tra più componenti, evitando duplicazioni nei singoli manifest.

* **`k8s/infra/postgres/01-initdb-configmap.yaml`** e **Script SQL di inizializzazione**
  Definiscono l’inizializzazione del database in Kubernetes (creazione dei database logici) e assicurano che l’ambiente dati sia predisposto prima dell’avvio dei microservizi.

* **`k8s/observability/prometheus/03-configmap.yaml`**
  Contiene la configurazione `prometheus.yml` per lo scraping delle metriche applicative esposte dai servizi strumentati tramite endpoint `*/actuator/prometheus`.

* **`scripts/build-images.sh` / `scripts/build-images.ps1`**
  Automatizzano la build delle immagini Docker dei microservizi, producendo tag coerenti (es. `dsbd/<service>:<tag>`) riutilizzabili nelle fasi di caricamento e deploy.

* **`scripts/kind/*`** e **`k8s/kind/kind-cluster.yaml`**
  Supportano la creazione e gestione del cluster **kind** locale e il caricamento delle immagini nel cluster, rendendo riproducibile l’ambiente Kubernetes senza dipendere da un registry esterno.

* **`docker/docker-compose.yml`**
  Definisce l’orchestrazione completa dell’ambiente di esecuzione containerizzato. In questo file sono specificati:

  * i servizi Docker (`postgres`, `user-manager-service`, `data-collector-service`, `alert-system-service`, `alert-notifier-service`, `kafka`, `zookeeper`, `kafka-ui`, `prometheus`, `api-gateway`);
  * le immagini da utilizzare o generare, i *build context* e i `Dockerfile` associati;
  * i volumi per la persistenza dei dati PostgreSQL e per eventuali mount di configurazione;
  * le reti interne utilizzate per la comunicazione tra i container;
  * le dipendenze di avvio tra i servizi, in modo che il database e il broker Kafka siano disponibili prima dei microservizi applicativi.

* **`docker/env/postgres.env`**
  Contiene le variabili d’ambiente utilizzate per configurare il servizio PostgreSQL (nome utente, password, nomi dei database logici), centralizzando la definizione dei parametri critici del database ed evitando la duplicazione di configurazioni nei vari servizi.

* **`docker/env/services.env`**
  Raccoglie le variabili d’ambiente comuni ai microservizi, tra cui le informazioni di connessione al database, le credenziali per l’accesso a OpenSky, i parametri di configurazione del broker Kafka e le impostazioni per l’invio delle email. In questo modo è possibile gestire in un unico punto i valori che devono essere condivisi tra più container, rendendo agevole l’adattamento del sistema a diversi ambienti.

* **`docker/nginx/nginx.conf`** e **`k8s/apps/api-gateway/01-nginx-configmap.yaml`**
  Descrivono la configurazione del reverse proxy NGINX utilizzato come API Gateway nei diversi ambienti (containerizzato e Kubernetes), definendo i *virtual server*, le regole di routing e le porte esposte.

* **`user-manager-service/Dockerfile`**, **`data-collector-service/Dockerfile`**, **`alert-system-service/Dockerfile`** e **`alert-notifier-service/Dockerfile`**
  Descrivono il processo di build delle immagini Docker per i quattro microservizi applicativi. Ogni Dockerfile è strutturato come *multi-stage build* per separare la fase di compilazione Maven dalla fase di runtime, producendo un’immagine finale più leggera basata su una immagine JDK/JRE minimal.

* **`pom.xml`** (root) e **`*/pom.xml`** (microservizi)
  Il `pom.xml` in root aggrega i moduli dei microservizi e consente build coordinate, mentre i `pom.xml` dei singoli servizi definiscono dipendenze, plugin e configurazioni necessarie alla build Maven (utile sia per build locali sia per integrazione in pipeline CI/CD).

Nel complesso, questi file costituiscono il nucleo operativo necessario per costruire ed eseguire l’intero sistema in modo riproducibile e controllato in diversi contesti di esecuzione.

## 3. Prerequisites

### 3.1 Requisiti hardware e sistema operativo

Il sistema è progettato per essere eseguito su una macchina in grado di sostenere l’esecuzione contemporanea di più microservizi, componenti infrastrutturali e relativi container. Sono raccomandate le seguenti caratteristiche minime:

* CPU: almeno **2 core** fisici (4 thread consigliati) per evitare contenention eccessiva fra i container;
* RAM: almeno **8 GB** di memoria, con **16 GB** consigliati per lavorare in modo agevole con Docker, IDE e altri strumenti aperti in parallelo;
* Storage: almeno **5–10 GB** di spazio libero dedicato ai container Docker, alle immagini e ai log applicativi.

Per l’esecuzione su **cluster Kubernetes locale (kind)** e per l’avvio di componenti aggiuntivi (es. **Prometheus**), è consigliato disporre di almeno **16 GB** di RAM, soprattutto su Windows/macOS dove Docker Desktop e la virtualizzazione hanno un overhead non trascurabile.

Per quanto riguarda il sistema operativo, il progetto è stato pensato per ambienti moderni e supportati:

* **Linux** (distribuzioni recenti come Ubuntu, Debian, Fedora, ecc.);
* **macOS** (versioni con supporto ufficiale per Docker Desktop);
* **Windows 10/11** a 64 bit, preferibilmente con **Docker Desktop** installato.

È importante che il sistema operativo consenta l’esecuzione di Docker in modalità **Linux container** e che l’utente disponga dei permessi necessari per avviare e gestire i container.

---

### 3.2 Software necessario

Il funzionamento completo della piattaforma richiede un insieme di strumenti software. Alcuni sono **obbligatori** per l’esecuzione standard su cluster Kubernetes locale (basato su *kind*), altri sono **opzionali ma raccomandati** per la build e il run locale dei servizi.

#### 3.2.1 Docker

**Docker** è il requisito principale per l’esecuzione containerizzata dell’ambiente (in particolare come runtime per i nodi del cluster *kind* e per la build delle immagini applicative). Si raccomanda l’installazione di una versione recente, ad esempio:

* Docker Engine / Docker Desktop **20.x** o superiore.

La presenza di Docker consente di:

* avviare l’istanza PostgreSQL con la configurazione prevista;
* eseguire i microservizi all’interno di container isolati;
* riprodurre con facilità l’ambiente di esecuzione su macchine differenti.

#### 3.2.2 kubectl

**kubectl** è il client a riga di comando utilizzato per interagire con il cluster Kubernetes (creato tramite *kind*). È necessario per:

* applicare i manifest (`kubectl apply` / `kubectl apply -k`);
* verificare lo stato delle risorse (*pods*, *deployments*, *services*, ecc.);
* consultare log ed eventi del cluster per attività di diagnostica.

È raccomandato utilizzare una versione di `kubectl` compatibile con la versione di Kubernetes fornita dal cluster *kind*.

#### 3.2.3 kind

**kind** (*Kubernetes IN Docker*) consente di creare un cluster Kubernetes locale utilizzando container Docker come nodi. Nel progetto viene impiegato per:

* predisporre un ambiente Kubernetes riproducibile su una singola macchina;
* eseguire il deploy della piattaforma tramite i manifest presenti in repository;
* validare end-to-end il comportamento del sistema in un contesto coerente con un’infrastruttura Kubernetes.

#### 3.2.4 Kustomize (opzionale: alternativa a `kubectl apply -k`)

**Kustomize** è uno strumento per la gestione di manifest Kubernetes tramite overlay e composizione (*base* / *overlays*). Nel progetto è utilizzabile per:

* applicare in modo dichiarativo un insieme di risorse Kubernetes coerenti tra loro;
* mantenere separati manifest “base” e personalizzazioni (ad es. override di immagini, variabili, replica count);
* semplificare l’applicazione dello stack tramite un singolo comando.

In molti ambienti, Kustomize è già integrato in `kubectl` tramite l’opzione `-k`.

#### 3.2.5 JDK (per build/esecuzione locale senza Docker)

Per chi desidera eseguire i microservizi localmente (senza containerizzarli), è necessario disporre di un **Java Development Kit (JDK)** compatibile con la versione di Spring Boot utilizzata. Si raccomanda:

* **JDK 21** (LTS) oppure una versione **JDK 17+** compatibile.

È importante che il comando `java` punti al JDK e non a un JRE obsoleto, in modo da garantire il corretto funzionamento dei plugin Maven e delle applicazioni Spring Boot.

#### 3.2.6 Maven (per build/esecuzione locale senza Docker)

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

L’integrazione con OpenSky è basata su **OAuth2 Client Credentials**, che richiede la definizione di una *client application* sul lato OpenSky con relativa coppia **client id / client secret**. Una volta ottenute tali credenziali, devono essere configurate come variabili d’ambiente nei canali di deploy previsti:

* in modalità containerizzata, valorizzando i file `env` dedicati (ad esempio `docker/env/services.env`);
* in modalità Kubernetes, valorizzando `k8s/config/01-configmap.yaml` per i parametri **non sensibili** (ad es. endpoint di authorization e base URL delle API) e `k8s/config/02-secret.yaml` per i valori **sensibili** (client id e client secret). Tali risorse vengono poi importate nei Pod tramite `envFrom` nei manifest di Deployment.

In questo modo il `Data Collector Service` può:

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
4. Mappare tali valori sulle variabili d’ambiente previste dal sistema. In modalità containerizzata, i parametri possono essere valorizzati nei file `env` (ad esempio `docker/env/services.env`). In modalità Kubernetes, i valori **non sensibili** (host, porta, flag TLS, mittente) sono tipicamente collocati in `k8s/config/01-configmap.yaml`, mentre le credenziali **sensibili** (username e password SMTP) sono collocate in `k8s/config/02-secret.yaml` e importate nei Pod tramite `envFrom` nei manifest di Deployment. In questo modo il microservizio **Alert Notifier** può stabilire una connessione autenticata al server SMTP al momento dell’invio di ogni notifica.

È buona pratica utilizzare una inbox dedicata esclusivamente a questo sistema, così da poter monitorare facilmente il flusso di email generate dalle condizioni di alert e distinguere tali messaggi da eventuali altri progetti che utilizzano lo stesso account Mailtrap.

---

### 3.4 Verifica installazione dei prerequisiti

Prima di procedere con la build e il deploy, è opportuno verificare che tutti i prerequisiti software siano installati e correttamente configurati.

Per controllare la versione di Docker:

```bash
docker --version
```

Per verificare l’installazione di `kubectl`:

```bash
kubectl version --client
```

Per verificare l’installazione di `kind`:

```bash
kind version
```

Per verificare la disponibilità di Kustomize (se installato come binario dedicato):

```bash
kustomize version
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

È opportuno infine validare che le variabili d’ambiente relative a OpenSky (client id, client secret, endpoint di authorization e API base URL) e al provider SMTP utilizzato per il testing (host, porta, credenziali e parametri di sicurezza della inbox Mailtrap) siano configurate correttamente nel contesto in cui verranno applicati i manifest Kubernetes (ad es. tramite `kubectl apply` / `kubectl apply -k`) o in cui verranno eseguiti i microservizi in locale.

## 4. Configuration

### 4.1 Strategie di configurazione (env-based configuration)

La configurazione del sistema è basata su un approccio **env-based**, in cui i parametri sensibili o dipendenti dall’ambiente (host, porte, credenziali, URL di servizi esterni) vengono veicolati tramite **variabili d’ambiente** e risorse di configurazione, mentre i file di configurazione Spring Boot (`application-*.yml`) si limitano a referenziarli. Questa strategia consente di:

* separare in modo netto il **codice applicativo** dai **valori di configurazione**;
* utilizzare la stessa immagine container in ambienti diversi (sviluppo, test, produzione) variando soltanto i valori di runtime;
* evitare l’hardcoding di credenziali e URL all’interno dei sorgenti.

A livello infrastrutturale, l’esecuzione su Kubernetes utilizza risorse dedicate per la configurazione: una **ConfigMap** per i parametri non sensibili e un **Secret** per le credenziali. Tali risorse vengono poi iniettate nei Pod dei microservizi tramite `envFrom` nei rispettivi manifest di Deployment. Sul lato applicativo, i microservizi leggono le variabili tramite le property Spring (ad esempio `${DB_HOST}`, `${OPENSKY_CLIENT_ID}`, ecc.).

---

### 4.2 ConfigMap e Secret (Kubernetes)

La configurazione runtime in ambiente Kubernetes è centralizzata nella cartella `k8s/config/` e viene applicata allo stack tramite Kustomize (direttamente o per mezzo dell’overlay/stack principale). L’obiettivo è garantire **coerenza**, **riutilizzabilità** e **separazione** tra dati non sensibili e credenziali.

#### 4.2.1 ConfigMap: variabili non sensibili

La ConfigMap principale è definita in **`k8s/config/01-configmap.yaml`** (nome risorsa: `dsbd-config`, namespace: `dsbd`). Contiene variabili non sensibili, tra cui:

* parametri di connessione logica al database (**host**, **porta**, **username**, nomi dei database logici);
* bootstrap servers del broker Kafka;
* URL delle OpenSky Network API (authorization endpoint e API base);
* parametri non sensibili del sistema SMTP (host, porta, flag di autenticazione/TLS) e mittente logico (`MAIL_FROM`).

Nei Deployment dei microservizi, l’iniezione della ConfigMap avviene tramite:

* `envFrom.configMapRef.name: dsbd-config`

in modo da rendere disponibili le chiavi come variabili d’ambiente, senza duplicazione per singolo servizio.

#### 4.2.2 Secret: credenziali e dati sensibili

Le credenziali sono definite nel Secret **`k8s/config/02-secret.yaml`** (nome risorsa: `dsbd-secrets`, namespace: `dsbd`). Il Secret include tipicamente:

* `DB_PASSWORD` (password del database);
* `OPENSKY_CLIENT_ID` e `OPENSKY_CLIENT_SECRET` (OAuth2 *client credentials*);
* `MAIL_USERNAME` e `MAIL_PASSWORD` (credenziali SMTP).

Analogamente alla ConfigMap, i microservizi importano le variabili tramite:

* `envFrom.secretRef.name: dsbd-secrets`

Per i componenti infrastrutturali (ad esempio PostgreSQL), il Secret può essere referenziato anche puntualmente via `valueFrom.secretKeyRef` (ad esempio `POSTGRES_PASSWORD` derivata da `DB_PASSWORD`).

#### 4.2.3 Gestione sicura delle credenziali (placeholder vs valori reali)

Per un utilizzo corretto e sicuro, è opportuno distinguere tra **valori di esempio** e **valori reali**:

* i manifest `k8s/config/02-secret.yaml` dovrebbero contenere, idealmente, **placeholder** (ad esempio `your_client_id`, `your_client_secret`) e i valori effettivi dovrebbero essere applicati in modo controllato (ad esempio tramite file non versionati, CI/CD, secret manager, o strumenti come Sealed Secrets/External Secrets);
* in esecuzione locale e/o in contesti di sviluppo controllati, è possibile utilizzare valori reali, purché venga garantito che non vengano committati su repository remoti.

Nel progetto è presente anche il file **`docker/env/services.env`**, che documenta lo stesso insieme di variabili in formato `.env`. Qualora si operi su entrambe le modalità (esecuzione containerizzata “classica” e deploy su Kubernetes), è essenziale mantenere **coerenti** le coppie di valori tra `docker/env/services.env` e i manifest `k8s/config/*`.

---

### 4.3 Configurazione del database PostgreSQL

La configurazione del database è incapsulata nella combinazione di:

* variabili d’ambiente fornite da **ConfigMap/Secret**;
* script SQL di inizializzazione;
* configurazioni Spring Boot nei microservizi.

#### 4.3.1 Parametri di connessione (host, port, user, password)

I parametri di connessione fondamentali sono:

* **host** del database (ad esempio `postgres` come nome del Service nel cluster);
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

La separazione tra **User DB** e **Data DB** è realizzata attraverso due database logici distinti (`userdb` e `datadb`) all’interno della stessa istanza PostgreSQL. In ambiente Kubernetes, la creazione dei database avviene al bootstrap dell’istanza tramite la ConfigMap **`k8s/infra/postgres/01-initdb-configmap.yaml`**, montata in `/docker-entrypoint-initdb.d`.

Ogni microservizio si connette esclusivamente al proprio database logico e gestisce lo schema tramite **Flyway**, con migrazioni collocate in:

* `user-manager-service/src/main/resources/db/migration/` per lo schema utenti;
* `data-collector-service/src/main/resources/db/migration/` per lo schema aeroporti–interessi–voli.

Questo approccio garantisce un isolamento netto dei domain model, pur mantenendo una infrastruttura di persistenza condivisa e facilmente gestibile.

---

### 4.4 Configurazione delle credenziali OpenSky

Le credenziali per l’accesso alle **OpenSky Network API** sono fornite tramite variabili d’ambiente, in modo da evitare la loro inclusione diretta nei sorgenti o nei file di configurazione versionati.

#### 4.4.1 Variabili d’ambiente per client id e secret

Gli elementi minimi necessari per l’autenticazione OAuth2 *Client Credentials* sono:

* `OPENSKY_AUTH_URL`: URL del token endpoint;
* `OPENSKY_API_URL`: URL base delle API di volo;
* `OPENSKY_CLIENT_ID`: identificativo della client application registrata presso OpenSky;
* `OPENSKY_CLIENT_SECRET`: secret associato alla client application.

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

All’interno del repository, i file di esempio e i manifest Kubernetes dovrebbero contenere **placeholder** e non valori reali. Le credenziali effettive vanno:

* inserite localmente nei file non versionati e/o applicate tramite pipeline di deploy;
* oppure gestite tramite secret manager e meccanismi di injection controllati.

È opportuno garantire che:

* i file contenenti valori reali non vengano committati nel repository remoto;
* l’accesso a tali file sia limitato agli operatori che necessitano di eseguire il sistema.

---

### 4.5 Configurazione del sistema di posta (Mailtrap SMTP)

L’invio delle notifiche email è gestito dal microservizio **Alert Notifier**, che si integra con un server SMTP esterno. In ambiente di sviluppo e test viene utilizzato **Mailtrap**, che fornisce una *sandbox* SMTP dedicata alla verifica delle email senza recapito verso destinatari reali. Anche in questo caso la configurazione è interamente **env-based**: le credenziali e gli endpoint del server SMTP non sono codificati nel codice sorgente, ma vengono forniti tramite variabili d’ambiente e risorse Kubernetes.

#### 4.5.1 Variabili d’ambiente `MAIL_*` per l’Alert Notifier

Il microservizio *Alert Notifier* legge i parametri necessari alla configurazione del `JavaMailSender` da un insieme di variabili d’ambiente con prefisso `MAIL_`, definite in Kubernetes tra **ConfigMap** (`k8s/config/01-configmap.yaml`) e **Secret** (`k8s/config/02-secret.yaml`) e iniettate nel Pod tramite `envFrom`. Le variabili principali sono:

* `MAIL_HOST`: hostname del server SMTP;
* `MAIL_PORT`: porta di ascolto del server SMTP (il valore dipende dalla configurazione indicata dal provider);
* `MAIL_USERNAME`: username dell’account SMTP configurato su Mailtrap;
* `MAIL_PASSWORD`: password associata all’account SMTP;
* `MAIL_SMTP_AUTH`: flag booleano che abilita l’autenticazione SMTP (`true`/`false`);
* `MAIL_SMTP_STARTTLS_ENABLE`: flag booleano che abilita l’estensione **STARTTLS** per la cifratura del canale;
* `MAIL_FROM`: mittente logico utilizzato dall’applicazione per le notifiche.

Queste variabili vengono lette dalla configurazione Spring (`spring.mail.*` e `app.alerts.mail.from`) e consentono di adattare il comportamento dell’SMTP senza modificare i sorgenti.

#### 4.5.2 Considerazioni su mittente, autenticazione e TLS

Per garantire un comportamento coerente e sicuro del sistema di notifica è opportuno:

* utilizzare un **mittente dedicato** alle notifiche applicative (ad esempio `no-reply@alerts.example.com`), evitando account personali;
* mantenere **attiva l’autenticazione SMTP**, impostando `MAIL_SMTP_AUTH=true` e conservando le credenziali in secret o sistemi equivalenti;
* abilitare **STARTTLS** quando supportato dal provider (`MAIL_SMTP_STARTTLS_ENABLE=true`), così da proteggere le credenziali e il contenuto delle notifiche durante il transito;
* utilizzare password robuste o, ove disponibili, **token di accesso** specifici per l’SMTP, evitando il riuso di credenziali generiche.

Nel contesto di test con Mailtrap, questi parametri consentono di simulare fedelmente le condizioni di produzione, mantenendo al contempo il recapito confinato alla *sandbox* del provider.

---

### 4.6 Profili e configurazioni Spring Boot

La configurazione dei microservizi è organizzata tramite i file di property Spring Boot (`application.yml` e, ove previsto, eventuali file di profilo), integrati con variabili d’ambiente fornite dall’infrastruttura (**Kubernetes**, tramite **ConfigMap** e **Secret** applicati nei manifest). In assenza di profili espliciti, Spring Boot utilizza il **profilo di default**, che nel progetto è pensato per coprire i principali scenari di esecuzione, demandando ai parametri env-based la specializzazione per i singoli ambienti.

#### 4.6.1 Uso del profilo di default e overriding tramite variabili d’ambiente

Il profilo di default di Spring Boot è sufficiente per la maggior parte degli scenari supportati dal sistema. In particolare:

* i parametri relativi al **datasource** (URL JDBC, utente, password) sono definiti nei file di configurazione applicativa (`application.yml` per l’esecuzione locale, `application-docker.yml` per l’esecuzione containerizzata) in forma generica, assumendo `localhost` e la porta standard `5432` come configurazione di base per l’esecuzione locale e delegando a variabili d’ambiente la risoluzione degli host/logical name in ambiente cluster;
* i parametri relativi ai **servizi esterni** (endpoint OpenSky, credenziali OAuth2, configurazione gRPC, integrazione con Kafka e sistema di posta) sono referenziati tramite placeholder e risolti al runtime utilizzando le variabili d’ambiente (`OPENSKY_AUTH_URL`, `OPENSKY_API_URL`, `OPENSKY_CLIENT_ID`, `OPENSKY_CLIENT_SECRET`, `KAFKA_BOOTSTRAP_SERVERS`, `MAIL_*`, ecc.).

In questo modello, i file di configurazione Spring fungono da **singola sorgente di verità** per la struttura della configurazione applicativa, mentre gli aspetti ambiente‑specifici (host, credenziali, URL esterni, porte) sono demandati alle variabili d’ambiente illustrate nelle sezioni precedenti.

In ambiente Kubernetes, i microservizi attivano il profilo **`docker`** tramite `SPRING_PROFILES_ACTIVE=docker` nei rispettivi manifest di Deployment, così da utilizzare le configurazioni `application-docker.yml` (datasource su `DB_HOST=postgres`, bootstrap Kafka, indirizzi gRPC interni al cluster, ecc.) con override via ConfigMap/Secret.

Qualora in futuro si rendesse necessario introdurre una differenziazione più marcata tra ambienti (ad esempio **sviluppo**, **test**, **produzione**), è possibile estendere il modello attuale definendo profili Spring Boot dedicati, ad esempio:

* `application-dev.yml` per configurazioni specifiche di sviluppo (logging più verboso, feature flag, parametri di scheduler meno aggressivi);
* `application-prod.yml` per parametri più conservativi, time‑out più stringenti, livelli di log più restrittivi.

L’attivazione di tali profili può avvenire tramite:

* variabile d’ambiente `SPRING_PROFILES_ACTIVE` (ad esempio `SPRING_PROFILES_ACTIVE=prod`);
* oppure parametro da riga di comando, ad esempio:

  ```bash
  java -jar data-collector-service.jar --spring.profiles.active=prod
  ```

#### 4.6.2 Configurazione del Circuit Breaker Resilience4j verso OpenSky

Il `Data Collector Service` integra un **Circuit Breaker** (Resilience4j) per proteggere le invocazioni verso le OpenSky Network API in caso di errori ripetuti, time‑out o indisponibilità temporanea del provider esterno.

La configurazione è definita nella sezione `resilience4j.circuitbreaker` del file `data-collector-service/src/main/resources/application.yml`. Un estratto esemplificativo è:

```yaml
resilience4j:
  circuitbreaker:
    instances:
      opensky:
        slidingWindowType: COUNT_BASED
        slidingWindowSize: 10
        failureRateThreshold: 50
        waitDurationInOpenState: 30s
        permittedNumberOfCallsInHalfOpenState: 3
        automaticTransitionFromOpenToHalfOpenEnabled: true
```

Il client che invoca le OpenSky Network API viene decorato con il `CircuitBreaker` identificato dal nome `opensky`. I parametri configurati definiscono:

* la dimensione della **finestra di osservazione** (`slidingWindowSize`);
* la **soglia di failure** oltre la quale il circuito passa allo stato *open* (`failureRateThreshold`);
* la durata di permanenza nello stato *open* prima del tentativo di ritorno allo stato *half-open* (`waitDurationInOpenState`);
* il numero di chiamate consentite nello stato *half-open* (`permittedNumberOfCallsInHalfOpenState`) e l’abilitazione della transizione automatica da *open* a *half-open*.

In presenza di errori ripetuti o di indisponibilità temporanea del servizio OpenSky, il Circuit Breaker evita di saturare l’endpoint esterno con richieste fallimentari, proteggendo il microservizio chiamante e contribuendo alla stabilità complessiva del sistema.

---

### 4.7 Configurazione del monitoring (Actuator/Micrometer/Prometheus)

Il sistema integra un livello di **osservabilità white-box** basato su Spring Boot Actuator e Micrometer, esportando metriche in formato Prometheus. In ambiente Kubernetes, un’istanza Prometheus dedicata effettua lo scraping degli endpoint dei microservizi e rende disponibili le metriche per analisi e troubleshooting.

#### 4.7.1 Esposizione endpoint `/actuator/prometheus`

Per i microservizi monitorati, l’esposizione delle metriche avviene tramite:

* abilitazione di Actuator nel servizio;
* esposizione degli endpoint `health`, `info` e `prometheus` (configurazione in `application.yml` e/o override via variabili d’ambiente nei manifest Kubernetes);
* endpoint HTTP `/actuator/prometheus`, utilizzato da Prometheus come `metrics_path`.

Nei Deployment Kubernetes, per robustezza vengono esplicitate le variabili di abilitazione delle metriche Prometheus (ad esempio `MANAGEMENT_ENDPOINTS_WEB_EXPOSURE_INCLUDE=health,info,prometheus` e flag di export Prometheus) e vengono configurate probe di health su `/actuator/health`.

#### 4.7.2 Convenzioni di naming e labeling delle metriche

Le metriche esportate includono sia metriche **tecniche** (JVM, HTTP server, thread, pool, ecc.) sia metriche **applicative** (strumentazione specifica dei componenti chiave). Per rendere le serie temporali confrontabili e filtrabili in modo affidabile, sono adottate convenzioni di labeling coerenti:

* `service`: nome logico del microservizio, valorizzato tramite `SERVICE_NAME` (con fallback su `${spring.application.name}`);
* `node`: nome del nodo Kubernetes su cui il Pod è schedulato, valorizzato tramite `NODE_NAME` (downward API: `spec.nodeName`).

Questi tag sono definiti in `management.metrics.tags.*` e permettono di correlare le metriche tra servizi e nodi, facilitando analisi comparative e diagnosi di colli di bottiglia o anomalie di runtime.

## 5. Build Instructions

### 5.1 Build immagini Docker (modalità raccomandata)

La modalità di build raccomandata prevede l’utilizzo di **Docker** per costruire le immagini dei microservizi applicativi. Le immagini risultanti vengono poi utilizzate come artefatti di deploy in ambiente containerizzato (es. cluster locale *kind*).

#### 5.1.1 Posizionamento nella cartella corretta

Dopo aver clonato la repository, è necessario posizionarsi nella **root** del progetto (la directory che contiene le cartelle dei microservizi e la cartella `scripts/`):

```bash
cd DSBD_Project
```

Tutti i comandi di build indicati in questo documento presuppongono che la directory corrente sia la root del repository.

#### 5.1.2 Comando per buildare le immagini Docker

Per costruire le immagini Docker dei microservizi applicativi a partire dai rispettivi `Dockerfile`, è possibile utilizzare:

* gli **script di supporto** versionati nella cartella `scripts/` (modalità consigliata);
* in alternativa, i comandi `docker build` eseguiti manualmente.

Gli script producono immagini con naming coerente con i manifest Kubernetes:

* `dsbd/user-manager-service:<tag>`
* `dsbd/data-collector-service:<tag>`
* `dsbd/alert-system-service:<tag>`
* `dsbd/alert-notifier-service:<tag>`

dove `<tag>` è un identificatore di versione/ambiente (di default `dev`).

**Linux/macOS (bash):**

```bash
./scripts/build-images.sh dev
```

**Windows (PowerShell):**

```powershell
.\scripts\build-images.ps1 -Tag dev
```

In alternativa, è possibile buildare manualmente le immagini:

```bash
docker build -t dsbd/user-manager-service:dev ./user-manager-service
docker build -t dsbd/data-collector-service:dev ./data-collector-service
docker build -t dsbd/alert-system-service:dev ./alert-system-service
docker build -t dsbd/alert-notifier-service:dev ./alert-notifier-service
```

**Nota sul tag delle immagini.** I manifest Kubernetes referenziano esplicitamente le immagini (ad esempio `dsbd/user-manager-service:dev`). Se si utilizza un tag diverso da `dev`, è necessario aggiornare coerentemente i campi `image:` nei file di deployment.

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

   * utilizza una immagine base JRE o JDK ridotta (ad esempio una variante *slim* o *alpine* compatibile);
   * copia dal primo stage il jar già costruito in una directory di destinazione (ad esempio `/app/app.jar`);
   * definisce il comando di avvio, tipicamente:

     ```Dockerfile
     ENTRYPOINT ["java", "-jar", "/app/app.jar"]
     ```

La build dell’immagine viene eseguita indicando come *build context* la cartella del singolo servizio, ad esempio:

```bash
docker build -t dsbd/user-manager-service:dev ./user-manager-service
```

Gli altri componenti dello stack (database, broker Kafka, Zookeeper, Kafka UI, API Gateway, Prometheus) utilizzano immagini già pronte reperite da registry pubblici e non richiedono un `Dockerfile` specifico all’interno del repository.

Questa configurazione permette di mantenere i microservizi indipendenti, garantendo al contempo una pipeline di build coerente e ripetibile.

---

### 5.2 Build locale senza Docker (opzionale)

È possibile costruire i microservizi anche in modalità **non containerizzata**, utilizzando direttamente Maven. Questa modalità è utile durante lo sviluppo, per l’esecuzione da IDE o per scenari di debug approfondito, mantenendo comunque una pipeline di build allineata a quella utilizzata negli image Docker. In pratica, l’esecuzione locale è pensata soprattutto per i servizi **User Manager** e **Data Collector**; per **Alert System** e **Alert Notifier** la build locale produce comunque gli artefatti jar necessari, ma l’esecuzione runtime viene normalmente effettuata in ambiente containerizzato, poiché dipende dalla disponibilità di un broker **Kafka** e di un server **SMTP** (ad esempio Mailtrap) correttamente configurati.

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

Per questo microservizio la configurazione principale è definita nel file `application-docker.yml`, pensato per l’esecuzione in ambiente containerizzato, dove le variabili d’ambiente vengono fornite dalla piattaforma di orchestrazione (ad esempio tramite ConfigMap/Secret). Il jar prodotto viene tipicamente utilizzato come artefatto di riferimento negli **stage runtime** dei Dockerfile multi-stage. Un’eventuale esecuzione stand‑alone richiede la predisposizione manuale di un ambiente con **PostgreSQL** e **Kafka** raggiungibili e una configurazione esplicita delle proprietà Spring Boot (ad esempio tramite variabili d’ambiente o file di configurazione esterni).

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

#### 5.2.5 Differenze rispetto alla modalità container-based

La build locale tramite Maven consente di ottenere rapidamente gli artefatti jar dei singoli microservizi ed è particolarmente adatta per attività di sviluppo e debug puntuale, soprattutto per **User Manager** e **Data Collector**, che possono essere eseguiti anche al di fuori dell’ambiente containerizzato, a patto di predisporre un’istanza PostgreSQL accessibile.

La modalità **container-based** estende questo modello includendo nel ciclo di build anche la produzione delle immagini dei microservizi e l’esecuzione dell’ambiente infrastrutturale tramite orchestrazione: database PostgreSQL, broker **Kafka**, **Zookeeper**, **Kafka UI**, **Prometheus** e **API Gateway** vengono esposti come risorse di piattaforma insieme ai microservizi applicativi, con le rispettive variabili d’ambiente e le dipendenze di rete gestite in modo centralizzato (ad esempio tramite manifest Kubernetes). In questo scenario, le immagini costruite a partire dai Dockerfile multi-stage incapsulano sia gli artefatti jar sia le configurazioni necessarie per l’esecuzione, riducendo il rischio di discrepanze tra ambienti diversi e semplificando la riproduzione dello stesso setup su macchine differenti.

Dal punto di vista operativo, la build locale offre maggiore flessibilità in fase di sviluppo, mentre la build ed esecuzione container-based rappresenta la modalità di riferimento per l’esecuzione completa del sistema di flight monitoring e per la validazione end-to-end del flusso di raccolta dati, valutazione delle soglie e invio delle notifiche di alert.

---

### 5.3 Caricamento immagini nel cluster kind

Un cluster **kind** non effettua il pull delle immagini locali dalla macchina host: per rendere disponibili ai nodi del cluster le immagini appena buildate, è necessario **caricarle esplicitamente**.

Prima di procedere con il caricamento, assicurarsi che il cluster **kind** sia già stato creato ed esista (vedi sezione 6.1.1), poiché il comando di load richiede un cluster attivo e correttamente indirizzabile tramite il relativo *cluster name*.

Per uniformità con i manifest, si assume come cluster name predefinito `dsbd-local` e come tag predefinito `dev`.

#### 5.3.1 Verifica immagini disponibili localmente

Dopo la build, è possibile verificare la presenza delle immagini nel *local Docker daemon*:

```bash
docker images | grep "^dsbd/" || true
```

In alternativa, si può interrogare una singola immagine:

```bash
docker image inspect dsbd/user-manager-service:dev > /dev/null
```

#### 5.3.2 Comando `kind load docker-image`

Il caricamento nel cluster può essere eseguito tramite script:

**Linux/macOS (bash):**

```bash
./scripts/kind/load-images.sh dev dsbd-local
```

**Windows (PowerShell):**

```powershell
.\scripts\kind\load-images.ps1 -Tag dev -ClusterName dsbd-local
```

In alternativa, è possibile utilizzare direttamente il comando `kind load docker-image` per ciascuna immagine:

```bash
kind load docker-image dsbd/user-manager-service:dev --name dsbd-local
kind load docker-image dsbd/data-collector-service:dev --name dsbd-local
kind load docker-image dsbd/alert-system-service:dev --name dsbd-local
kind load docker-image dsbd/alert-notifier-service:dev --name dsbd-local
```

#### 5.3.3 Verifica del caricamento nel cluster

Il controllo più immediato consiste nel verificare che i Pod, una volta creati, non entrino in stato `ImagePullBackOff` e riescano a passare in `Running`.

Se si desidera un riscontro diretto lato nodo kind, è possibile elencare le immagini presenti nel container del control plane (nome nodo dipendente dal cluster name):

```bash
docker exec -it dsbd-local-control-plane crictl images | grep "dsbd/" || true
```

Se le immagini `dsbd/*:<tag>` risultano presenti, il caricamento è stato completato correttamente.

## 6. Deploy & Run on Kubernetes (kind)

### 6.1 Prima esecuzione dello stack completo

L’esecuzione in ambiente Kubernetes locale utilizza **kind** (*Kubernetes IN Docker*) come cluster di sviluppo. Il deploy avviene tramite manifest Kubernetes versionati nella cartella `k8s/`, organizzati mediante **Kustomize**.

Per poter eseguire correttamente lo stack è necessario che:

* il cluster kind sia stato creato e il namespace `dsbd` sia presente;
* le immagini Docker dei microservizi siano state buildate e caricate nel cluster kind;
* i file di configurazione Kubernetes (**ConfigMap** e **Secret**) siano valorizzati con credenziali e parametri coerenti.

#### 6.1.1 Creazione del cluster kind

Il cluster può essere creato in due modalità equivalenti.

Una volta completata la creazione del cluster, se le immagini Docker dei microservizi sono state buildate localmente, è necessario renderle disponibili al runtime di *kind* caricandole nel cluster come descritto nella sezione 5.3 (in assenza di questo step i Pod possono andare in `ErrImagePull` / `ImagePullBackOff`).

**Opzione A — Script di supporto (consigliata)**

Gli script in `scripts/kind/` creano il cluster a partire dalla configurazione versionata in `k8s/kind/kind-cluster.yaml` e applicano automaticamente il namespace `dsbd`.

**Linux/macOS (bash):**

```bash
./scripts/kind/create-cluster.sh dsbd-local
```

**Windows (PowerShell):**

```powershell
.\scripts\kind\create-cluster.ps1 -ClusterName dsbd-local
```

**Opzione B — Comandi kind/kubectl**

```bash
kind create cluster --name dsbd-local --config k8s/kind/kind-cluster.yaml
kubectl apply -f k8s/00-namespace.yaml
```

Dopo la creazione è opportuno verificare il *context* corrente e l’accesso al cluster:

```bash
kubectl config current-context
kubectl get nodes
```

Il context atteso (cluster `dsbd-local`) è tipicamente `kind-dsbd-local`.

#### 6.1.2 Preparazione ConfigMap e Secret

La configurazione runtime dello stack è fornita ai Pod tramite:

* `k8s/config/01-configmap.yaml` (**ConfigMap**) per variabili non sensibili;
* `k8s/config/02-secret.yaml` (**Secret**) per credenziali e dati sensibili.

Entrambe le risorse sono dichiarate nel namespace `dsbd` e vengono applicate automaticamente durante il deploy tramite Kustomize.

1. Aprire `k8s/config/01-configmap.yaml` e verificare i parametri applicativi principali:

   * **Database**: `DB_HOST`, `DB_PORT`, `DB_USERNAME`, `USER_DB_NAME`, `DATA_DB_NAME`.
   * **Kafka**: `KAFKA_BOOTSTRAP_SERVERS`.
   * **OpenSky**: `OPENSKY_AUTH_URL`, `OPENSKY_API_URL`.
   * **Mail**: `MAIL_HOST`, `MAIL_PORT`, `MAIL_SMTP_AUTH`, `MAIL_SMTP_STARTTLS_ENABLE`, `MAIL_FROM`.

2. Aprire `k8s/config/02-secret.yaml` e sostituire i valori presenti con quelli del proprio ambiente:

   * **Database**: `DB_PASSWORD`.
   * **OpenSky OAuth2**: `OPENSKY_CLIENT_ID`, `OPENSKY_CLIENT_SECRET`.
   * **Mailtrap SMTP**: `MAIL_USERNAME`, `MAIL_PASSWORD`.

3. Verificare che i manifest dei microservizi referenzino correttamente ConfigMap e Secret.

   Nel progetto corrente l’iniezione delle variabili d’ambiente avviene nei deployment Kubernetes dei servizi applicativi (cartelle `k8s/apps/*/`), tramite riferimenti a `dsbd-config` e `dsbd-secrets`.

4. In caso di modifiche successive a ConfigMap/Secret con stack già in esecuzione, applicare nuovamente i manifest:

   ```bash
   kubectl apply -k k8s
   ```

   e, se necessario, forzare un riavvio dei Pod per rendere effettive le nuove variabili:

   ```bash
   kubectl -n dsbd rollout restart deploy/user-manager-service
   kubectl -n dsbd rollout restart deploy/data-collector-service
   kubectl -n dsbd rollout restart deploy/alert-system-service
   kubectl -n dsbd rollout restart deploy/alert-notifier-service
   kubectl -n dsbd rollout restart deploy/api-gateway
   ```

#### 6.1.3 Deploy tramite Kustomize (`kubectl apply -k`)

Il deploy dello stack completo avviene applicando il *kustomization* root `k8s/kustomization.yaml`, che include:

* `k8s/stack/` (microservizi + infrastruttura applicativa e di messaging);
* `k8s/observability/prometheus/` (monitoring).

Dalla root della repository:

```bash
kubectl apply -k k8s
```

In alternativa, se si intende applicare **solo** lo stack applicativo (senza Prometheus), è possibile applicare direttamente:

```bash
kubectl apply -k k8s/stack
```

Durante la prima esecuzione alcuni Pod potrebbero rimanere temporaneamente in stato `Init` o `ContainerCreating` a causa delle dipendenze tra componenti (ad esempio inizializzazione di PostgreSQL e disponibilità del broker Kafka).

#### 6.1.4 Verifica che i Pod siano in esecuzione

Per verificare lo stato del deploy nel namespace `dsbd`:

```bash
kubectl -n dsbd get pods
kubectl -n dsbd get svc
```

Per osservare in tempo reale la stabilizzazione dei Pod:

```bash
kubectl -n dsbd get pods -w
```

Nel caso di Pod non pronti (`READY 0/1`) o in errore (`CrashLoopBackOff`, `ImagePullBackOff`), è opportuno:

* controllare l’immagine referenziata nel deployment e la disponibilità nel cluster kind;
* controllare che `ConfigMap` e `Secret` siano presenti e contengano i valori attesi;
* analizzare eventi e log (vedi sezione 6.3).

---

### 6.2 Arresto del sistema

La terminazione controllata dello stack su Kubernetes consente di liberare le risorse del cluster mantenendo, se desiderato, la persistenza dei dati applicativi.

#### 6.2.1 Eliminazione delle risorse Kubernetes (`kubectl delete -k`)

Per rimuovere tutte le risorse applicate tramite Kustomize:

```bash
kubectl delete -k k8s
```

Se il namespace `dsbd` è stato creato separatamente e si desidera rimuoverlo esplicitamente:

```bash
kubectl delete namespace dsbd
```

#### 6.2.2 Eliminazione del cluster kind

Per eliminare il cluster kind:

**Opzione A — Script di supporto**

**Linux/macOS (bash):**

```bash
./scripts/kind/delete-cluster.sh dsbd-local
```

**Windows (PowerShell):**

```powershell
.\scripts\kind\delete-cluster.ps1 -ClusterName dsbd-local
```

**Opzione B — Comando kind**

```bash
kind delete cluster --name dsbd-local
```

---

### 6.3 Comandi Kubernetes utili

Oltre alle operazioni standard di avvio e arresto dello stack, alcuni comandi **kubectl** risultano particolarmente utili per attività di diagnosi e manutenzione puntuale dei singoli servizi.

#### 6.3.1 Visualizzazione risorse (`kubectl get`) e stato

Comandi di uso frequente per ottenere una vista sintetica dello stack:

```bash
kubectl -n dsbd get all
kubectl -n dsbd get pods -o wide
kubectl -n dsbd get deployments
kubectl -n dsbd get configmap,secret
kubectl -n dsbd get pvc
```

Per controllare lo stato del cluster kind:

```bash
kubectl get nodes
kubectl cluster-info
```

#### 6.3.2 Accesso ai log di un Pod (`kubectl logs`)

Per consultare i log di un Pod (singolo container):

```bash
kubectl -n dsbd logs <pod-name>
```

Per seguire i log in streaming:

```bash
kubectl -n dsbd logs -f <pod-name>
```

Se un Pod contiene più container (ad esempio initContainer + container applicativo), è possibile specificare il container:

```bash
kubectl -n dsbd logs <pod-name> -c <container-name>
```

#### 6.3.3 Describe e debugging (`kubectl describe`)

`kubectl describe` consente di ispezionare eventi e motivazioni di failure (pull immagini, probe, env mancanti, crash):

```bash
kubectl -n dsbd describe pod <pod-name>
kubectl -n dsbd describe deployment <deployment-name>
```

Per aprire una shell in un Pod (se l’immagine lo consente):

```bash
kubectl -n dsbd exec -it <pod-name> -- sh
```

---

### 6.4 Deploy dei servizi infrastrutturali (overview)

I servizi infrastrutturali sono dichiarati nei manifest Kubernetes sotto `k8s/infra/` e `k8s/observability/` e vengono applicati automaticamente tramite il deploy Kustomize.

#### 6.4.1 PostgreSQL

PostgreSQL è definito in `k8s/infra/postgres/` e include:

* un **ConfigMap** di inizializzazione (`01-initdb-configmap.yaml`) per la creazione dei database logici (`userdb`, `datadb`);
* un **PersistentVolumeClaim** (`02-pvc.yaml`) per garantire persistenza dei dati durante i riavvii dei Pod;
* un **Deployment** (`03-deployment.yaml`) con container `postgres`;
* un **Service** (`04-service.yaml`) esposto internamente come `postgres:5432` nel namespace `dsbd`.

I microservizi applicativi puntano a PostgreSQL tramite `DB_HOST=postgres` e `DB_PORT=5432`.

#### 6.4.2 Kafka e servizi correlati

Lo stack di messaging è definito in `k8s/infra/kafka/` e include:

* **Zookeeper** (`Deployment` + `Service`) esposto come `zookeeper:2181`;
* **Kafka Broker** (`Deployment` + `Service`) esposto come `kafka:9092`.

I microservizi che producono/consumano eventi utilizzano `KAFKA_BOOTSTRAP_SERVERS=kafka:9092`.

#### 6.4.3 Kafka UI

Kafka UI è definito in `k8s/infra/kafka/` come deployment `kafka-ui` e service `kafka-ui:8080` (ClusterIP). È utile per verificare topic, consumer group e messaggi pubblicati dai servizi applicativi.

#### 6.4.4 Prometheus

Prometheus è definito in `k8s/observability/prometheus/` e include:

* **ServiceAccount** e regole **RBAC** per permettere lo *scraping* delle metriche dai target;
* **ConfigMap** con la configurazione `prometheus.yml`;
* **Deployment** e **Service** (`prometheus:9090`, ClusterIP).

I microservizi espongono le metriche tramite endpoint **`/actuator/prometheus`** e vengono monitorati da Prometheus secondo la configurazione dichiarata nei manifest.

## 7. Accessing the Services

### 7.1 Accesso tramite port-forward (approccio raccomandato)

In ambiente Kubernetes (cluster *kind*), i servizi sono esposti come **Service di tipo ClusterIP** e non risultano direttamente raggiungibili dall’host. L’accesso operativo (sviluppo, test e osservabilità) avviene quindi tramite **port-forward**, instaurando un tunnel locale verso il Service (o verso un singolo Pod).

I comandi di `kubectl port-forward` devono essere eseguiti in terminali dedicati e **mantenuti in esecuzione** per tutta la durata dell’utilizzo (interruzione con `CTRL+C`). In caso di conflitto di porte, è possibile sostituire la porta locale con un valore libero.

#### 7.1.1 Port-forward API Gateway

Per esporre localmente l’API Gateway (Service `api-gateway`, porta 80):

```bash
kubectl -n dsbd port-forward svc/api-gateway 8080:80
```

Una volta attivo il port-forward, il gateway è raggiungibile su:

* **Base URL (gateway)**: `http://localhost:8080`

#### 7.1.2 Port-forward Kafka UI

Per esporre localmente la Kafka UI (Service `kafka-ui`, porta 8080):

```bash
kubectl -n dsbd port-forward svc/kafka-ui 8085:8080
```

Una volta attivo il port-forward, l’interfaccia è raggiungibile su:

* **Kafka UI**: `http://localhost:8085`

#### 7.1.3 Port-forward Prometheus

Per esporre localmente Prometheus (Service `prometheus`, porta 9090):

```bash
kubectl -n dsbd port-forward svc/prometheus 9090:9090
```

Una volta attivo il port-forward, l’interfaccia è raggiungibile su:

* **Prometheus UI**: `http://localhost:9090`

---

### 7.2 User Manager Service

Lo *User Manager Service* espone un set di API REST dedicate alla gestione del ciclo di vita degli utenti (registrazione, lettura, cancellazione) e rappresenta l’autorità applicativa per la persistenza degli utenti nel *User DB*. Le API sono progettate per essere invocate tramite l’**API Gateway**, mantenendo un contratto stabile e facilmente integrabile da client esterni.

#### 7.2.1 Endpoint base (host, port)

Il servizio può essere raggiunto in due modalità principali: tramite **API Gateway** (esposizione verso l’host tramite port-forward) oppure in modo diretto, indirizzando il Service del microservizio.

*Accesso tramite API Gateway*

Quando lo stack è eseguito su Kubernetes (kind), l’API Gateway rappresenta l’unico punto di ingresso HTTP verso l’esterno e instrada le richieste verso lo *User Manager Service*:

* **Base URL (gateway)**: `http://localhost:8080/api/users`

Tutte le operazioni descritte nelle sezioni successive sono accessibili prefissando i path indicati con questo endpoint.

*Accesso diretto al microservizio*

Per scenari di sviluppo o debug, è possibile invocare direttamente il microservizio, bypassando il gateway.

* **Accesso interno nel cluster**
  Le chiamate tra servizi avvengono utilizzando il DNS del Service:

  * **Base URL interno**: `http://user-manager-service:8081/api/users`

* **Accesso dall’host tramite port-forward (opzionale)**
  È possibile esporre localmente il Service del microservizio:

  ```bash
  kubectl -n dsbd port-forward svc/user-manager-service 8081:8081
  ```

  In questo caso, dall’host è possibile invocare il servizio direttamente tramite:

  * **Base URL host**: `http://localhost:8081/api/users`

#### 7.2.2 Principali API REST esposte

Le principali operazioni REST esposte dallo *User Manager Service* sono le seguenti:

* **Registrazione di un nuovo utente**

  * **Metodo**: `POST`
  * **URL**: `/api/users`
  * **Body (JSON)**: contiene almeno `email` e `name` dell’utente.

* **Lettura di un utente specifico**

  * **Metodo**: `GET`
  * **URL**: `/api/users/{email}`

* **Elenco degli utenti**

  * **Metodo**: `GET`
  * **URL**: `/api/users`

* **Cancellazione di un utente**

  * **Metodo**: `DELETE`
  * **URL**: `/api/users/{email}`

Quando l’accesso avviene tramite gateway, i path sopra riportati sono da intendersi come relativi al prefisso `http://localhost:8080`.

#### 7.2.3 Codici di risposta attesi

Le principali convenzioni sui codici di stato HTTP restituiti dal servizio sono:

* `201 Created` per la creazione di un nuovo utente;
* `200 OK` per richieste di lettura o lista eseguite con successo;
* `204 No Content` per cancellazioni avvenute correttamente;
* `400 Bad Request` in caso di payload non valido o campi obbligatori mancanti;
* `404 Not Found` quando l’utente richiesto non esiste.

---

### 7.3 Data Collector Service

Il *Data Collector Service* espone API REST per la gestione degli aeroporti di interesse e per l’interrogazione dello stato dei voli, persiste le informazioni nel *Data DB* e interagisce con OpenSky Network per recuperare lo stato corrente dei voli. L’accesso da parte dei client è previsto tramite **API Gateway**.

#### 7.3.1 Endpoint base (host, port)

Analogamente allo *User Manager*, il *Data Collector Service* può essere invocato tramite API Gateway oppure in modo diretto.

*Accesso tramite API Gateway*

L’API Gateway espone verso l’esterno i principali endpoint del *Data Collector* sotto i seguenti prefissi:

* **Interessi utente–aeroporto**: `http://localhost:8080/api/interests`
* **Interrogazioni sui voli**: `http://localhost:8080/api/flights`

Gli esempi di richiesta riportati nelle sezioni successive assumono questi prefissi come base URL.

*Accesso diretto al microservizio*

* **Accesso interno nel cluster**

  * **Base URL interno**: `http://data-collector-service:8082/api`

* **Accesso dall’host tramite port-forward (opzionale)**

  ```bash
  kubectl -n dsbd port-forward svc/data-collector-service 8082:8082
  ```

  In questo caso, dall’host è possibile invocare direttamente il servizio tramite:

  * **Base URL host**: `http://localhost:8082/api`

#### 7.3.2 API REST per aeroporti e interessi (incluse le soglie)

Le API dedicate alla gestione degli interessi utente–aeroporto permettono di:

* creare o aggiornare un interesse specificando `userEmail`, `airportCode` e (se previsto) le soglie `highValue`/`lowValue`;
* ottenere la lista degli interessi configurati;
* leggere o aggiornare un singolo interesse identificato da coppia (`userEmail`, `airportCode`).

I path esposti dal servizio sono raggiungibili tramite gateway sotto il prefisso `/api/interests`.

#### 7.3.3 API REST per interrogare i voli

Le API di interrogazione dei voli consentono di:

* recuperare lo stato più recente dei voli per un aeroporto;
* effettuare interrogazioni su intervalli temporali;
* ottenere liste e dettagli relativi ai voli osservati.

I path esposti dal servizio sono raggiungibili tramite gateway sotto il prefisso `/api/flights`.

---

### 7.4 gRPC Interface

Lo *User Manager Service* espone, oltre alle API REST, un’interfaccia gRPC utilizzata per la **validazione dell’esistenza di un utente**. Tale interfaccia è impiegata internamente dal *Data Collector Service* durante la gestione degli interessi.

#### 7.4.1 Panoramica del servizio gRPC esposto dallo User Manager

L’interfaccia gRPC prevede un servizio logico, ad esempio `UserValidationService`, con un metodo principale:

* `rpc userExists(UserEmailRequest) returns (UserExistsResponse)`

in cui:

* `UserEmailRequest` contiene un singolo campo `email`;
* `UserExistsResponse` contiene un campo booleano che indica se l’utente è presente nel database degli utenti gestito dallo *User Manager*.

Il servizio gRPC è pubblicato sul Service `user-manager-service` (porta 9090) e **non è destinato a client esterni HTTP**, ma esclusivamente ad uso interno tra microservizi.

#### 7.4.2 Utilizzo interno da parte del Data Collector

Il *Data Collector Service* utilizza il metodo `userExists` prima di accettare la creazione o l’aggiornamento di un interesse utente–aeroporto. La sequenza tipica è la seguente:

1. Il client invia una richiesta REST a `POST /api/interests` specificando `userEmail` e `airportCode`.
2. Il *Data Collector* costruisce una richiesta gRPC `UserEmailRequest` e invoca `userExists` sullo *User Manager*.
3. Se la risposta indica che l’utente esiste (`exists = true`), il *Data Collector* procede a registrare o aggiornare l’interesse nel proprio database;
4. Se l’utente non esiste, il *Data Collector* restituisce un errore (tipicamente `404 Not Found`), evitando di registrare interessi per utenti non validi.

Questa interazione permette di mantenere il *User Manager* come punto unico di verità per l’identità applicativa, evitando duplicazioni di responsabilità.

---

### 7.5 API Gateway

L’**API Gateway** è implementato tramite NGINX e costituisce il punto di accesso pubblico alle API dell’intero sistema. Il suo obiettivo è fornire un singolo endpoint verso i client e schermare i dettagli di rete dei singoli servizi.

#### 7.5.1 Endpoint pubblici esposti dal gateway

In ambiente Kubernetes (kind), l’API Gateway è esposto come Service di tipo ClusterIP e viene raggiunto dall’host tramite port-forward (vedi sezione 7.1.1). L’accesso esterno avviene quindi tramite:

* `http://localhost:8080`

Il gateway espone pubblicamente i seguenti endpoint principali:

* **Gestione utenti** (proxy verso *User Manager Service*):

  * `http://localhost:8080/api/users` (lista e creazione utenti);
  * `http://localhost:8080/api/users/{email}` (lettura e cancellazione di un utente specifico).

* **Gestione interessi e interrogazione voli** (proxy verso *Data Collector Service*):

  * `http://localhost:8080/api/interests` (creazione, elenco e aggiornamento degli interessi);
  * `http://localhost:8080/api/interests/{userEmail}/{airportCode}` (gestione di un singolo interesse);
  * `http://localhost:8080/api/flights/...` (interrogazioni sui voli, ad esempio `/last`, intervalli temporali, ecc.).

I client HTTP possono utilizzare esclusivamente questi endpoint pubblici senza doversi preoccupare dei nomi dei servizi e delle porte interne.

#### 7.5.2 Instradamento verso i microservizi interni

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

In questo modo, un client che invoca `http://localhost:8080/api/users/` viene servito dal *User Manager*, mentre le richieste verso `.../api/interests` e `.../api/flights` vengono inoltrate al *Data Collector*.

---

### 7.6 Alert System & Alert Notifier

Gli ultimi due microservizi applicativi sono dedicati alla pipeline di alerting: l’*Alert System* valuta le soglie sugli interessi e produce eventi di notifica, mentre l’*Alert Notifier* si occupa dell’invio delle notifiche e‑mail verso gli utenti finali.

#### 7.6.1 Ruolo dei servizi nella pipeline di notifica

L’*Alert System Service* opera come **consumer Kafka** sul topic che veicola gli eventi di osservazione dei voli (ad esempio il topic `to-alert-system`). Ogni evento contiene le informazioni necessarie a determinare se un interesse debba generare una notifica (ritardo oltre `lowValue` o `highValue`). Per ogni messaggio, il servizio:

1. recupera dal *Data DB* le informazioni sull’interesse associato;
2. valuta le soglie configurate per determinare se la condizione richiede una notifica;
3. in caso positivo, produce un nuovo messaggio su un topic Kafka dedicato alle notifiche (ad esempio `to-notifier`), includendo i dettagli utili (destinatario, aeroporto, dettaglio del volo, ritardo registrato, ecc.).

L’*Alert Notifier Service* è a sua volta un **consumer Kafka** sul topic delle notifiche. Per ogni evento ricevuto, esso:

1. costruisce il contenuto dell’e‑mail (oggetto e corpo del messaggio) sulla base delle informazioni contenute nell’evento di notifica;
2. utilizza il `JavaMailSender` configurato tramite variabili `MAIL_*` per inviare il messaggio verso il server SMTP di testing.

#### 7.6.2 Osservazione del flusso tramite log e Kafka UI

La verifica del corretto funzionamento della pipeline di alerting può essere effettuata principalmente tramite:

* **log dei microservizi**;
* **Kafka UI** esposta tramite port-forward.

Per ispezionare i log è possibile utilizzare, ad esempio:

```bash
kubectl -n dsbd logs -f deploy/alert-system-service
```

e, in un secondo terminale:

```bash
kubectl -n dsbd logs -f deploy/alert-notifier-service
```

In questo modo è possibile osservare in tempo reale:

* la ricezione dei messaggi da parte dell’*Alert System*;
* la generazione di eventi di notifica sui topic Kafka;
* l’elaborazione dei messaggi da parte dell’*Alert Notifier* e il tentativo di invio delle e‑mail.

Per utilizzare la **Kafka UI**, avviare il port-forward come descritto nella sezione 7.1.2 e accedere all’interfaccia tramite:

* `http://localhost:8081`

All’interno della Kafka UI è possibile:

* verificare la presenza dei topic utilizzati dal sistema (ad esempio `to-alert-system`, `to-notifier`);
* ispezionare i messaggi prodotti dal *Data Collector* e consumati dall’*Alert System*;
* controllare i messaggi di notifica destinati all’*Alert Notifier*.

---

### 7.7 Kafka UI

La Kafka UI fornisce un’interfaccia web per ispezionare il broker Kafka, i topic e i messaggi scambiati tra i microservizi.

#### 7.7.1 Endpoint di accesso (host, port)

Dopo aver avviato il port-forward del Service `kafka-ui` (vedi sezione 7.1.2), la UI è raggiungibile tramite:

* **URL**: `http://localhost:8085`

#### 7.7.2 Verifica dei topic e ispezione messaggi

Dall’interfaccia è possibile:

* controllare che i topic applicativi siano stati creati correttamente;
* verificare l’attività dei consumer group e lo stato dei lag;
* ispezionare i messaggi pubblicati sui topic principali della pipeline (produzione da *Data Collector*, consumo da *Alert System*, propagazione verso *Alert Notifier*).

---

### 7.8 Prometheus UI

Prometheus consente di verificare lo scraping delle metriche esposte dai microservizi (endpoint `/actuator/prometheus`) e di eseguire query sulle serie temporali raccolte.

#### 7.8.1 Endpoint di accesso (host, port)

Dopo aver avviato il port-forward del Service `prometheus` (vedi sezione 7.1.3), la UI è raggiungibile tramite:

* **URL**: `http://localhost:9090`

#### 7.8.2 Verifica scraping (Targets) e query base

All’interno della UI è possibile:

* verificare lo stato dello scraping accedendo a **Status → Targets**, controllando che i target risultino *UP*;
* eseguire query di base (tab **Graph**) per verificare la presenza delle metriche tecniche e custom, ad esempio:

  * metriche generali JVM/Spring Boot (`jvm_*`, `process_*`, `http_server_requests_*`);
  * metriche esposte dai componenti applicativi tramite Micrometer/Actuator;
  * serie temporali collegate a circuit breaker e resilienza (se esposte dal runtime).

## 8. Using Postman Collections

### 8.1 Localizzazione delle collection (`postman/`)

Le **Postman collections** sono collocate nella directory `postman/` della repository e sono organizzate per versione funzionale del sistema. La struttura principale è la seguente:

```text
postman/
  ├── homework-1/
  │   ├── hw1-user-manager-api.postman_collection.json
  │   └── hw1-data-collector-api.postman_collection.json
  ├── homework-2/
  │   ├── hw2-user-manager-api.postman_collection.json
  │   └── hw2-data-collector-api.postman_collection.json
  └── homework-3/
      ├── hw3-user-manager-api.postman_collection.json
      └── hw3-data-collector-api.postman_collection.json
```

Le collection presenti in `postman/homework-1/` rappresentano il set originario di richieste per l’esercizio delle API di **User Manager** e **Data Collector** nella configurazione di base. Le collection in `postman/homework-2/` estendono tale set includendo:

* casi di test addizionali per la gestione delle **soglie** sugli interessi utente–aeroporto;
* scenari di errore e validazione allineati alle nuove regole applicative;
* richieste dedicate alle nuove API esposte dal **Data Collector** per interrogazioni analitiche (ad esempio media dei voli su intervalli temporali).

Le collection in `postman/homework-3/` mantengono lo stesso set di richieste della versione precedente, risultando quindi pienamente compatibili con gli scenari di test già definiti.

Per verificare il comportamento della versione corrente del sistema è consigliabile utilizzare in via preferenziale le collection collocate in `postman/homework-3/`, mantenendo le collection delle versioni precedenti come riferimento storico.

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
* `postman/homework-2/hw2-user-manager-api.postman_collection.json`;
* `postman/homework-3/hw3-user-manager-api.postman_collection.json`.

La versione in `homework-3` è quella di riferimento per la versione corrente del sistema. Essa contiene un insieme di richieste preconfigurate verso il microservizio **User Manager**, che espone le API responsabili del ciclo di vita degli utenti. In particolare, sono presenti richieste per:

* **registrare un nuovo utente** (`UM-01 – Create user (201 Created)`), con corpo JSON contenente almeno `email` e `name`;
* **tentare la registrazione di un utente duplicato** (`UM-02 – Create user (409 Conflict)`), utile per verificare la gestione dei vincoli di unicità;
* **recuperare la lista degli utenti** (`UM-03 – Get all users`);
* **recuperare un utente per email** (`UM-04 – Get user by email`);
* **cancellare un utente esistente** (`UM-05 – Delete user (204 No Content)`);
* **gestire casi di errore sulla cancellazione** (`UM-06 – Delete user (404 Not Found)`).

Tutte le richieste della collection fanno riferimento, in configurazione predefinita, al seguente endpoint HTTP:

```text
http://localhost:8081/api/users
```

Quando il sistema è eseguito su Kubernetes (*kind*), l’endpoint sopra riportato è raggiungibile attivando un **port-forward** verso il Service `user-manager-service` (porta esposta `8081`):

```bash
kubectl -n dsbd port-forward svc/user-manager-service 8081:8081
```

In presenza di conflitti (ad esempio porta `8081` già in uso), è possibile selezionare una porta locale differente (es. `8083:8081`) aggiornando coerentemente gli URL nelle collection oppure utilizzando variabili di ambiente Postman (vedi sezione 8.3).

#### 8.2.2 Data Collector API collection

La collection **Data Collector API** corrisponde ai file:

* `postman/homework-3/hw1-data-collector-api.postman_collection.json`;
* `postman/homework-2/hw2-data-collector-api.postman_collection.json`.
* `postman/homework-3/hw3-data-collector-api.postman_collection.json`.

La versione in `homework-3` include l’insieme aggiornato di richieste verso il microservizio **Data Collector**, esteso con la gestione delle soglie sugli interessi e con nuove API di interrogazione analitica. Le richieste coprono, tra le altre, le seguenti aree funzionali:

* **gestione degli aeroporti** (creazione, lettura, cancellazione), con richieste dedicate al popolamento del catalogo degli aeroporti monitorabili;
* **gestione degli interessi utente–aeroporto con soglie**: creazione, aggiornamento, lettura e cancellazione degli interessi che associano un utente a un aeroporto, comprensivi dei valori `highValue` e `lowValue` utilizzati per l’alerting;
* **interrogazione dei voli** associati a uno specifico aeroporto o interesse, inclusi gli endpoint per:

  * recuperare l’ultimo volo in arrivo o in partenza per un aeroporto (`DC-23 – Recupero ultimo volo in arrivo (caso positivo)`, `DC-24 – Recupero ultimo volo in partenza (caso positivo)`);
  * calcolare la **media dei voli** in un determinato numero di giorni per direzione (`DC-29`–`DC-32`);
* **gestione dei casi di errore** (utente inesistente, aeroporto inesistente, soglie non valide, direzioni non valide, ecc.), tramite richieste esplicite che consentono di verificare il comportamento del sistema in condizioni non corrette.

In configurazione predefinita, le richieste puntano al seguente endpoint:

```text
http://localhost:8082/api
```

Quando il sistema è eseguito su Kubernetes (*kind*), l’endpoint sopra riportato è raggiungibile attivando un **port-forward** verso il Service `data-collector-service` (porta esposta `8082`):

```bash
kubectl -n dsbd port-forward svc/data-collector-service 8082:8082
```

Le API per aeroporti, interessi e voli sono organizzate su path coerenti con il modello di dominio, ad esempio:

* `http://localhost:8082/api/airports` per la gestione del catalogo aeroporti;
* `http://localhost:8082/api/interests` per la gestione degli interessi utente–aeroporto;
* `http://localhost:8082/api/flights/...` per le interrogazioni relative ai voli.

---

### 8.3 Configurazione delle variabili di ambiente in Postman (host, port, base URL)

Le collection fornite sono già configurate per funzionare con i valori predefiniti, ossia:

* **User Manager** raggiungibile su `http://localhost:8081`;
* **Data Collector** raggiungibile su `http://localhost:8082`.

In esecuzione su Kubernetes (*kind*), è necessario assicurarsi che i relativi **port-forward** siano attivi e mappati sulle porte locali attese dalle collection (vedi sezioni 8.2.1 e 8.2.2).

Gli URL presenti nelle collection utilizzano direttamente questi valori. Se si desidera rendere i test più portabili (ad esempio per eseguire il sistema su host o porte differenti), è possibile introdurre un semplice layer di parametrizzazione tramite **environment variables** di Postman.

Un setup tipico prevede la creazione di un ambiente Postman con le seguenti variabili:

| Variabile                 | Valore predefinito      | Descrizione                               |
| ------------------------- | ----------------------- | ----------------------------------------- |
| `user_manager_base_url`   | `http://localhost:8081` | Base URL del microservizio User Manager   |
| `data_collector_base_url` | `http://localhost:8082` | Base URL del microservizio Data Collector |
| `api_gateway_base_url`    | `http://localhost:8080` | Base URL dell’API Gateway NGINX           |

Dopo aver creato l’ambiente, è possibile **adattare le request esistenti** sostituendo il prefisso fisso degli URL con i placeholder, ad esempio:

* `http://localhost:8081/api/users` → `{{user_manager_base_url}}/api/users`;
* `http://localhost:8082/api/interests` → `{{data_collector_base_url}}/api/interests`.

In questo modo, un eventuale cambiamento di host o porta potrà essere gestito modificando unicamente i valori nell’ambiente Postman, senza intervenire su ogni singola richiesta.

---

### 8.4 Esecuzione di scenari end-to-end tramite Postman

Le collection fornite consentono di esercitare in modo sistematico le principali funzionalità esposte dal sistema, simulando scenari end‑to‑end che coinvolgono sia lo **User Manager** sia il **Data Collector**. I paragrafi seguenti illustrano tre scenari tipici, che possono essere utilizzati come riferimento per la validazione funzionale.

#### 8.4.1 Registrazione di un nuovo utente

Per registrare un nuovo utente tramite Postman è possibile utilizzare la request `UM-01 – Registrazione utente (caso positivo)` nella collection **User Manager API** (versione `homework-3`). La procedura operativa è la seguente:

1. Assicurarsi che il sistema sia in esecuzione e che lo *User Manager Service* sia raggiungibile su `http://localhost:8081` (eventualmente tramite port-forward).

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

Per registrare un **interesse utente–aeroporto** comprensivo di soglie è possibile utilizzare la request `DC-01 – Registrazione interesse (caso positivo)` nella collection **Data Collector API** (versione `homework-3`). Lo scenario tipico prevede i seguenti passi:

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

1. Assicurarsi che il sistema sia in esecuzione e che il **Data Collector** abbia avuto il tempo di eseguire almeno un ciclo di raccolta dai servizi OpenSky (secondo la schedulazione configurata).
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

## 9. Monitoring & Metrics (Prometheus)

### 9.1 Endpoint `/actuator/prometheus` e metriche applicative

Ciascun microservizio che partecipa al monitoring espone l’endpoint HTTP **`/actuator/prometheus`** tramite **Spring Boot Actuator** e **Micrometer Prometheus Registry**.

Nel deployment su Kubernetes, lo scraping è effettuato da **Prometheus** verso i Service interni del namespace `dsbd`, utilizzando **Kubernetes Service Discovery** (role `endpoints`) e una regola di *relabeling* che seleziona i soli Service di interesse.

Nel progetto corrente, Prometheus raccoglie metriche da:

* **`data-collector-service`** (*job*: `data-collector`)
* **`alert-system-service`** (*job*: `alert-system`)
* **`alert-notifier-service`** (*job*: `alert-notifier`)

Le metriche sono composte da:

* **metriche tecniche** esposte automaticamente da Actuator/Micrometer (es. JVM, HTTP server, ecc.);
* **metriche applicative custom** implementate esplicitamente a livello di codice per osservare punti critici della pipeline (OpenSky client, valutazione soglie, consumo notifiche, invio email).

A livello di configurazione, Prometheus utilizza un `scrape_interval` pari a **15s** e un `metrics_path` fissato a **`/actuator/prometheus`** per i job applicativi.

---

### 9.2 Tipologie richieste: COUNTER e GAUGE

Le metriche custom adottano esclusivamente le due tipologie richieste:

* **COUNTER** (*monotonic*, solo incrementale): adatto a conteggi di eventi (richieste, errori, notifiche, fallback). In PromQL, tali metriche vanno tipicamente analizzate tramite funzioni derivate (*rate/increase*) su finestre temporali.
* **GAUGE** (*istantaneo*, aggiornabile): adatto a rappresentare uno stato o un valore misurato (in questo caso, la durata dell’ultima operazione significativa, espressa in millisecondi).

Metriche **COUNTER** implementate nel progetto:

* `opensky_requests_total`, `opensky_request_errors_total`, `opensky_fallback_total` (*Data Collector*)
* `alert_system_evaluations_total`, `alert_system_notifications_total` (*Alert System*)
* `alert_notifier_notifications_consumed_total`, `alert_notifier_notifications_processing_errors_total` (*Alert Notifier*)
* `email_sent_total`, `email_send_errors_total`, `email_rate_limited_total` (*Alert Notifier*)

Metriche **GAUGE** implementate nel progetto (durate *last operation*, in ms):

* `opensky_last_fetch_duration_ms`
* `alert_system_last_eval_duration_ms`
* `alert_notifier_last_processing_duration_ms`
* `email_last_send_duration_ms`

---

### 9.3 Labeling (service, node) e convenzioni adottate

Per garantire **coerenza e correlazione** tra le serie temporali raccolte, tutte le metriche (tecniche e custom) sono arricchite con due label uniformi:

* **`service`**: identifica il microservizio produttore della metrica.
* **`node`**: identifica il nodo Kubernetes su cui è schedulato il Pod.

Il labeling è applicato a livello Micrometer tramite `management.metrics.tags`:

* `service` è valorizzata tramite la variabile d’ambiente **`SERVICE_NAME`** (impostata nei manifest dei Deployment).
* `node` è valorizzata tramite **`NODE_NAME`**, derivata da `spec.nodeName` via `fieldRef`.

Nel file di configurazione di Prometheus, l’opzione **`honor_labels: true`** è attiva per i job applicativi, preservando le label emesse a livello applicativo.

---

### 9.4 Verifica dei target in Prometheus (Status > Targets)

Dopo il deploy dello stack, la verifica primaria consiste nel controllare che Prometheus stia effettuando lo scraping dei target attesi.

1. Rendere disponibile la UI di Prometheus (se non già attivo):

   ```bash
   kubectl -n dsbd port-forward svc/prometheus 9090:9090
   ```

2. Accedere a `http://localhost:9090`.

3. Aprire **Status → Targets**.

Nella sezione *Targets* ci si attende di osservare:

* i job **`data-collector`**, **`alert-system`**, **`alert-notifier`** con stato **UP**;
* target corrispondenti agli endpoint esposti dai rispettivi Service (role `endpoints`).

In caso di target *DOWN*, le verifiche più indicative sono:

* corretta esposizione dell’endpoint `GET /actuator/prometheus` nel Pod (configurazione Actuator);
* correttezza della selezione del Service tramite `relabel_configs` (match su `__meta_kubernetes_service_name`);
* raggiungibilità delle porte e disponibilità dell’applicazione (Pod in *Running/Ready*).

---

### 9.5 Query PromQL di riferimento

#### 9.5.1 Query COUNTER (rate/increase)

Esempi di query tipiche per metriche **COUNTER**:

* Rate delle richieste verso OpenSky al secondo (finestra 1m):

  ```promql
  rate(opensky_requests_total[1m])
  ```

* Incremento notifiche generate dall’Alert System (finestra 5m):

  ```promql
  increase(alert_system_notifications_total[5m])
  ```

* Rate errori di invio email (finestra 5m):

  ```promql
  rate(email_send_errors_total[5m])
  ```

#### 9.5.2 Query GAUGE (valori istantanei e aggregazioni)

Esempi di query per metriche **GAUGE**:

* Durata (ms) dell’ultima chiamata OpenSky:

  ```promql
  opensky_last_fetch_duration_ms
  ```

* Durata (ms) dell’ultima valutazione soglie:

  ```promql
  alert_system_last_eval_duration_ms
  ```

* Massimo della durata di processing notifica (ms) osservato tra i target attivi:

  ```promql
  max(alert_notifier_last_processing_duration_ms)
  ```

#### 9.5.3 Query per label (service/node)

Poiché il progetto standardizza le label **`service`** e **`node`**, è possibile filtrare e aggregare in modo uniforme.

* Filtrare le metriche OpenSky prodotte dal solo *Data Collector*:

  ```promql
  opensky_requests_total{service="data-collector-service"}
  ```

* Confrontare, per nodo, la rate delle richieste OpenSky:

  ```promql
  sum by (node) (rate(opensky_requests_total[1m]))
  ```

* Aggregare, per servizio, gli errori di processing delle notifiche:

  ```promql
  sum by (service) (rate(alert_notifier_notifications_processing_errors_total[5m]))
  ```

## 10. Health Checks, Logs and Basic Diagnostics

### 10.1 Verifica della raggiungibilità dei servizi

La prima forma di diagnostica consiste nel verificare che i microservizi siano effettivamente **raggiungibili** e che stiano esponendo le API previste sulle porte attese.

In ambiente Kubernetes (cluster **kind**), l’accesso dall’host avviene tipicamente tramite **port-forward** verso i *Service* (approccio raccomandato), oppure — in alternativa — tramite un *client* esterno configurato sulla porta locale esposta dal port-forward.

Un controllo preliminare può essere effettuato direttamente dal terminale utilizzando `curl` oppure tramite strumenti come Postman.

* Verifica della raggiungibilità dell’**API Gateway** (dopo port-forward):

  ```bash
  curl -i http://localhost:<LOCAL_PORT>/
  ```

* Verifica della raggiungibilità dello **User Manager Service** (dopo port-forward):

  ```bash
  curl -i http://localhost:<LOCAL_PORT>/
  ```

* Verifica della raggiungibilità del **Data Collector Service** (dopo port-forward):

  ```bash
  curl -i http://localhost:<LOCAL_PORT>/
  ```

La risposta non deve necessariamente contenere un payload specifico; è sufficiente che il server risponda con un codice **2xx** o **3xx** per confermare che il processo sia in ascolto sulla porta indicata. Risposte `5xx` o errori di connessione (ad esempio *connection refused* o *timeout*) indicano un problema a livello di avvio del servizio, configurazione della porta o instradamento.

Oltre ai due microservizi core, nella versione estesa dell’architettura sono presenti ulteriori componenti esposti tramite HTTP, fra cui i microservizi **Alert System Service** e **Alert Notifier Service**. Per la sola verifica di raggiungibilità si possono utilizzare comandi analoghi verso i relativi *Service* (sempre mediante port-forward).

#### 10.1.1 Endpoint di health (se presenti) o semplice ping

Se gli **endpoint di health** (ad esempio basati su Spring Boot Actuator) sono abilitati, rappresentano il metodo preferenziale per verificare lo stato interno del servizio.

Esempio di chiamata ad un endpoint di health standard (dopo port-forward verso il servizio target):

```bash
curl -i http://localhost:<LOCAL_PORT>/actuator/health
```

Una risposta tipica, in caso di servizio *UP*, può essere del tipo:

```json
{
  "status": "UP"
}
```

Se gli endpoint di health non sono disponibili o non risultano abilitati, è possibile utilizzare un **semplice ping applicativo** verso un endpoint REST funzionale noto, ad esempio una richiesta di lettura con parametri controllati:

* per lo **User Manager Service**:

  ```bash
  curl -i "http://localhost:<LOCAL_PORT>/api/users/{emailDiTest}"
  ```

* per il **Data Collector Service**:

  ```bash
  curl -i "http://localhost:<LOCAL_PORT>/api/interests?userEmail={emailDiTest}"
  ```

In questo caso, anche una risposta `4xx` (ad esempio `404 Not Found` per utente inesistente) può essere considerata un segnale positivo, in quanto indica che il servizio è attivo e sta elaborando correttamente la richiesta.

---

### 10.2 Log dei microservizi

La seconda fonte di diagnostica è rappresentata dai **log applicativi** prodotti dai microservizi. In ambiente Kubernetes, i log dei container sono accessibili tramite `kubectl logs`.

#### 10.2.1 Accesso ai log via Kubernetes (`kubectl logs`)

Per consultare i log di un microservizio, è possibile riferirsi direttamente al relativo *Deployment*:

```bash
kubectl -n dsbd logs -f deploy/user-manager-service
kubectl -n dsbd logs -f deploy/data-collector-service
kubectl -n dsbd logs -f deploy/alert-system-service
kubectl -n dsbd logs -f deploy/alert-notifier-service
```

Il flag `-f` (*follow*) consente di seguire in tempo reale l’evoluzione dei log, utile soprattutto durante la fase di avvio o mentre si eseguono chiamate di test.

Se un Pod va in crash e viene riavviato, può essere utile recuperare i log del tentativo precedente:

```bash
kubectl -n dsbd logs pod/<POD_NAME> --previous
```

In presenza di più container nello stesso Pod, è possibile specificare il container:

```bash
kubectl -n dsbd logs -f pod/<POD_NAME> -c <CONTAINER_NAME>
```

#### 10.2.2 Principali messaggi informativi/di errore da tenere d’occhio

Tra i messaggi più rilevanti per la diagnostica si segnalano:

* i log di **avvio del contesto Spring Boot**, che indicano l’avvenuta inizializzazione del microservizio e l’apertura della porta HTTP di ascolto;
* i log relativi alle **migrazioni Flyway**, che segnalano l’esecuzione corretta degli script di migrazione del database o eventuali problemi di schema;
* i log di **connessione al database**, che evidenziano errori di autenticazione, indisponibilità dell’host o problemi di rete;
* i log relativi alle **invocazioni verso OpenSky**, con particolare attenzione allo stato delle risposte (codici HTTP, messaggi di errore, time‑out);
* eventuali **stack trace** di eccezioni non gestite, che possono fornire indicazioni preziose sulla causa di errori logici o di configurazione.
* i log del **producer Kafka** nel *Data Collector Service*, che confermano la pubblicazione degli eventi di aggiornamento delle finestre temporali verso il topic di input dell’Alert System;
* i log di **consumo ed elaborazione** nell’*Alert System Service*, che riportano la ricezione degli eventi e l’eventuale generazione delle notifiche di superamento soglia;
* i log relativi all’**invio delle notifiche e‑mail** nell’*Alert Notifier Service*, che esplicitano l’indirizzo del destinatario, l’oggetto del messaggio e l’esito della consegna verso il server SMTP configurato.

---

### 10.3 Diagnostica del database

La diagnostica del database PostgreSQL è utile per verificare che gli **schemi applicativi** siano stati creati correttamente e che le tabelle principali vengano popolate come previsto a seguito dell’esecuzione dei microservizi.

#### 10.3.1 Accesso a PostgreSQL (via CLI o client esterno)

Per accedere a PostgreSQL in ambiente Kubernetes, sono disponibili due modalità principali:

1. **Accesso via CLI all’interno del Pod** (approccio diretto, nessuna esposizione verso l’host):

```bash
kubectl -n dsbd exec -it deploy/postgres -- psql -U <POSTGRES_USER> -d <USER_DB_NAME>
```

Una volta connessi al *User DB* (`userdb`), è possibile elencare le tabelle disponibili:

```sql
\dt
```

Analogamente, per connettersi al *Data DB* (`datadb`):

```bash
kubectl -n dsbd exec -it deploy/postgres -- psql -U <POSTGRES_USER> -d <DATA_DB_NAME>
```

2. **Accesso da un client esterno** (ad esempio estensione DB su VS Code) tramite **port-forward** del *Service* PostgreSQL:

```bash
kubectl -n dsbd port-forward svc/postgres 15432:5432
```

A questo punto, il database è raggiungibile dall’host su `localhost:15432` con le credenziali configurate (utente/password) e i due database logici (`userdb`, `datadb`).

All’interno di ciascun database è possibile eseguire query di verifica sul contenuto delle tabelle principali, ad esempio:

```sql
SELECT * FROM users;
SELECT * FROM airports;
SELECT * FROM user_airport_interest;
SELECT * FROM flight_records;
```

Queste interrogazioni permettono di verificare se gli utenti, gli aeroporti, gli interessi e i record di volo risultano correttamente inseriti in seguito alle chiamate effettuate tramite le API esposte dai microservizi.

#### 10.3.2 Verifica della creazione automatica di schemi e tabelle (Flyway)

La creazione e l’evoluzione degli schemi del database sono gestite tramite **Flyway**, configurato all’interno dei microservizi. Le migrazioni vengono applicate automaticamente all’avvio dei servizi:

* lo **User Manager Service** applica le migrazioni relative allo schema utenti nel database `userdb`;
* il **Data Collector Service** applica le migrazioni relative a aeroporti, interessi e voli nel database `datadb`.

Nei log di avvio dei microservizi è possibile individuare i messaggi di Flyway che indicano l’esecuzione delle migrazioni, ad esempio:

```text
Flyway Community Edition x.x.x by Redgate
Successfully validated n migrations (execution time ...)
Current version of schema "public": n
Successfully applied n migrations to schema "public" (execution time ...)
```

In caso di errori (script non applicabili, conflitti di versionamento, problemi di permessi), Flyway riporterà dettagli specifici nei log, consentendo di intervenire rapidamente sulla correzione degli script o sulla configurazione del database.

Una ulteriore verifica può essere effettuata controllando la tabella `flyway_schema_history` all’interno dei database logici, per confermare l’avanzamento della versione applicata.

---

### 10.4 Diagnostica di Kafka, Mailtrap e Prometheus

La diagnostica dei componenti infrastrutturali consente di validare il corretto flusso degli eventi (Kafka), la consegna delle notifiche (Mailtrap) e la raccolta delle metriche (Prometheus).

#### 10.4.1 Verifica dei topic e dei messaggi tramite Kafka UI

Il broker Kafka è affiancato da una **Kafka UI**, che permette di ispezionare cluster, topic, partizioni e consumer group tramite interfaccia web.

1. Verificare che i Pod infrastrutturali siano in esecuzione:

   ```bash
   kubectl -n dsbd get pods -l app=zookeeper
   kubectl -n dsbd get pods -l app=kafka
   kubectl -n dsbd get pods -l app=kafka-ui
   ```

2. Esporre temporaneamente la Kafka UI tramite **port-forward**:

   ```bash
   kubectl -n dsbd port-forward svc/kafka-ui 8085:8080
   ```

3. Raggiungere l’interfaccia da browser all’indirizzo `http://localhost:8085` e:

   * verificare l’elenco dei **topic** configurati (in particolare quelli coinvolti nella pipeline di alerting);
   * controllare lo stato dei **consumer group** associati ai servizi;
   * ispezionare i messaggi più recenti per verificare che il payload JSON corrisponda alla struttura attesa.

Durante l’esecuzione di scenari di test è possibile osservare in tempo quasi reale l’aumento del numero di messaggi sui topic coinvolti e l’avanzamento degli offset dei consumer.

#### 10.4.2 Verifica dell’invio email tramite Mailtrap

L’invio delle notifiche email è gestito dal microservizio **Alert Notifier**, che utilizza un server SMTP esterno (Mailtrap) configurato tramite variabili d’ambiente `MAIL_*`.

Per verificare il corretto funzionamento dell’integrazione:

1. Accedere all’account Mailtrap utilizzato e selezionare la **Inbox** dedicata alle notifiche.

2. Eseguire uno scenario applicativo che porti alla generazione di una notifica (superamento soglia).

3. Monitorare i log dell’**Alert Notifier Service** per verificare l’esito dell’invio:

   ```bash
   kubectl -n dsbd logs -f deploy/alert-notifier-service
   ```

4. Verificare, all’interno della Inbox Mailtrap, la presenza del messaggio controllando mittente, destinatario, oggetto e corpo.

Se le email non risultano recapitate, è opportuno verificare che i parametri SMTP siano coerenti e che il microservizio stia effettivamente consumando i messaggi Kafka di notifica (correlando log applicativi e Kafka UI).

#### 10.4.3 Verifica scraping e metriche su Prometheus

Prometheus consente di verificare che le metriche esposte dai microservizi strumentati vengano correttamente raccolte (*scraped*) e rese interrogabili via PromQL.

1. Verificare che Prometheus sia in esecuzione:

   ```bash
   kubectl -n dsbd get pods -l app=prometheus
   ```

2. Esporre temporaneamente Prometheus tramite **port-forward**:

   ```bash
   kubectl -n dsbd port-forward svc/prometheus 9090:9090
   ```

3. Accedere all’interfaccia web su `http://localhost:9090` e:

   * aprire **Status → Targets** e verificare che i target risultino **UP**;
   * eseguire una query PromQL di base (ad esempio `up`) per controllare la disponibilità delle serie.

In presenza di target `DOWN`, è consigliato verificare (i) la corretta esposizione dell’endpoint `/actuator/prometheus` sul servizio target e (ii) la coerenza tra porte/Service/endpoint utilizzati nella configurazione di `prometheus.yml`.

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

La costruzione delle immagini avviene tramite **`docker build`** (BuildKit) sui *build context* dei singoli microservizi, tipicamente orchestrata dagli script presenti in `scripts/` (es. `build-images.sh` e `build-images.ps1`). **Docker Compose non è utilizzato** per la fase di build: ogni servizio viene buildato come unità autonoma a partire dal relativo `Dockerfile`.

**Sintomi tipici**

* Errori generici di BuildKit / build graph (es. `failed to solve: ...`).
* Errori sul contesto di build (es. `COPY failed: file not found in build context`).
* Errori Maven nello *stage* di build (es. dipendenze non risolvibili, timeout verso repository, certificati/proxy).
* Saturazione disco o cache Docker (es. `no space left on device`).

**Verifiche e rimedi**

1. Verificare che Docker sia avviato e operativo:

   ```bash
   docker version
   ```

2. Eseguire la build **tramite gli script di progetto** dalla root della repository (consigliato, perché uniforma naming e tag):

   *Linux/macOS (bash)*

   ```bash
   ./scripts/build-images.sh dev
   ```

   *Windows (PowerShell)*

   ```powershell
   ./scripts/build-images.ps1 -Tag dev
   ```

3. Se l’errore non è immediatamente identificabile, isolare la build su **un singolo servizio** per circoscrivere la causa (path, dipendenze, Dockerfile):

   ```bash
   docker build -t dsbd/user-manager-service:dev ./user-manager-service
   ```

   Ripetere per gli altri servizi aggiornando *image name* e *context directory*.

4. Verificare che le immagini risultino presenti in locale e con il tag atteso:

   ```bash
   docker images | grep dsbd/
   ```

5. In caso di errori sul contesto (`COPY failed` / file mancanti), verificare:

   * che il *build context* (ultimo argomento di `docker build`) punti alla directory corretta;
   * che i file richiesti dal `Dockerfile` esistano realmente (es. `pom.xml`, `src/`, `target/` se previsto);
   * l’assenza di regole `.dockerignore` che escludano file necessari;
   * la coerenza dei path usati nei `COPY` rispetto alla struttura del repository.

6. In caso di errori Maven nello stage di build, distinguere:

   * **problema applicativo** (compilazione/test): eseguire prima una build locale del servizio e risolvere gli errori:

     ```bash
     cd user-manager-service
     mvn clean package
     ```

   * **problema di rete/ambiente** (download dipendenze): verificare connettività, DNS, proxy aziendali/universitari e certificati.

7. Se si sospettano layer incoerenti o cache “sporca”, ripetere la build pulendo selettivamente le risorse di build (operazione potenzialmente distruttiva):

   ```bash
   docker builder prune -f
   docker system prune -f
   ```

#### 11.1.3 Immagini non disponibili nel cluster kind (mancato `kind load`)

Quando lo stack viene eseguito su un cluster *kind*, le immagini Docker costruite localmente **non** sono automaticamente disponibili all’interno dei nodi del cluster. In assenza di caricamento esplicito, i Pod possono rimanere in stato `ImagePullBackOff` / `ErrImagePull` anche se l’immagine è presente sul Docker daemon dell’host.

*Sintomi tipici*

* Pod in errore con eventi simili a:

  ```text
  Failed to pull image "...": rpc error: code = NotFound
  ImagePullBackOff
  ```

*Verifiche e rimedi*

1. Identificare il nome esatto dell’immagine referenziata dal Pod:

   ```bash
   kubectl -n dsbd describe pod <pod-name>
   ```

   Nella sezione **Containers** verificare il campo **Image** (tag incluso).

2. Verificare che l’immagine esista sul Docker daemon locale:

   ```bash
   docker images
   ```

3. Caricare l’immagine nel cluster *kind* (usando il nome cluster effettivo):

   ```bash
   kind get clusters
   kind load docker-image <image-name>:<tag> --name <cluster-name>
   ```

4. Se il Pod continua a non avviarsi, forzare la ricreazione (ad esempio riavviando il Deployment):

   ```bash
   kubectl -n dsbd rollout restart deploy/<deployment-name>
   ```

5. Verificare che il manifest Kubernetes utilizzi il tag corretto e una `imagePullPolicy` coerente (in genere `IfNotPresent` per immagini caricate in *kind*).

---

### 11.2 Problemi comuni in fase di run

Una volta completata la build e applicati i manifest Kubernetes, la fase di esecuzione può essere ostacolata da problemi legati al lifecycle dei Pod, alla configurazione (ConfigMap/Secret), alla connettività tra servizi o alle integrazioni esterne.

#### 11.2.1 Pod in CrashLoopBackOff / ConfigMap-Secret mancanti

*Sintomi tipici*

* Uno o più Pod risultano in stato `CrashLoopBackOff`, `Error` oppure non completano la fase di avvio (`Init:...`).
* In `Events` compaiono messaggi come:

  ```text
  configmap "..." not found
  secret "..." not found
  ```

*Verifiche e rimedi*

1. Identificare rapidamente i componenti in errore:

   ```bash
   kubectl -n dsbd get pods
   ```

2. Ispezionare il Pod per individuare la causa (eventi e dettaglio container):

   ```bash
   kubectl -n dsbd describe pod <pod-name>
   ```

3. Verificare l’esistenza delle risorse di configurazione nello stesso namespace:

   ```bash
   kubectl -n dsbd get configmap
   kubectl -n dsbd get secret
   ```

4. Se una risorsa risulta mancante, riapplicare i manifest (o la kustomization) e controllare eventuali errori di apply:

   ```bash
   kubectl apply -k k8s/
   ```

5. In caso di chiavi errate (ad esempio `configMapKeyRef` / `secretKeyRef` che puntano a key inesistenti), correggere i manifest e riavviare i Pod:

   ```bash
   kubectl -n dsbd rollout restart deploy/<deployment-name>
   ```

#### 11.2.2 Il database non si avvia correttamente

*Sintomi tipici*

* Il Pod di PostgreSQL non entra in stato `Running` oppure risulta `CrashLoopBackOff`.
* Le applicazioni falliscono in bootstrap con errori di connessione JDBC e conseguenti retry.

*Verifiche e rimedi*

1. Verificare lo stato del Pod e gli eventuali restart:

   ```bash
   kubectl -n dsbd get pods
   ```

2. Analizzare i log del database:

   ```bash
   kubectl -n dsbd logs -f deploy/postgres
   ```

3. Se il problema riguarda volumi o permessi (ad esempio mount non eseguito, path non valido), ispezionare la descrizione del Pod e le `Events`:

   ```bash
   kubectl -n dsbd describe pod <postgres-pod>
   ```

4. Verificare che le variabili critiche (es. password, database iniziali) siano presenti e correttamente iniettate tramite ConfigMap/Secret.

#### 11.2.3 I servizi non riescono a connettersi a PostgreSQL

*Sintomi tipici*

* Nei log dei microservizi compaiono errori del tipo:

  ```text
  Connection refused
  could not connect to server
  timeout expired
  ```

*Verifiche e rimedi*

1. Verificare che il Service del database esista e che esponga la porta attesa:

   ```bash
   kubectl -n dsbd get svc
   kubectl -n dsbd get endpoints postgres
   ```

2. Verificare che gli hostname/URL usati dai microservizi puntino al Service Kubernetes (tipicamente `postgres` o `postgres.<namespace>.svc`) e non a `localhost`.

3. Eseguire un check di connettività dal Pod del microservizio verso il database (se l’immagine contiene strumenti minimi):

   ```bash
   kubectl -n dsbd exec -it deploy/user-manager-service -- sh
   # all'interno del Pod:
   # nc -vz postgres 5432
   ```

4. Se il database è `Running` ma non accetta connessioni, verificare:

   * credenziali e DB logici (user/db) coerenti con la configurazione;
   * migrazioni Flyway e permessi utente;
   * readiness del Pod PostgreSQL (eventuale delay di inizializzazione).

#### 11.2.4 Problemi di connessione a Kafka

*Sintomi tipici*

* I microservizi producer/consumer non riescono a connettersi al broker (`bootstrap.servers`), con errori nei log relativi a `org.apache.kafka`.
* Kafka UI mostra consumer group inattivi o topic non popolati.

*Verifiche e rimedi*

1. Verificare lo stato dei Pod infrastrutturali:

   ```bash
   kubectl -n dsbd get pods
   ```

   In particolare, controllare che **Zookeeper** e **Kafka** risultino `Running`.

2. Controllare i log del broker Kafka:

   ```bash
   kubectl -n dsbd logs -f deploy/kafka
   ```

3. Verificare che i microservizi utilizzino come bootstrap server il Service Kubernetes del broker (ad esempio `kafka:9092`) e non indirizzi localhost.

4. Se Kafka UI è raggiungibile ma non elenca i topic attesi, verificare:

   * la configurazione del broker (listener/advertised listeners coerenti con il DNS interno);
   * eventuali errori di creazione automatica topic;
   * la presenza di errori applicativi che impediscono la produzione di messaggi.

#### 11.2.5 Errori di autenticazione verso OpenSky

*Sintomi tipici*

* Il *Data Collector Service* non riesce a ottenere un token OAuth2 o riceve risposte `401/403`.
* Nei log compaiono errori in fase di token request oppure durante le chiamate alle API.

*Verifiche e rimedi*

1. Verificare che le credenziali (`OPEN_SKY_CLIENT_ID`, `OPEN_SKY_CLIENT_SECRET`) siano state inserite nei Secret e siano effettivamente iniettate nel Pod:

   ```bash
   kubectl -n dsbd exec -it deploy/data-collector-service -- printenv | grep OPEN_SKY
   ```

2. Controllare che gli endpoint configurati (`OPEN_SKY_AUTH_BASE_URL`, `OPEN_SKY_API_BASE_URL`) siano corretti e aggiornati rispetto alla documentazione ufficiale di OpenSky.

3. Assicurarsi che l’account OpenSky associato alle credenziali sia attivo e abilitato all’utilizzo delle API richieste.

4. Esaminare i log completi del *Data Collector Service* durante il tentativo di autenticazione per individuare dettagli aggiuntivi restituiti dal server OAuth2 (errori di formato della richiesta, grant type non supportato, scope non valido).

5. In presenza di errori intermittenti dovuti a problemi di rete o di disponibilità del servizio OpenSky, valutare l’introduzione di meccanismi di retry e backoff (se non già presenti) o eseguire nuovamente il sistema in un momento successivo.

#### 11.2.6 Errori SMTP e mancato recapito delle email

*Sintomi tipici*

* Il *Alert Notifier Service* non invia email o fallisce durante l’invio.
* Le email attese non compaiono nella casella di destinazione (ad esempio in Mailtrap).

*Verifiche e rimedi*

1. Verificare che i parametri SMTP (host, porta, username, password, eventuali flag `MAIL_SMTP_AUTH` e `MAIL_SMTP_STARTTLS_ENABLE`) siano correttamente valorizzati in ConfigMap/Secret e coerenti con la configurazione del provider.

2. Controllare, tramite i log di *Alert Notifier*, se l’errore è legato alla connessione (es. *connection timed out*), all’autenticazione (es. *535 Authentication failed*) o al rifiuto del messaggio da parte del server (es. *550 Message rejected*). Queste informazioni orientano la correzione:

   * in caso di problemi di connessione, verificare la connettività in uscita e l’eventuale presenza di proxy/firewall;
   * in caso di errori di autenticazione, verificare credenziali e permessi dell’account SMTP;
   * in caso di rifiuto del messaggio, controllare indirizzo mittente/destinatario e policy del provider.

3. Se si utilizza Mailtrap, verificare dalla dashboard web che:

   * le credenziali SMTP siano aggiornate;
   * la casella di posta di test selezionata sia quella configurata in applicazione.

4. In presenza di errori persistenti, abilitare un livello di logging più dettagliato per il package di posta (`org.springframework.mail` e affini) e ripetere l’invio di una notifica di test, analizzando con attenzione il dettaglio dello stack trace.

#### 11.2.7 Prometheus non mostra metriche / target DOWN

*Sintomi tipici*

* La pagina **Status → Targets** mostra uno o più target in stato `DOWN`.
* Le query in Prometheus non restituiscono serie temporali, oppure mostrano solo metriche di Prometheus stesso.

*Verifiche e rimedi*

1. Verificare che Prometheus sia in esecuzione e raggiungibile via port-forward:

   ```bash
   kubectl -n dsbd get pods | grep prometheus
   kubectl -n dsbd port-forward svc/prometheus 9090:9090
   ```

2. Verificare che i microservizi espongano effettivamente l’endpoint `/actuator/prometheus`. Un controllo rapido può essere effettuato con un port-forward diretto, ad esempio:

   ```bash
   kubectl -n dsbd port-forward svc/data-collector-service 8082:8082
   curl http://localhost:8082/actuator/prometheus
   ```

3. Verificare che i Service Kubernetes espongano le porte corrette e che gli Endpoints siano popolati:

   ```bash
   kubectl -n dsbd get svc
   kubectl -n dsbd get endpoints data-collector-service
   ```

4. Se i target risultano `DOWN` per timeout o `connection refused`, controllare:

   * che il `metrics_path` configurato in Prometheus sia coerente (`/actuator/prometheus`);
   * che i nomi DNS e le porte dei target corrispondano ai Service interni;
   * che i Pod dei microservizi siano `Ready` (un Pod non pronto può rendere l’endpoint non raggiungibile).

---

### 11.3 Verifiche passo-passo per isolare gli errori

Le seguenti verifiche guidano un percorso **step-by-step** per isolare i problemi più comuni, partendo dallo strato di configurazione fino ai singoli Pod.

#### 11.3.1 Verifica variabili d’ambiente (ConfigMap/Secret)

1. Verificare che ConfigMap e Secret siano presenti nello stesso namespace di deploy e che contengano tutte le chiavi attese:

   ```bash
   kubectl -n dsbd get configmap
   kubectl -n dsbd get secret
   ```

2. Ispezionare una risorsa specifica per controllare valori e key:

   ```bash
   kubectl -n dsbd describe configmap <configmap-name>
   kubectl -n dsbd describe secret <secret-name>
   ```

3. Verificare che le variabili critiche siano effettivamente disponibili nel Pod (la verifica è particolarmente utile per individuare mismatch tra key e `*KeyRef`):

   ```bash
   kubectl -n dsbd exec -it deploy/data-collector-service -- printenv | egrep "DB_|KAFKA_|OPEN_SKY_|MAIL_"
   ```

4. In caso di Secret, ricordare che i valori sono memorizzati in base64: per controllare un valore specifico è possibile decodificare la key:

   ```bash
   kubectl -n dsbd get secret <secret-name> -o jsonpath='{.data.<KEY>}' | base64 -d
   ```

#### 11.3.2 Verifica delle porte occupate (port-forward)

1. Se un comando `kubectl port-forward` fallisce con errore di porta occupata (`address already in use`), verificare che la porta locale non sia già utilizzata:

   * su sistemi Unix-like:

     ```bash
     lsof -i :8080
     lsof -i :8081
     lsof -i :9090
     ```

   * su Windows:

     ```powershell
     netstat -ano | findstr ":8080"
     netstat -ano | findstr ":8081"
     netstat -ano | findstr ":9090"
     ```

2. Se la porta risulta occupata, è possibile:

   * terminare il processo che la utilizza;
   * oppure cambiare la porta locale mantenendo invariata quella remota (es. `18080:80`).

3. Dopo aver modificato la porta locale, aggiornare di conseguenza i client (Postman) e ogni configurazione che punta all’endpoint esposto.

#### 11.3.3 Controllo dei log dei singoli Pod

1. Individuare il Pod specifico (o il Deployment) che presenta problemi:

   ```bash
   kubectl -n dsbd get pods
   ```

2. Analizzare i log del componente:

   ```bash
   kubectl -n dsbd logs deploy/user-manager-service
   kubectl -n dsbd logs deploy/data-collector-service
   kubectl -n dsbd logs deploy/alert-system-service
   kubectl -n dsbd logs deploy/alert-notifier-service
   ```

3. Per osservare il comportamento in tempo reale, utilizzare l’opzione `-f`:

   ```bash
   kubectl -n dsbd logs -f deploy/data-collector-service
   ```

4. In presenza di restart, può essere utile ispezionare anche i log del container precedente:

   ```bash
   kubectl -n dsbd logs --previous deploy/data-collector-service
   ```

5. Prestare particolare attenzione a:

   * stack trace di eccezioni non gestite;
   * errori di connessione verso database, Kafka o servizi esterni;
   * errori di validazione legati a input non conformi;
   * mismatch di configurazione (variabili mancanti o con nomi errati).

#### 11.3.4 Verifica risorse Kubernetes (get/describe/events)

1. Ottenere una vista d’insieme delle risorse principali nel namespace:

   ```bash
   kubectl -n dsbd get all
   ```

2. Verificare lo stato dei Service e dei rispettivi Endpoints:

   ```bash
   kubectl -n dsbd get svc
   kubectl -n dsbd get endpoints
   ```

3. In caso di Pod non schedulati (`Pending`) o con errori non evidenti nei log, controllare gli eventi ordinati temporalmente:

   ```bash
   kubectl -n dsbd get events --sort-by=.lastTimestamp
   ```

4. Utilizzare `describe` per ottenere una diagnostica completa (spec, env, volumi, probes, eventi):

   ```bash
   kubectl -n dsbd describe pod <pod-name>
   kubectl -n dsbd describe deploy <deployment-name>
   ```

## 12. Validation Scenarios

### 12.1 Scenario minimo di smoke test

Lo scenario di *smoke test* ha l’obiettivo di verificare che l’intera piattaforma sia in grado di:

* avviarsi correttamente;
* accettare richieste di gestione utenti;
* registrare interessi utente–aeroporto;
* raccogliere e rendere disponibili dati di volo di base.

#### 12.1.1 Avvio del sistema

1. Assicurarsi che i prerequisiti hardware e software siano soddisfatti (Docker installato e operativo, **kind** e **kubectl** disponibili, risorse macchina adeguate) e che i file di configurazione Kubernetes siano stati valorizzati:

   * `k8s/config/01-configmap.yaml` (parametri *non sensibili*);
   * `k8s/config/02-secret.yaml` (credenziali e parametri *sensibili* in `stringData`).

2. Costruire le immagini Docker dei microservizi (dalla **root** della repository):

   *Linux/macOS (bash)*

   ```bash
   ./scripts/build-images.sh dev
   ```

   *Windows (PowerShell)*

   ```powershell
   ./scripts/build-images.ps1 -Tag dev
   ```

3. Creare (o verificare) il cluster *kind* (dalla **root** della repository):

   *Linux/macOS (bash)*

   ```bash
   ./scripts/kind/create-cluster.sh dsbd-local ./k8s/kind/kind-cluster.yaml
   ```

   *Windows (PowerShell)*

   ```powershell
   powershell -ExecutionPolicy Bypass -File scripts/kind/create-cluster.ps1 -ClusterName dsbd-local -ConfigFilePath k8s/kind/kind-cluster.yaml
   ```

4. Caricare le immagini nel cluster *kind* (dalla **root** della repository):

   *Linux/macOS (bash)*

   ```bash
   ./scripts/kind/load-images.sh dev dsbd-local
   ```

   *Windows (PowerShell)*

   ```powershell
   powershell -ExecutionPolicy Bypass -File scripts/kind/load-images.ps1 -Tag dev -Cluster dsbd-local
   ```

5. Deploy dello stack su Kubernetes tramite Kustomize:

   ```bash
   kubectl apply -k k8s
   ```

6. Verificare lo stato delle risorse:

   ```bash
   kubectl -n dsbd get pods
   ```

   Tutti i Pod devono risultare in stato `Running` con *Readiness* a `1/1` (o `N/N` in caso di più container).

7. Controllare rapidamente i log per accertarsi dell’assenza di errori gravi in fase di bootstrap:

   ```bash
   kubectl -n dsbd logs --tail=100 deploy/postgres
   kubectl -n dsbd logs --tail=100 deploy/user-manager-service
   kubectl -n dsbd logs --tail=100 deploy/data-collector-service
   ```

   In particolare, è opportuno verificare che:

   * PostgreSQL sia operativo e pronto ad accettare connessioni;
   * le migrazioni Flyway siano state applicate con successo;
   * i microservizi Spring Boot abbiano completato la fase di startup senza errori di configurazione.

#### 12.1.2 Creazione di un utente di test

1. Esporre localmente l’**API Gateway** tramite *port-forward* (mantenere il comando in esecuzione in un terminale dedicato):

   ```bash
   kubectl -n dsbd port-forward svc/api-gateway 8080:80
   ```

2. Inviare una richiesta `POST` all’endpoint di creazione utente, ad esempio tramite `curl` o Postman:

   ```bash
   curl -i -X POST "http://localhost:8080/api/users" \
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

5. Esporre localmente PostgreSQL (in un secondo terminale) per consentire la verifica tramite client SQL / estensione VSCode:

   ```bash
   kubectl -n dsbd port-forward svc/postgres 15432:5432
   ```

6. Controllare la tabella `users` sul database `userdb` per verificare la presenza dell’utente:

   ```sql
   SELECT id, email, name
   FROM users
   WHERE email = 'smoke.user@example.com';
   ```

   Deve risultare un singolo record coerente con i dati inviati.

#### 12.1.3 Registrazione di un interesse per un aeroporto

1. Se necessario, elencare gli aeroporti registrati nella tabella `airports` (database `datadb`) per scegliere un codice valido (ad esempio `LICC`, `LIMC`, `LIRF`, `LIML`).

2. Inviare una richiesta `POST` all’endpoint di registrazione dell’interesse (tramite API Gateway):

   ```bash
   curl -i -X POST "http://localhost:8080/api/interests" \
     -H "Content-Type: application/json" \
     -d '{
       "userEmail": "smoke.user@example.com",
       "airportCode": "LICC"
     }'
   ```

3. Verificare che la risposta sia `201 Created` e che il payload restituito contenga l’associazione utente–aeroporto attesa.

4. Controllare sul database `datadb` l’effettiva creazione dell’interesse nella tabella `user_airport_interest`:

   ```sql
   SELECT user_email, airport_code
   FROM user_airport_interest
   WHERE user_email = 'smoke.user@example.com'
     AND airport_code = 'LICC';
   ```

   Deve risultare un singolo record coerente con i dati appena inseriti.

#### 12.1.4 Verifica del popolamento dei dati di volo

1. Verificare, nei log del *Data Collector Service*, che siano attivi i job schedulati di raccolta dei voli da OpenSky e di persistenza nel database `datadb`:

   ```bash
   kubectl -n dsbd logs -f deploy/data-collector-service
   ```

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

4. In alternativa o in aggiunta, utilizzare l’API REST di interrogazione dei voli (descritta nello scenario 12.3) per ottenere i dati di volo relativi all’aeroporto monitorato, su una finestra temporale recente.

---

### 12.2 Scenario di test della politica at-most-once

Questo scenario verifica che la creazione di utenti sia *idempotente* rispetto all’indirizzo email, ovvero che più richieste di registrazione con gli stessi dati non producano duplicati nel database.

#### 12.2.1 Ripetizione di una registrazione utente

1. Assicurarsi che l’API Gateway sia esposto sull’host, come nello scenario 12.1.2.

2. Inviare una prima richiesta `POST` di creazione utente:

   ```bash
   curl -i -X POST "http://localhost:8080/api/users" \
     -H "Content-Type: application/json" \
     -d '{
       "email": "atmost.user@example.com",
       "name": "AtMostOnce User"
     }'
   ```

   La risposta attesa è `201 Created` e nel database `userdb` deve essere creato un nuovo record.

3. Ripetere la richiesta con **lo stesso** payload:

   ```bash
   curl -i -X POST "http://localhost:8080/api/users" \
     -H "Content-Type: application/json" \
     -d '{
       "email": "atmost.user@example.com",
       "name": "AtMostOnce User"
     }'
   ```

4. Verificare che la seconda risposta sia `200 OK` e che il corpo contenga i dati dell’utente già esistente, senza creare un nuovo record.

#### 12.2.2 Comportamento atteso (assenza di duplicati, codici HTTP attesi)

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

---

### 12.3 Scenario di interrogazione dei voli su intervalli temporali

Questo scenario verifica il corretto funzionamento dell’API di interrogazione dei voli su intervalli temporali, con particolare attenzione alla coerenza tra i dati restituiti e quelli memorizzati nel database `datadb`.

1. Assicurarsi che siano stati raccolti dati di volo per almeno un aeroporto (ad esempio `LICC`), come descritto nello scenario 12.1.4.

2. Identificare una finestra temporale di interesse (ad esempio le ultime due ore), espressa in UNIX timestamp o nel formato richiesto dall’API.

3. Inviare una richiesta `GET` all’endpoint di interrogazione dei voli (tramite API Gateway):

   ```bash
   curl -s "http://localhost:8080/api/flights?airport=LICC&from=<FROM_TS>&to=<TO_TS>"
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

---

### 12.4 Scenario di configurazione e valutazione delle soglie

Questo scenario ha l’obiettivo di verificare che il sistema gestisca correttamente la configurazione delle soglie di traffico associate agli interessi utente–aeroporto e che tali soglie vengano effettivamente considerate nella valutazione dei volumi di volo aggregati sulle finestre temporali.

#### 12.4.1 Creazione di un interesse con `highValue`/`lowValue`

1. Assicurarsi che lo stack applicativo sia in esecuzione, con in particolare attivi:

   * il *User Manager Service*;
   * il *Data Collector Service*;
   * l’*Alert System Service*;
   * il broker **Kafka**;
   * il database `datadb`.

2. Creare (o riutilizzare) un utente dedicato ai test di soglia, ad esempio:

   ```bash
   curl -i -X POST "http://localhost:8080/api/users" \
     -H "Content-Type: application/json" \
     -d '{
       "email": "threshold.user@example.com",
       "name": "Threshold User"
     }'
   ```

   La risposta attesa è `201 Created` alla prima invocazione ed `200 OK` alle eventuali invocazioni successive, in linea con la semantica *at-most-once* descritta nello scenario 12.2.

3. Scegliere un aeroporto tra quelli presenti nella tabella `airports`, ad esempio `LIMC` (Milano Malpensa) o `LIRF` (Roma Fiumicino).

4. Creare un interesse utente–aeroporto impostando esplicitamente i campi di soglia `highValue` e/o `lowValue`. A titolo di esempio, per configurare una soglia *alta* molto bassa (in modo da facilitare il superamento):

   ```bash
   curl -i -X POST "http://localhost:8080/api/interests" \
     -H "Content-Type: application/json" \
     -d '{
       "userEmail": "threshold.user@example.com",
       "airportCode": "LIMC",
       "highValue": 1,
       "lowValue": null
     }'
   ```

   In alternativa, è possibile configurare anche una soglia *bassa*:

   ```bash
   curl -i -X POST "http://localhost:8080/api/interests" \
     -H "Content-Type: application/json" \
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
   curl -s "http://localhost:8080/api/interests?userEmail=threshold.user@example.com"
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

#### 12.4.2 Generazione di un carico di voli che superi la soglia

1. Assicurarsi che esistano uno o più interessi con soglie impostate per l’aeroporto scelto, come descritto nel punto precedente.

2. Verificare che il *Flight Collection Scheduler* del *Data Collector Service* sia in esecuzione, controllando i log del Deployment:

   ```bash
   kubectl -n dsbd logs -f deploy/data-collector-service
   ```

   Nei log devono comparire periodicamente messaggi che indicano l’avvio e il completamento dei cicli di raccolta, con l’indicazione della finestra temporale elaborata.

3. Esporre **Kafka UI** tramite *port-forward* (in un terminale dedicato):

   ```bash
   kubectl -n dsbd port-forward svc/kafka-ui 8085:8080
   ```

   Accedere quindi all’interfaccia web all’indirizzo `http://localhost:8085`.

4. Monitorare il topic `to-alert-system` su cui il *Data Collector Service* pubblica gli eventi `FlightCollectionWindowUpdateEvent`:

   * selezionare il cluster configurato;
   * individuare il topic `to-alert-system`;
   * ispezionare i messaggi più recenti.

5. Per ciascun messaggio di tipo `FlightCollectionWindowUpdateEvent`, analizzare il campo `airports`, che contiene una lista di snapshot `AirportFlightsWindowSnapshot`. Per l’aeroporto configurato (ad esempio `LIMC`) verificare i campi:

   * `arrivalsCount`;
   * `departuresCount`.

   Il **numero totale di voli** considerato dall’Alert System è dato da:

   ```text
   totalFlights = arrivalsCount + departuresCount
   ```

6. Scegliere un messaggio in cui `totalFlights` risulti coerente con le soglie impostate. Ad esempio:

   * per testare una violazione *HIGH*, è opportuno che `totalFlights` sia **maggiore** di `highValue`;
   * per testare una violazione *LOW*, è opportuno che `totalFlights` sia **minore** di `lowValue`.

7. Osservare i log dell’*Alert System Service*:

   ```bash
   kubectl -n dsbd logs -f deploy/alert-system-service
   ```

   In corrispondenza dell’elaborazione di un evento con `totalFlights` fuori soglia devono essere presenti log di pubblicazione di una notifica di superamento soglia verso Kafka, con indicazione del tipo di violazione (`HIGH` o `LOW`), dell’aeroporto e dell’utente interessato.

8. Facoltativamente, verificare nuovamente da Kafka UI che sul topic `to-notifier` siano presenti messaggi `ThresholdBreachNotificationEvent` coerenti con le soglie e con i conteggi osservati sul topic `to-alert-system`.

---

### 12.5 Scenario end-to-end della pipeline di notifica

Questo scenario ha lo scopo di verificare il funzionamento end-to-end della pipeline di notifica, dalla pubblicazione degli aggiornamenti di traffico sul topic Kafka `to-alert-system` fino all’invio delle email di alert tramite l’*Alert Notifier Service* e l’infrastruttura SMTP configurata (ad esempio Mailtrap).

#### 12.5.1 Pubblicazione su `to-alert-system` e propagazione su `to-notifier`

1. Assicurarsi che siano soddisfatte le seguenti condizioni:

   * esista almeno un interesse con soglie configurate per un aeroporto, come descritto nello scenario 12.4;
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
   kubectl -n dsbd logs -f deploy/alert-system-service
   kubectl -n dsbd logs -f deploy/alert-notifier-service
   ```

   Nei log dell’Alert System devono comparire messaggi informativi relativi alla pubblicazione delle notifiche di superamento soglia; nei log dell’Alert Notifier devono comparire messaggi che attestano il consumo delle notifiche da Kafka e l’inoltro verso il servizio di posta.

#### 12.5.2 Verifica finale della ricezione email

1. Configurare correttamente le variabili d’ambiente SMTP (ad esempio Mailtrap) nel file `k8s/config/02-secret.yaml`, assicurandosi che i parametri:

   * `MAIL_HOST`;
   * `MAIL_PORT`;
   * `MAIL_USERNAME`;
   * `MAIL_PASSWORD`;
   * `MAIL_SMTP_AUTH`;
   * `MAIL_SMTP_STARTTLS_ENABLE`;
   * `MAIL_FROM`;

   siano coerenti con l’account di test utilizzato.

2. Riapplicare la configurazione e riavviare il Deployment dell’Alert Notifier per rendere effettive le modifiche:

   ```bash
   kubectl apply -k k8s
   kubectl -n dsbd rollout restart deploy/alert-notifier-service
   ```

3. Accedere all’interfaccia web del provider SMTP di test (ad esempio Mailtrap) e selezionare la inbox configurata per l’ambiente di sviluppo.

4. Generare un carico di voli che porti al superamento di almeno una soglia (`HIGH` o `LOW`) per uno degli interessi configurati, come descritto nello scenario 12.4.2:

   * attendere l’esecuzione di uno o più cicli del *Flight Collection Scheduler*;
   * verificare, se necessario, da Kafka UI che sul topic `to-notifier` siano presenti nuove notifiche di superamento soglia.

5. Controllare la inbox del provider SMTP di test e verificare la presenza di una o più email indirizzate all’utente configurato (ad esempio `threshold.user@example.com`). Per ciascuna email verificare che:

   * l’oggetto contenga un riferimento esplicito al tipo di violazione (`HIGH`/`LOW`) e all’aeroporto interessato;
   * il corpo del messaggio riporti in modo chiaro:

     * il codice dell’aeroporto;
     * il tipo di violazione;
     * il valore osservato (`actualValue`);
     * il valore di soglia (`thresholdValue`);
     * la finestra temporale (`windowBegin`–`windowEnd`) a cui si riferisce la valutazione.

6. Mantenendo aperti i log dell’*Alert Notifier Service*, verificare che l’invio delle email sia accompagnato da messaggi di log coerenti (costruzione del subject/body, tentativo di invio, eventuale gestione di errori SMTP).

---

### 12.6 Scenario con indisponibilità di OpenSky e Circuit Breaker attivo

Questo scenario verifica il comportamento del sistema in presenza di indisponibilità o forte degrado del servizio **OpenSky**, con particolare attenzione al corretto funzionamento del *Circuit Breaker* configurato sull’`OpenSkyClient` del *Data Collector Service* e all’impatto sulla pipeline di raccolta dati e di alerting.

1. Identificare la configurazione corrente di OpenSky, valorizzata tramite **Secret** Kubernetes (file `k8s/config/02-secret.yaml`) e iniettata nel Deployment del *Data Collector Service*. In particolare, risultano rilevanti le variabili:

   * `OPENSKY_AUTH_URL`;
   * `OPENSKY_API_URL`;
   * `OPENSKY_CLIENT_ID`;
   * `OPENSKY_CLIENT_SECRET`.

2. Per simulare l’indisponibilità di OpenSky, modificare temporaneamente il valore di `OPENSKY_API_URL` impostando un endpoint non raggiungibile (host inesistente o URL fittizia). Ad esempio:

   ```yaml
   OPENSKY_API_URL: https://invalid-opensky-endpoint.local/api
   ```

   *Nota operativa*: se il Secret è espresso tramite `stringData`, il valore può essere inserito in chiaro; se invece è espresso tramite `data`, occorre riportare il valore in **Base64**. In entrambi i casi, l’obiettivo è produrre nel cluster la stessa variabile d’ambiente con URL “non raggiungibile”.

3. Applicare la modifica nel cluster (stesso flusso di deploy usato per lo stack):

   ```bash
   kubectl apply -k k8s/stack
   ```

4. Riavviare il *Data Collector Service* affinché rilegga la configurazione aggiornata:

   ```bash
   kubectl -n dsbd rollout restart deploy/data-collector-service
   ```

5. Monitorare i log del *Data Collector Service*:

   ```bash
   kubectl -n dsbd logs -f deploy/data-collector-service
   ```

   Dopo alcuni cicli del *Flight Collection Scheduler* devono comparire messaggi che indicano errori nelle chiamate verso OpenSky (errori HTTP o di rete) e l’attivazione del **fallback** associato al *Circuit Breaker*, con evidenza del fatto che viene restituita una lista vuota di voli (o comunque un risultato degradato) per la finestra considerata.

6. Verificare che, nonostante i fallimenti verso OpenSky, il processo schedulato continui a essere eseguito regolarmente e che il microservizio **non vada in crash**. In particolare, non devono essere presenti eccezioni non gestite né arresti del contesto applicativo.

7. Controllare tramite **Kafka UI** il topic `to-alert-system` durante il periodo di indisponibilità simulata, utilizzando l’accesso via *port-forward*:

   ```bash
   kubectl -n dsbd port-forward svc/kafka-ui 8085:8080
   ```

   * `http://localhost:8085` → individuare il topic `to-alert-system` e ispezionare i messaggi più recenti.

   In funzione della logica implementata:

   * il *Data Collector Service* potrebbe **non pubblicare** nuovi eventi `FlightCollectionWindowUpdateEvent` in assenza di dati;
   * oppure potrebbe pubblicare eventi con snapshot `AirportFlightsWindowSnapshot` aventi conteggi `arrivalsCount` e `departuresCount` pari a **zero**, coerentemente con il fallback.

8. Verificare i log dell’*Alert System Service*:

   ```bash
   kubectl -n dsbd logs -f deploy/alert-system-service
   ```

   In corrispondenza degli eventuali eventi ricevuti da `to-alert-system`, il servizio deve:

   * evitare eccezioni legate a dati mancanti o nulli;
   * non generare notifiche spurie in assenza di traffico reale;
   * loggare correttamente l’assenza di violazioni di soglia (o l’assenza di interessi applicabili) sugli snapshot elaborati.

9. Interrogare l’API di lettura dei voli per uno degli aeroporti monitorati su una finestra temporale ricadente nel periodo di indisponibilità simulata. Utilizzando l’accesso via *API Gateway* (port-forward):

   ```bash
   kubectl -n dsbd port-forward svc/api-gateway 8080:80
   ```

   Eseguire quindi una richiesta coerente con gli endpoint esposti dal gateway, ad esempio:

   ```bash
   curl -s "http://localhost:8080/api/flights?airport=LIMC&from=...&to=..."
   ```

   La risposta deve contenere un numero di record coerente con il comportamento del fallback (tipicamente *assenza di nuovi voli registrati nel periodo di fault*).

10. Ripristinare la configurazione corretta di `OPENSKY_API_URL` nel Secret (file `k8s/config/02-secret.yaml`) e riapplicare lo stack:

```bash
kubectl apply -k k8s/stack
kubectl -n dsbd rollout restart deploy/data-collector-service
```

Dopo il ripristino, verificare dai log del *Data Collector Service* che le chiamate verso OpenSky tornino a completarsi con successo e che la raccolta periodica riprenda a popolare la tabella `flight_records` e, se configurate soglie adeguate, a produrre nuovamente eventi di aggiornamento verso Kafka.

### 12.7 Scenario di verifica del monitoring (metriche e label)

Questo scenario verifica che:

* i target Prometheus risultino *UP*;
* le metriche di tipo **COUNTER** e **GAUGE** siano effettivamente esposte e aggiornate a seguito di workload;
* le serie possano essere filtrate/aggregate per label riconducibili a *service* e *node* (tipicamente `job` e `instance` in Prometheus).

#### 12.7.1 Verifica target UP in Prometheus

1. Esporre Prometheus tramite *port-forward*:

   ```bash
   kubectl -n dsbd port-forward svc/prometheus 9090:9090
   ```

2. Accedere all’interfaccia Prometheus su `http://localhost:9090`.

3. Aprire **Status → Targets** e verificare che i job relativi ai microservizi applicativi risultino `UP`.

#### 12.7.2 Verifica COUNTER (incremento su workload)

1. Generare workload applicativo ripetendo più volte:

   * creazione utente (`POST /api/users`);
   * registrazione interesse (`POST /api/interests`);
   * interrogazione voli (`GET /api/flights?...`).

2. In Prometheus, eseguire query di tipo *rate* su una finestra temporale breve. Esempi:

   ```promql
   rate(opensky_requests_total[5m])
   ```

   ```promql
   rate(alert_system_evaluations_total[5m])
   ```

   ```promql
   rate(email_sent_total[5m])
   ```

3. In presenza di traffico, le query devono restituire valori *non nulli* (serie temporali valorizzate).

#### 12.7.3 Verifica GAUGE (valore istantaneo)

1. Eseguire query istantanee su metriche GAUGE di riferimento. Esempi:

   ```promql
   opensky_last_fetch_duration_ms
   ```

   ```promql
   alert_system_last_eval_duration_ms
   ```

   ```promql
   email_last_send_duration_ms
   ```

2. Dopo l’esecuzione di workload (o dopo l’elaborazione di notifiche), i valori devono risultare aggiornati e coerenti con l’ordine di grandezza atteso (unità *millisecondi*).

#### 12.7.4 Verifica label `service` e `node`

1. Verificare la disponibilità e l’utilizzabilità delle label (tipicamente `job` come identificatore di *service* e `instance` come identificatore di *node*):

   ```promql
   sum by (job) (rate(opensky_requests_total[5m]))
   ```

   ```promql
   sum by (job, instance) (rate(alert_system_notifications_total[5m]))
   ```

2. Verificare che il filtraggio per singolo servizio produca serie coerenti:

   ```promql
   rate(opensky_requests_total{job="data-collector"}[5m])
   ```

   ```promql
   rate(alert_notifier_notifications_consumed_total{job="alert-notifier"}[5m])
   ```

3. Se risultano presenti più repliche (*replicas* > 1), verificare che l’aggregazione per `instance` distingua correttamente le sorgenti e che l’aggregazione per `job` permetta di ottenere un valore complessivo a livello di servizio.
