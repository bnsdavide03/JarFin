# 🧾 **Jarfin – Assistente Conversazionale per la Contabilità Personale**

Jarfin è un **assistente conversazionale** per la gestione della contabilità personale, progettato con **architettura a microservizi** e un modulo dedicato di **Natural Language Understanding (NLU)**.

A differenza delle classiche app finanziarie, Jarfin consente all’utente di interagire in modo **naturale**, tramite **testo o voce**, per registrare spese, consultare statistiche e ottenere report automatici.


## 🎯 Obiettivi del Progetto

Jarfin permette di:

* 📥 **Registrare spese ed entrate** con comandi naturali
  > *“Segna che ho speso 20 euro al supermercato”*

* 📊 **Consultare lo stato finanziario**
  > *“Quanto ho speso questa settimana?”*

* 📈 **Ottenere analisi e report**
  (Totali, categorie, proiezioni mensili, budget)

Il progetto nasce come **caso di studio accademico**, seguendo metodologie **Agile (SCRUM)** e documentazione **UML**.

&nbsp;

## 🏗️ Architettura del Sistema

Jarfin è basato su un ecosistema di **microservizi**, orchestrati tramite un **API Gateway**.

### Componenti principali

* **Client Application** (Web UI): Interfaccia Thymeleaf con supporto Input vocale (Web Speech API).
* **API Gateway**: Punto unico di accesso (Porta `8080`), routing e sicurezza.
* **NLU Service (Core)**: Analisi del linguaggio naturale, estrazione intenti e parametri.
* **Accounting Service**: Gestione CRUD transazioni e persistenza dati.
* **Analytics Service**: Calcolo statistiche, report e proiezioni di spesa.
* **Database**: PostgreSQL (Containerizzato).

&nbsp;

## 🧠 Algoritmi Principali

### 1️⃣ Parser NLU
Trasforma una frase naturale in un comando strutturato.

**Input**
```text
"Ho speso 15 euro al bar"
```

**Output**

```json
{
  "azione": "EXPENSE",
  "importo": 15.00,
  "categoria": "Bar",
  "data": "2026-02-04"
}
```

* **Tecnica**: Keyword matching & Regex extraction.
* **Complessità**: 

### 2️⃣ Aggregatore Dati (Analytics)

Genera report finanziari aggregando lo storico.

**Output esempio**

```json
{
  "totale_spese": 320.50,
  "saldo_netto": 179.50,
  "savings_rate": "15%",
  "alert_level": "GREEN"
}
```

* **Tecnica**: Aggregazione lineare con proiezioni temporali.
* **Complessità**: 

&nbsp;

## 🚀 Come Avviare il Progetto

Poiché si tratta di un sistema a microservizi, è necessario avviare **il Database** e poi **i 4 servizi Java** separatamente.

### 📋 Prerequisiti

Assicurati di avere installato:

* **Java 21** (JDK)
* **Docker Desktop** (per il database)
* **Maven**

### 1️⃣ Passo 1: Avvia il Database (Docker)

Apri un terminale nella cartella del progetto (dove c'è il file `docker-compose.yml` nella cartella `/Iterazione_0`) ed esegui:

```bash
docker compose up -d
```

*Verifica che il container `jarfin_db` sia attivo su Docker Desktop.*

### 2️⃣ Passo 2: Compila il Progetto

Prima di lanciare i servizi, assicuriamoci che tutto il codice sia compilato correttamente. Dalla cartella principale:

```bash
mvn clean install -DskipTests
```

### 3️⃣ Passo 3: Avvia i Microservizi

Hai due modi per farlo: **tramite IDE** (consigliato) o **tramite Terminale**.

#### 🔹 Metodo A: Tramite IDE (IntelliJ / Eclipse) - *Consigliato*

Apri il progetto nell'IDE. Cerca le classi `Application` dentro ogni modulo e avviale (Run) in questo ordine specifico:

1. ▶️ **Gateway** (`GatewayApplication`)
2. ▶️ **Accounting Service** (`AccountingServiceApplication`)
3. ▶️ **Analytics Service** (`AnalyticsServiceApplication`)
4. ▶️ **Web UI** (`WebUiApplication`)

*Attendi che nella console di ogni servizio appaia la scritta "Started ... in x seconds".*

#### 🔹 Metodo B: Tramite Terminale

Se preferisci il terminale, devi aprire **4 finestre separate** (una per ogni servizio) ed eseguire questi comandi:

* **Terminale 1 (Gateway):**
`cd jarfin-gateway` → `mvn spring-boot:run`
* **Terminale 2 (Accounting):**
`cd jarfin-accounting` → `mvn spring-boot:run`
* **Terminale 3 (Analytics):**
`cd jarfin-analytics` → `mvn spring-boot:run`
* **Terminale 4 (Web UI):**
`cd jarfin-web-ui` → `mvn spring-boot:run`

### 4️⃣ Passo 4: Accedi a Jarfin

Una volta che tutti e 4 i servizi sono attivi, apri il browser e vai su:

👉 **http://localhost:8083**

&nbsp;

## 📐 Metodologia e Toolchain

Il progetto adotta l'approccio **Agile Model Driven Development (AMDD)**, supportato da uno stack tecnologico moderno che copre l'intero ciclo di vita del software.

| Categoria | Tecnologia / Strumento | Scopo e Utilizzo |
| --- | --- | --- |
| **Core & Backend** | **Java 21 LTS** | Linguaggio di programmazione principale. |
|  | **Spring Boot 3.2** | Framework per microservizi (Web, Data JPA). |
|  | **Spring Cloud Gateway** | Orchestrazione e routing centralizzato delle richieste. |
| **Frontend & Voice** | **Thymeleaf** | Template engine per il rendering Server-Side. |
|  | **Bootstrap 5** | Stile e responsività dell'interfaccia utente. |
|  | **Web Speech API** | API native del browser per Input vocale (STT) e Sintesi (TTS). |
| **Dati & Infra** | **PostgreSQL 15** | Database relazionale per la persistenza dei dati. |
|  | **Docker & Compose** | Containerizzazione del DB e gestione dell'ambiente. |
| **Build & Dev** | **Apache Maven** | Gestione delle dipendenze e automazione della build. |
|  | **Visual Studio Code / Eclipse** | Ambienti di sviluppo integrato (IDE). |
| **Testing** | **JUnit 5 & Mockito** | Unit Testing e simulazione delle dipendenze (Mocking). |
|  | **Postman** | Testing manuale e validazione degli endpoint REST. |
| **Qualità (QA)** | **EclEmma** | Analisi della Code Coverage (Copertura dei test). |
|  | **STAN4J** | Analisi statica strutturale e metriche di qualità. |
|  | **SLF4J** | Logging unificato e strutturato. |
| **Design & PM** | **UMLet** | Modellazione diagrammi UML (Classi, Sequenza, Deployment). |
|  | **GitHub Projects** | Gestione Agile (Kanban Board), Issue Tracking e Versioning. |

&nbsp;

## 👥 Il Team

Progetto realizzato per il corso di *Metodologie di Analisi e Progettazione*:

* **Davide Bonsembiante**
* **Alessandro Biscaro**
* **Alessandro Rocco**