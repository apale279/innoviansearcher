package com.innovian.searcher.core;

/** Callback per comunicare lo stato di avanzamento (caricamento indici / ricerca). */
public interface ProgressListener {

    /**
     * @param done    elementi completati
     * @param total   elementi totali
     * @param message messaggio descrittivo (es. nome paziente/file in corso)
     */
    void onProgress(int done, int total, String message);

    /** Permette all'interfaccia di interrompere l'operazione. */
    default boolean isCancelled() {
        return false;
    }
}
