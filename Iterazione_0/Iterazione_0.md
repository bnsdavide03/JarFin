# 📘 Documentazione Ufficiale: Iterazione 0 – Project Jarfin

**Progetto:** Jarfin (Java ARtificial Financial INtelligence)
**Metodologia:** SCRUM + AMDD (Agile Model Driven Development)
**Fase:** Iterazione 0 – Envisioning & Setup

---

## 1. Visione del Prodotto

### 1.1 L'Ispirazione: "Il mito di J.A.R.V.I.S."

L'idea alla base di **Jarfin** nasce da una fantasia condivisa da molti appassionati di tecnologia: possedere un assistente intelligente, onnipresente e proattivo, simile al **J.A.R.V.I.S.** (*Just A Rather Very Intelligent System*) visto nei film di *Iron Man*. Nel contesto cinematografico, Tony Stark delega all'IA la gestione di compiti complessi, permettendogli di concentrarsi solo sulle decisioni strategiche.

### 1.2 Il Problema Reale

Nel mondo reale, la gestione delle **finanze personali** è l'opposto di questa fantasia:

* È frammentata su più app bancarie.
* Richiede l'inserimento manuale noioso di dati su file Excel.
* Manca di un'interfaccia naturale: bisogna navigare menu complessi per capire quanto si è speso.

### 1.3 La Soluzione: Jarfin

**Jarfin** risolve questo problema colmando il divario tra la gestione finanziaria rigida e l'interazione naturale. Non è solo un "gestore di spese", ma un assistente che:

1. **Capisce il linguaggio naturale (NLU):** L'utente può dire o scrivere "Ho speso 20 euro per la pizza" e il sistema comprende intento, importo e categoria.
2. **Centralizza la logica:** Aggrega dati e fornisce analytics avanzati attraverso un'architettura a microservizi.
3. **Elimina l'attrito:** Rende l'inserimento e la consultazione dei dati immediati, trasformando un dovere noioso in un'interazione fluida.


## 2. Analisi dei Requisiti

In questa fase di *Envisioning*, sono stati formalizzati i requisiti necessari per costruire il Core, l'Analytics e il modulo NLU.

### 2.1 Requisiti Funzionali (RF)

Questi requisiti definiscono *cosa* il sistema deve fare.

| ID | Requisito | Descrizione Formale | Priorità |
| --- | --- | --- | --- |
| **RF-01** | **Gestione Transazioni (CRUD)** | Il sistema deve permettere la creazione, lettura, aggiornamento e cancellazione di entrate e uscite finanziarie tramite API REST dedicate. | Alta |
| **RF-02** | **Parsing del Linguaggio Naturale** | Il modulo NLU deve accettare input testuali (es. "Pranzo 15€"), estrarre le entità (Importo: 15, Valuta: €, Categoria: Pranzo) e mapparle in transazioni strutturate. | Alta |
| **RF-03** | **Aggregazione Dati e Reporting** | Il sistema deve calcolare totali per periodo temporale e categoria, fornendo proiezioni finanziarie basate sui dati storici. | Media |
| **RF-04** | **Orchestrazione API Gateway** | Un singolo punto di ingresso deve gestire il routing delle richieste verso i microservizi di Contabilità, Analytics e NLU. | Alta |
| **RF-05** | **Output Strutturato** | Il sistema deve restituire report e risposte in formato JSON standardizzato per essere consumati da eventuali frontend o interfacce vocali. | Media |

### 2.2 Requisiti Non Funzionali (RNF)

Questi requisiti definiscono *come* il sistema deve comportarsi (Quality Attributes).

* **RNF-01 – Modularity (Architettura):** Il sistema deve essere basato su **Microservizi** indipendenti (Spring Boot) per garantire che il fallimento di un modulo (es. Analytics) non blocchi le funzionalità Core (es. Contabilità).
* **RNF-02 – Maintainability (Qualità del Codice):** Il codice deve rispettare i principi di **Clean Code**. È obbligatoria l'analisi statica (tramite STAN4J) e una copertura dei test (JUnit + EclEmma) adeguata.
* **RNF-03 – Scalability:** L'architettura deve supportare l'aggiunta di nuovi moduli (es. Speech-to-Text) senza rifattorizzare l'intero backend.
* **RNF-04 – Usability (Interazione):** Il parser NLU deve riconoscere comandi con una variabilità sintattica ragionevole (sinonimi, ordine delle parole diverso) per garantire un'esperienza "umana".
* **RNF-05 – Data Integrity:** Le transazioni finanziarie devono garantire consistenza; nessuna spesa deve essere persa o duplicata durante l'elaborazione asincrona.


## 3. Strategia di Modellazione: UML 4+1 View Model

Per gestire la complessità di un sistema a microservizi sviluppato in team, abbiamo adottato il modello **UML 4+1**.
Come specificato nella strategia di progetto, utilizziamo queste viste in modo ibrido: **2 viste per la struttura statica** e **3 viste per lo sviluppo dinamico e ibrido**.

### A. Viste Strutturali (Fondamenta del Progetto)

Queste viste definiscono "l'ossatura" del sistema e sono state prioritarie nell'Iterazione 0.

1. **Logical View (Vista Logica) – *Class Diagram***
* **Scopo:** Rappresenta le classi, le interfacce e le loro relazioni (ereditarietà, associazione).
* **Applicazione in Jarfin:** Definisce il *Domain Model* finanziario (Transazione, Categoria, Utente, Report) e i DTO per la comunicazione tra microservizi.

2. **Physical/Deployment View (Vista di Distribuzione) – *Deployment Diagram***
* **Scopo:** Mappa i componenti software sull'hardware o sull'infrastruttura di rete.
* **Applicazione in Jarfin:** Definisce come i microservizi (Container Spring Boot), il Database e l'API Gateway comunicano tra loro e su quali nodi (locali o cloud) risiedono.

3. **Development View (Vista di Sviluppo) – *Component Diagram***
* **Scopo:** Organizzazione del codice in moduli e gestione delle dipendenze.
* **Applicazione in Jarfin:** Gestita tramite la struttura multi-modulo Maven/Gradle e i branch di GitHub, separando chiaramente `Core`, `Analytics` e `NLU`.


### B. Viste per lo Sviluppo Ibrido (Comportamento e Processo)

Queste viste guidano l'implementazione delle funzionalità e i flussi di dati.

4. **Process View (Vista di Processo) – *Sequence Diagram***
* **Scopo:** Mostra l'interazione tra oggetti nel tempo e lo scambio di messaggi.
* **Applicazione in Jarfin:** Cruciale per modellare il flusso asincrono: *Utente -> API Gateway -> NLU -> Contabilità -> DB*. Mostra come una frase viene trasformata in un record database.

5. **Scenarios (Vista "+1") – *Use Case Diagram***
* **Scopo:** Unifica tutte le altre viste descrivendo le interazioni degli utenti (attori) con il sistema.
* **Applicazione in Jarfin:** Definisce i casi d'uso primari (es. "Inserimento spesa vocale", "Richiesta report mensile") che guidano i test e la validazione dei requisiti.