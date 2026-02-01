# 🧾 **Jarfin – Assistente Conversazionale per la Contabilità Personale**

Jarfin è un **assistente conversazionale** per la gestione della contabilità personale, progettato con **architettura a microservizi** e un modulo dedicato di **Natural Language Understanding (NLU)**.

A differenza delle classiche app finanziarie, Jarfin consente all’utente di interagire in modo **naturale**, tramite **testo o voce**, per registrare spese, consultare statistiche e ottenere report automatici.


## 🎯 Obiettivi del Progetto

Jarfin permette di:

* 📥 **Registrare spese ed entrate** con comandi naturali
  > “Segna che ho speso 20 euro al supermercato”

* 📊 **Consultare lo stato finanziario**
  > “Quanto ho speso questa settimana?”

* 📈 **Ottenere analisi e report**
  (totali, categorie, proiezioni, budget)

Il progetto nasce come **caso di studio accademico**, seguendo metodologie Agile e documentazione UML.


## 🏗️ Architettura del Sistema

Jarfin è basato su un ecosistema di **microservizi**, orchestrati tramite un **API Gateway**.

### Componenti principali

* **Client Application**
  * Interfaccia web/mobile
  * Input vocale o testuale

* **API Gateway**
  * Punto unico di accesso
  * Routing e sicurezza

* **Speech-to-Text (esterno)**
  * Conversione audio → testo

* **NLU Service (core)**
  * Analisi del linguaggio naturale
  * Estrazione intenti e parametri

* **Accounting Service**
  * Gestione transazioni e categorie

* **Analytics Service**
  * Statistiche, report e proiezioni

* **Database**
  * PostgreSQL


## 🧠 Algoritmi Principali

### 1️⃣ Parser NLU
Trasforma una frase naturale in un comando strutturato.

**Input**

```
"Ho speso 15 euro al bar"
```

**Output**

```json
{
  "azione": "crea_spesa",
  "importo": 15,
  "categoria": "Bar"
}
```

* Keyword matching
* Regular Expression per importi e date
* Mapping categorie
* Complessità: **O(n)**

---

### 2️⃣ Aggregatore Dati
Genera report finanziari a partire dallo storico delle transazioni.

**Output esempio**

```json
{
  "totale_spese": 320,
  "totale_entrate": 500,
  "spese_per_categoria": {
    "Cibo": 120,
    "Trasporti": 50,
    "Varie": 150
  }
}
```

* Aggregazione lineare
* Complessità: **O(N)**

---

## 📐 Metodologia e Documentazione
Il progetto segue **Agile Model Driven Development (AMDD)**:

* Iterazioni incrementali
* UML secondo modello **4+1 Views**
* Analisi statica e dinamica del codice
* Test automatizzati


## 🛠️ Toolchain

| Area            | Strumento          | Scopo                                                  |
| --------------- | ------------------ | ------------------------------------------------------ |
| Modellazione    | UMLet              | Diagrammi UML (Use Case, Classi, Componenti, Sequenza) |
| Versioning & PM | GitHub             | Repository, Kanban board, Pull Request                 |
| Backend         | Java + Spring Boot | Microservizi Jarfin                                    |
| Testing API     | Postman            | Verifica endpoint REST                                 |
| Unit Testing    | JUnit              | Test della logica applicativa                          |
| Code Coverage   | EclEmma            | Analisi dinamica dei test                              |
| Analisi Statica | STAN4J             | Metriche di qualità del codice                         |


## 🔄 Piano delle Iterazioni

* **Iterazione 0** – Requisiti e architettura
* **Iterazione 1** – Core contabilità
* **Iterazione 2** – Integrazione dei servizi e analytics
* **Iterazione finale** – NLU e Integrazione