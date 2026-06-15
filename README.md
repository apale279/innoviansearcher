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
   Consigliati **Chrome** o **Edge** (con dischi di rete e migliaia di cartelle).
2. **Passo 1 – Cartella ed elenco SDO**
   - Seleziona *Diarie mediche (1 cartella)* oppure *Diarie infermieristiche
     (2 cartelle)*.
   - Premi **"Scegli cartella…"** e seleziona la **cartella che contiene le
     cartelle dei pazienti** (`COGNOME_SDO`) — ad esempio la cartella `Daily`,
     anche su **disco di rete**. Accetta la richiesta di lettura del browser.
   - Incolla **il tuo elenco di SDO** (uno per riga o separati da virgola) e premi
     **"Verifica elenco SDO"**: l'app mostra **quali SDO sono stati trovati**
     (con cognome) e **quali no**. Con **"Esporta elenco (CSV)"** salvi questo
     riepilogo.
3. **Passo 2 – Ricerca del farmaco (perampanel)**
   - Il termine è già impostato su **`perampanel`** (puoi cambiarlo o aggiungerne
     altri separati da virgola).
   - Scegli **OR**/**AND**, gli **errori di battitura tollerati** (0–2) e se
     **fermarti al primo PDF positivo**. Maiuscole/minuscole sempre ignorate.
   - La ricerca viene fatta **sui pazienti del tuo elenco trovati al passo 1**
     (se l'elenco è vuoto, cerca in tutti). Premi **"Cerca"**: i risultati
     compaiono in tempo reale; con **"Ferma"** puoi interrompere.
4. **Passo 3 – Pazienti positivi**
   - Tabella con Cognome, SDO, **Prima assunzione**, numero di PDF positivi e
     termini trovati.
   - **Prima assunzione** = il giorno **precedente** alla creazione del PDF
     positivo più vecchio (il PDF viene generato alle 7 del mattino del giorno
     dopo, quindi la giornata documentata è quella prima).
   - **Clicca su una riga** per vedere la cartella, la prima data di assunzione e
     l'elenco dei PDF positivi con la rispettiva data; **clicca sul nome di un
     PDF** per aprirlo.
   - Con **"Esporta risultati (CSV)"** salvi l'elenco (apribile in Excel).

### Consigli con tante cartelle (es. 4000+ cartelle su disco di rete)

- Usa **Chrome** o **Edge**: l'app apre e legge le cartelle **una alla volta,
  solo quando servono**, senza caricare in memoria l'intero archivio.
- La ricerca **non blocca** il browser: legge i PDF in parallelo e mostra i
  risultati mentre procede.
- Se conosci gli **SDO** da controllare, incollali nel filtro: l'app apre e
  legge **solo** quelle cartelle ed è enormemente più veloce (decine di cartelle
  invece di migliaia).
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
| **Verifica elenco SDO** (trovati / non trovati) | Passo 1: confronta il tuo elenco con le cartelle e mostra/esporta gli SDO trovati e quelli mancanti. |
| **Prima data di assunzione** | Giorno precedente alla creazione del PDF positivo più vecchio (PDF generato alle 7 del giorno dopo). I PDF di ogni paziente vengono ordinati per data, così la prima positività è la più vecchia. |
| Una o due cartelle (mediche / infermieristiche) | Selettore di modalità; con due cartelle i pazienti con lo stesso SDO vengono uniti. |
| Barra di avanzamento | Mostrata durante la lettura dei PDF, con risultati in tempo reale. |
| Ricerca AND / OR | OR = almeno un termine; AND = tutti i termini nello stesso PDF. |
| Ignora maiuscole/minuscole | Sempre attivo. |
| Errore di scrittura fino a 2 lettere | Distanza di *Levenshtein* (regolabile 0–2). |
| Fermarsi alla prima positività o leggere tutto | Opzione "Fermati al primo PDF positivo per paziente". |
| Ricerca limitata all'elenco di SDO | La ricerca usa l'elenco del passo 1 (vuoto = tutti). |
| Lista dei positivi con collegamento | Tabella + apertura diretta dei PDF positivi + esportazione CSV. |

---

## Note e limiti (importanti)

- Per motivi di **sicurezza del browser**, una pagina HTML **non può aprire la
  cartella nel gestore file del sistema operativo**. Per questo i collegamenti
  aprono direttamente i **PDF positivi** (la cosa più utile) e viene mostrato il
  nome della cartella del paziente.
- Struttura attesa delle cartelle: `cartella scelta` → `COGNOME_SDO` → file PDF
  (esempio reale: `Daily` → `Abatiello_2118001262` → i PDF delle giornate).
  L'SDO è la parte dopo l'ultimo trattino basso `_`, quindi funzionano anche i
  cognomi con spazi (`Afeworki Berhe_2124016976`) o con più `_` (`DE_LUCA_999`).
- La **data di creazione del PDF** usata per la "prima data di assunzione" è la
  data di ultima modifica del file (`lastModified`), che per un PDF generato e
  mai più toccato coincide con la creazione. Se i file fossero stati copiati in
  modo da perdere la data originale, questo valore potrebbe non essere corretto.
- Tutto avviene in locale nel browser; con molti PDF la prima indicizzazione può
  richiedere un po' di tempo, ma poi la ricerca è immediata.

---

## Dettagli tecnici

- Una sola pagina: **`index.html`** (HTML + CSS + JavaScript, nessuna dipendenza
  da installare).
- Lettura PDF nel browser con **pdf.js** (caricata da CDN, poi in cache).
- Selezione cartelle con la **File System Access API** (`showDirectoryPicker`)
  su Chrome/Edge, con ripiego automatico su `webkitdirectory` sugli altri
  browser.
- Ricerca con tolleranza ai refusi tramite **distanza di Levenshtein** (≤ 2).
