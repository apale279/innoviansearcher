# InnovianSearcher

Applicazione **desktop in Java** che gira **solo sul tuo computer**, in locale,
**senza alcun server** (nemmeno locale). Serve per cercare termini all'interno
dei PDF contenuti nelle cartelle dei pazienti.

Ogni paziente ha una sua cartella con dentro vari PDF; ogni PDF contiene i dati
di una giornata di ricovero. Le cartelle sono nominate nel formato
**`COGNOME_SDO`** (esempio: `ROSSI_12345`).

> I file dei pazienti restano sul tuo computer: l'app non invia nulla su Internet
> (il collegamento serve solo la prima volta, per scaricare i componenti necessari
> alla costruzione del programma).

---

## Cosa sa fare

- **Una o due cartelle**
  - *Diarie mediche*: selezioni **una** cartella.
  - *Diarie infermieristiche*: selezioni **due** cartelle (i pazienti con lo
    stesso numero SDO vengono uniti automaticamente).
  - Quando carichi gli indici, una **barra di avanzamento** mostra la progressione.

- **Ricerca dei termini con logica AND / OR**
  - **OR**: il paziente è positivo se almeno uno dei termini è presente.
  - **AND**: il paziente è positivo se in uno stesso PDF (una stessa giornata)
    sono presenti **tutti** i termini.
  - Le **maiuscole/minuscole vengono sempre ignorate**.
  - Per ogni termine si tiene conto dei **possibili errori di battitura fino a 2
    lettere** (impostazione regolabile da 0 a 2).

- **Comportamento alla prima positività**
  - *Fermati al primo PDF positivo per paziente*: appena trova una positività
    passa al paziente successivo (non legge gli altri PDF di quel paziente).
  - Se togli la spunta, controlla **tutti** i PDF del paziente e conta quante
    giornate risultano positive.

- **Filtro per numero di SDO**
  - Puoi incollare un elenco di SDO (uno per riga oppure separati da virgola):
    la ricerca si limita a quei pazienti. Se lasci il campo vuoto, cerca in tutti.

- **Elenco dei positivi con collegamento alla cartella**
  - I pazienti positivi compaiono in una tabella (Cognome, SDO, numero di PDF
    positivi, termini trovati).
  - Con **doppio clic** su una riga, oppure con il pulsante **"Apri cartella
    paziente"**, apri direttamente la cartella del paziente.
  - Puoi anche **esportare i risultati in un file CSV**.

---

## Come si avvia (guida per chi non programma)

### 1. Installa Java (una sola volta)

Serve **Java 17 o successivo**. Per verificare se è già installato, apri il
Prompt dei comandi (Windows) o il Terminale (Mac) e scrivi:

```
java -version
```

Se compare un numero **17** o superiore, sei a posto. Altrimenti scarica e
installa Java gratuito da: <https://adoptium.net> (scegli la versione "Temurin 21").

### 2. Avvia l'applicazione

Scarica/copia questa cartella sul tuo computer, poi:

- **Windows**: fai doppio clic sul file **`run.bat`**.
- **Mac / Linux**: fai doppio clic su **`run.sh`** (oppure, dal Terminale dentro
  la cartella, esegui `./run.sh`).

> Il **primo avvio** costruisce il programma e richiede un collegamento a
> Internet e qualche minuto. Dai successivi avvii l'app parte subito.

---

## Come si usa

1. **Tipo di cartelle**: scegli *Diarie mediche (1 cartella)* oppure *Diarie
   infermieristiche (2 cartelle)*.
2. Premi **"Sfoglia…"** e seleziona la cartella che contiene **le cartelle dei
   pazienti** (quelle con nome `COGNOME_SDO`). In modalità infermieristiche
   seleziona anche la seconda cartella.
3. Premi **"Carica indici"** e attendi che la barra arrivi in fondo.
4. Inserisci i **termini** da cercare separati da virgola (es. `febbre, tosse`).
5. Scegli **OR** o **AND**, regola gli **errori di battitura tollerati**, e
   decidi se **fermarti al primo PDF positivo**.
6. (Facoltativo) Incolla l'elenco di **SDO** da considerare.
7. Premi **"Cerca"**: i pazienti positivi compaiono nella tabella in basso.
8. Doppio clic su un paziente per **aprire la sua cartella**, oppure usa
   **"Esporta risultati (CSV)"**.

---

## Note tecniche

- Linguaggio: **Java** (interfaccia grafica Swing), nessun server.
- Lettura PDF: libreria **Apache PDFBox**.
- Ricerca con tolleranza ai refusi: **distanza di Levenshtein** (fino a 2 errori).
- Costruzione: **Maven** (incluso tramite *Maven Wrapper*, non serve installarlo).

### Avvio manuale (per utenti esperti)

```bash
./mvnw clean package          # genera target/InnovianSearcher.jar
java -jar target/InnovianSearcher.jar
```
