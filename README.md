# InnovianSearcher

Pagina **HTML** che cerca termini nei PDF delle cartelle dei pazienti.
Funziona **interamente nel tuo browser, in locale**: niente installazione,
niente programmi da configurare, **nessun server** (nemmeno locale).

Ogni paziente ha una cartella con dentro vari PDF (un PDF = una giornata di
ricovero). Le cartelle hanno nome **`COGNOME_SDO`** (es. `ROSSI_12345`).

> **I file restano sul tuo computer.** I PDF non vengono caricati da nessuna
> parte: vengono letti solo nella memoria del browser. L'unica cosa che arriva
> da Internet è la libreria che legge i PDF (`pdf.js`), e solo al primo utilizzo;
> poi resta nella cache del browser.

---

## Come si usa (niente da installare)

1. Apri il file **`index.html`** con un doppio clic (si apre nel browser).
   Va bene **Chrome**, **Edge** o **Firefox** aggiornati.
2. **Passo 1 – Scegli le cartelle**
   - Seleziona *Diarie mediche (1 cartella)* oppure *Diarie infermieristiche
     (2 cartelle)*.
   - Premi sul selettore e scegli la **cartella che contiene le cartelle dei
     pazienti** (`COGNOME_SDO`). Il browser chiederà conferma a leggere la
     cartella: accetta. Comparirà subito il numero di pazienti e di PDF trovati.
3. **Passo 2 – Imposta la ricerca**
   - Scrivi i **termini** separati da virgola (es. `febbre, tosse`).
   - Scegli **OR** (almeno un termine) o **AND** (tutti i termini nello stesso
     PDF). Le **maiuscole/minuscole sono sempre ignorate**.
   - Imposta gli **errori di battitura tollerati** (0, 1 o 2): tiene conto dei
     refusi (es. *febre* trova *febbre*).
   - Spunta o togli **"Fermati al primo PDF positivo per paziente"**.
   - (Facoltativo) incolla un elenco di **SDO** per limitare la ricerca a quei
     pazienti; lascia vuoto per cercarli tutti.
   - Premi **"Cerca"**. I PDF vengono letti **mentre** cerca: la barra mostra
     l'avanzamento (PDF letti, pazienti positivi, tempo stimato) e i risultati
     **compaiono man mano**. Con il pulsante **"Ferma"** puoi interrompere quando
     vuoi.
4. **Passo 3 – Pazienti positivi**
   - La tabella si riempie in tempo reale con Cognome, SDO, numero di PDF
     positivi e termini trovati.
   - **Clicca su una riga** per vedere il nome della cartella e l'elenco dei PDF
     (giornate) positivi; **clicca sul nome di un PDF per aprirlo** in una nuova
     scheda.
   - Con **"Esporta risultati (CSV)"** salvi l'elenco (apribile in Excel).

### Consigli con tante cartelle (es. 4000 PDF)

- La ricerca **non blocca** più il browser: legge i PDF in parallelo e mostra i
  risultati mentre procede.
- Se conosci gli **SDO** da controllare, incollali nel filtro: l'app legge
  **solo** quei pazienti ed è enormemente più veloce.
- Lasciando attivo **"Fermati al primo PDF positivo"**, appena un paziente
  risulta positivo l'app salta i suoi PDF restanti.
- Dopo la prima lettura i PDF restano **in cache** per la sessione: cambiare i
  termini e rifare la ricerca è quasi immediato.
- La primissima ricerca su molte migliaia di PDF richiede comunque qualche
  minuto (è il tempo di leggere tutti i file): l'avanzamento è sempre visibile.

---

## Le funzioni richieste

| Richiesta | Come funziona |
|---|---|
| Una o due cartelle (mediche / infermieristiche) | Selettore di modalità; con due cartelle i pazienti con lo stesso SDO vengono uniti. |
| Barra di avanzamento al caricamento | Mostrata durante la lettura dei PDF. |
| Ricerca AND / OR | OR = almeno un termine; AND = tutti i termini nello stesso PDF. |
| Ignora maiuscole/minuscole | Sempre attivo. |
| Errore di scrittura fino a 2 lettere | Distanza di *Levenshtein* (regolabile 0–2). |
| Fermarsi alla prima positività o leggere tutto | Opzione "Fermati al primo PDF positivo per paziente". |
| Filtro per numero di SDO | Campo dove incollare gli SDO (cartelle `COGNOME_SDO`). |
| Lista dei positivi con collegamento | Tabella + apertura diretta dei PDF positivi + esportazione CSV. |

---

## Note e limiti (importanti)

- Per motivi di **sicurezza del browser**, una pagina HTML **non può aprire la
  cartella nel gestore file del sistema operativo**. Per questo i collegamenti
  aprono direttamente i **PDF positivi** (la cosa più utile) e viene mostrato il
  nome della cartella del paziente.
- Struttura attesa delle cartelle: `cartella scelta` → `COGNOME_SDO` → file PDF.
  L'SDO è la parte dopo l'ultimo trattino basso `_` (così funzionano anche
  cognomi composti come `DE_LUCA_999`).
- Tutto avviene in locale nel browser; con molti PDF la prima indicizzazione può
  richiedere un po' di tempo, ma poi la ricerca è immediata.

---

## Dettagli tecnici

- Una sola pagina: **`index.html`** (HTML + CSS + JavaScript, nessuna dipendenza
  da installare).
- Lettura PDF nel browser con **pdf.js** (caricata da CDN, poi in cache).
- Selezione cartelle con l'attributo `webkitdirectory` dei browser.
- Ricerca con tolleranza ai refusi tramite **distanza di Levenshtein** (≤ 2).
