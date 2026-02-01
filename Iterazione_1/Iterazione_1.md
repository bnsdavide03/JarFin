# 📗 Documentazione Ufficiale: Iterazione 1 – Core Accounting

## 1. Decisioni Architetturali (Il "Perché")

In questa fase, abbiamo trasformato la *Logical View* dell'Iterazione 0 in codice eseguibile. Le scelte tecnologiche sono state guidate dai requisiti di **manutenibilità** e **scalabilità**.

### 1.1 Perché Spring Boot 3.2?

Abbiamo scelto Spring Boot come "orchestratore" del microservizio perché:

* **Auto-configurazione:** Riduce drasticamente il codice di setup (boilerplate), permettendoci di concentrarci sulla logica di business (le transazioni).
* **Embedded Server:** Include un server Tomcat preconfigurato, rendendo il microservizio un'unità indipendente e facilmente containerizzabile.

### 1.2 Perché Jakarta EE e JPA?

Invece di usare driver di basso livello o framework specifici come Hibernate in modo diretto (che creerebbero un forte accoppiamento), abbiamo utilizzato **Jakarta Persistence (JPA)**:

* **Astrazione (ORM):** JPA ci permette di interagire con il database usando oggetti Java (`Transaction`) invece di query SQL manuali. Questo riduce gli errori e rende il codice più leggibile.
* **Indipendenza dal DB:** Se decidessimo di passare da PostgreSQL a MySQL, non dovremmo cambiare una sola riga di codice Java, ma solo una proprietà nella configurazione.
* **Spring Data JPA:** Abbiamo adottato le interfacce `Repository` perché implementano automaticamente le operazioni CRUD standard, garantendo un'implementazione pulita e veloce.

### 1.3 Perché PostgreSQL 15?

A differenza di un database NoSQL, PostgreSQL è stato scelto per:

* **Proprietà ACID:** Fondamentali per un'app finanziaria. Garantiscono che una transazione sia salvata completamente o per nulla, evitando dati corrotti.
* **Integrità Referenziale:** Permette di definire vincoli precisi sui dati (es. importi non nulli).

<br>

## 2. Implementazione Dettagliata (Il "Come")

### 2.1 Il Modello dei Dati (`Transaction.java`)

L'entità è il cuore del sistema. Abbiamo utilizzato le annotazioni Jakarta Persistence per mappare la classe al database:

* `@Entity`: Indica a JPA che questa classe deve essere trasformata in una tabella.
* `@GeneratedValue(strategy = GenerationType.IDENTITY)`: Delega al database la creazione degli ID univoci, garantendo l'integrità anche in caso di accessi multipli.

### 2.2 Il Layer di Servizio (`TransactionService.java`)

Abbiamo isolato la logica di business dal Controller.

* **Decisione:** Il Service non conosce l'esistenza del Web o di HTTP. Si occupa solo di gestire le regole delle transazioni. Questo facilita il riutilizzo del codice (es. se volessimo aggiungere un'interfaccia a riga di comando).

### 2.3 Layer Controller (`TransactionController.java`)

Espone le funzionalità tramite API RESTful:

* **Scelta degli Endpoint:** Abbiamo seguito gli standard REST (POST per creare, GET per leggere, DELETE per eliminare) per rendere l'API intuitiva per il futuro sviluppo del Gateway e della UI.

<br>

## 3. Infrastruttura e Deploy

### 3.1 Dockerization (`docker-compose.yml`)

Abbiamo applicato il principio di **isolamento dell'ambiente**:

* **Container jarfin_db:** Invece di installare PostgreSQL sul PC, usiamo un'immagine Docker ufficiale. Questo assicura che ogni client/utilizzatore lavori esattamente con la stessa versione del database.
* **Environment Variables:** Nel file `application.properties`, abbiamo usato la sintassi `${VAR:default}`.
* **Decisione:** Questo permette di cambiare le credenziali del database (URL, password) al volo tramite Docker senza dover ricompilare il codice Java.

<br>

## 4. Validazione della Qualità

### 4.1 Unit Testing con JUnit e Mockito

In linea con l'AMDD, ogni incremento deve essere validato:

* **Mocking del Repository:** In `TransactionServiceTest.java`, abbiamo usato Mockito per "simulare" il database.
* **Decisione:** Questo ci permette di testare la logica del Service in millisecondi, senza dover effettivamente scrivere sul disco, rendendo il ciclo di sviluppo estremamente rapido.

<br>

## 5. Logica di Business e Interfacce REST

Il servizio segue il pattern **Controller-Service-Repository**:
- **Repository**: Interfaccia che estende `JpaRepository` per fornire metodi CRUD standard.
- **Service**: `TransactionService.java` incapsula la logica di business. Qui viene gestita la delega delle operazioni al repository.
- **Controller**: `TransactionController.java` espone gli endpoint RESTful sotto il path `/api/transactions`.

    - *POST*: Creazione transazione.
    - *GET*: Recupero lista completa.
    - *DELETE*: Rimozione tramite ID.

<br>

## 6. Tracciabilità dei Modelli (AMDD Check)

### 6.1 Validazione Empirica con Postman

Oltre ai test unitari, il sistema è stato validato tramite **Postman** per verificare il corretto funzionamento degli endpoint REST e la persistenza effettiva su PostgreSQL. Questa fase conferma l'integrità del flusso: `Client -> HTTP -> Controller -> Service -> DB`.

#### **Scenario A: Registrazione di una nuova spesa (POST)**

È stata simulata la creazione di una transazione finanziaria inviando un payload JSON al path `/api/transactions`.

* **Endpoint:** `POST http://localhost:8080/api/transactions`
* **Payload Inviato:**
```json
{
    "amount": -15.50,
    "description": "Pranzo di lavoro",
    "category": "Cibo",
    "date": "2024-05-20"
}

```

![Testing metodo con ...] (images/screen2.png)

* **Risultato:** HTTP 200 OK. Il sistema restituisce l'oggetto creato con l'ID autogenerato (es. `id: 1`), confermando il successo dell'operazione e il corretto intervento del driver PostgreSQL.

#### **Scenario B: Recupero dello storico (GET)**

Verifica della capacità del sistema di interrogare il database e restituire i dati strutturati.

* **Endpoint:** `GET http://localhost:8080/api/transactions`
* **Risultato:** HTTP 200 OK. Restituisce un array JSON contenente tutte le transazioni salvate.

#### **Scenario C: Rimozione di una transazione (DELETE)**

Test della funzionalità di eliminazione tramite Path Variable.

* **Endpoint:** `DELETE http://localhost:8080/api/transactions/1`
* **Risultato:** Messaggio di conferma: *"Transazione eliminata con successo: 1"*.

<br>

### 7. Analisi delle Decisioni Tecniche (Refinement)

* **Perché Hibernate (via JPA):** Abbiamo utilizzato l'implementazione Hibernate di JPA per la gestione automatica del database (`spring.jpa.hibernate.ddl-auto=update`). Questa scelta permette al sistema di adattare automaticamente lo schema delle tabelle se modifichiamo la classe `Transaction`, riducendo i tempi di manutenzione durante le iterazioni rapide.
* **Perché Jakarta Persistence API:** Utilizzare lo standard Jakarta (ex Java EE) garantisce che il nostro codice sia portabile. Se in futuro volessimo cambiare framework (es. passare da Spring Boot a Quarkus), le nostre annotazioni `@Entity` e `@Id` rimarrebbero valide.
* **Perché le Variabili d'Ambiente in Docker:** Nel file `docker-compose.yml`, abbiamo isolato il DB. La decisione di mappare la porta `5432:5432` permette sia la comunicazione interna tra container, sia l'accesso esterno per tool di debugging (come DBeaver o pgAdmin), facilitando la trasparenza dei dati durante lo sviluppo.