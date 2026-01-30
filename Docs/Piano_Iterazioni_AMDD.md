# Piano delle Iterazioni AMDD – Jarfin

## 1. Modello di Processo: SCRUM + AMDD

Il progetto **Jarfin** adotta un modello **SCRUM** integrato con i principi di **Agile Model Driven Development (AMDD)**. Lo sviluppo è iterativo e incrementale, con modellazione leggera iniziale e continuo affinamento dei modelli durante le iterazioni.

### Durata degli Sprint

Gli **sprint hanno durata di 2 settimane**, scelta che rappresenta un compromesso efficace tra produttività e impegni accademici del team. Ogni sprint produce un incremento funzionante del sistema.

### Riunioni

* **Sprint Planning (inizio sprint)**: definizione obiettivi, task e priorità.

* **Riunione settimanale di sincronizzazione (Scrum Week)** – ~1 ora:
  * Monitoraggio avanzamento lavori
  * Identificazione e risoluzione degli impedimenti
  * Riorganizzazione dei task in base alle priorità

* **Sprint Review & Retrospective (fine sprint)**:
  * Valutazione dell’incremento prodotto
  * Analisi di cosa ha funzionato e cosa migliorare


## 2. Organizzazione del Team SCRUM

Il progetto è sviluppato da un **team di tre membri**, che gestisce l’intero ciclo di vita del software (analisi, progettazione, sviluppo, test e documentazione).

### Membri del Team

* **Davide Bonsembiante**
* **Alessandro Biscaro**
* **Alessandro Rocco**

### Ruoli e Responsabilità

* **Scrum Master a rotazione**
  Il ruolo di Scrum Master viene ricoperto a rotazione da ciascun membro del team. Questo favorisce la comprensione condivisa del processo SCRUM e lo sviluppo di competenze organizzative e di facilitazione.

* **Product Owner condiviso**
  Il ruolo di Product Owner è condiviso tra tutti i membri. Le decisioni su backlog, priorità e funzionalità vengono prese collegialmente, garantendo una visione comune del prodotto.

* **Team di sviluppo**
  Tutti i membri partecipano attivamente allo sviluppo del codice. Non esistono ruoli rigidi, ma aree di maggiore competenza che possono variare nel tempo (backend, analytics, integrazione, testing).

* **Tester e utenti simulati**
  Ogni membro svolge anche attività di testing (manuale e automatizzato). In assenza di clienti esterni, il team assume il ruolo di utente finale, testando Jarfin come “cliente” per garantire feedback continuo e miglioramento costante.


## 3. Gestione dei Task e Collaborazione

* Tutti i task sono tracciati tramite **GitHub Project Board (Kanban)**.
* I task sono suddivisi in modo equo tra i membri, mantenendo flessibilità e collaborazione continua.

### Gestione del Codice

* Ogni sviluppatore lavora su un **branch dedicato**.
* Prima di ogni **Scrum Week**, il codice deve essere pushato sul branch personale.
* L’integrazione nel branch principale avviene solo dopo **revisione collettiva** tramite Pull Request.
* La revisione include una valutazione qualitativa e consente di individuare e risolvere rapidamente eventuali problemi.


## 4. Best Practices Agile adottate

* **Sviluppo Iterativo**: ogni sprint produce funzionalità complete e testabili.
* **Pair Programming**: utilizzato per componenti complessi (es. Parser NLU e algoritmi di aggregazione).
* **Test-first mindset**: scrittura di test unitari e test API prima o in parallelo allo sviluppo.
* **Code Review obbligatoria**: nessun commit diretto su `main`.
* **Feedback continuo**: il team testa il prodotto come utente finale a ogni incremento.


## 5. Toolchain di Progetto

| Area            | Strumento          | Scopo                                                  |
| --------------- | ------------------ | ------------------------------------------------------ |
| Modellazione    | UMLet              | Diagrammi UML (Use Case, Classi, Componenti, Sequenza) |
| Versioning & PM | GitHub             | Repository, Kanban board, Pull Request                 |
| Backend         | Java + Spring Boot | Microservizi Jarfin                                    |
| Testing API     | Postman            | Verifica endpoint REST                                 |
| Unit Testing    | JUnit              | Test della logica applicativa                          |
| Code Coverage   | EclEmma            | Analisi dinamica dei test                              |
| Analisi Statica | STAN4J             | Metriche di qualità del codice                         |


## 6. Piano delle Iterazioni (Roadmap AMDD)

### Iterazione 0 – Envisioning & Setup

**Obiettivo:** definire le basi del progetto.

* Raccolta requisiti funzionali e non funzionali
* Definizione dei casi d’uso
* Progettazione architettura a microservizi (Jarfin)
* Setup repository GitHub, board SCRUM e toolchain

**Output:** documentazione analisi requisiti e architetturale iniziale.


### Iterazione 1 – Core Contabilità

**Obiettivo:** realizzare il cuore funzionale di Jarfin.

* Implementazione microservizio di **Contabilità**
* API CRUD per entrate e spese
* Configurazione e integrazione database
* Test unitari del service layer
* Test manuali delle API con Postman

**Output:** gestione dati finanziari funzionante.


### Iterazione 2 – Analytics & Reporting

**Obiettivo:** elaborazione e analisi dei dati finanziari.

* Implementazione microservizio **Analytics**
* Sviluppo Algoritmo di aggregazione dati (totali, categorie, proiezioni)
* Analisi della complessità computazionale
* Validazione con dataset di test
* Prima analisi statica del codice

**Output:** report finanziari in formato JSON.


### Iterazione 3 – NLU, Integrazione & Rilascio

**Obiettivo:** interazione naturale e completamento del sistema.

* Implementazione microservizio **NLU**
* Parser NLU per estrazione intenti e parametri
* Integrazione Speech-to-Text
* Orchestrazione tramite API Gateway
* Analisi statica e dinamica finale
* Aggiornamento UML allo stato *as-built*
* Stesura guida utente

**Output:** sistema Jarfin completo, integrato e pronto al rilascio.


## 7. Pacchetti di Lavoro

Il progetto è suddiviso in **macro-pacchetti di lavoro**:

1. Analisi e requisiti
2. Architettura e modellazione UML
3. Core Contabilità
4. Analytics & Algoritmi
5. NLU e integrazione esterna
6. Testing e qualità
7. Documentazione

Ogni sprint assegna a ciascun membro un set di attività ben definito, garantendo equità, tracciabilità e avanzamento continuo del progetto.
