package com.innovian.searcher.model;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Rappresenta un paziente: una cartella (o due, nel caso delle diarie
 * infermieristiche) il cui nome ha il formato COGNOME_SDO.
 * Aggrega tutti i PDF appartenenti al paziente.
 */
public class PatientFolder {

    private final String sdo;
    private final String cognome;
    private final List<Path> folders = new ArrayList<>();
    private final List<Path> pdfFiles = new ArrayList<>();

    public PatientFolder(String cognome, String sdo) {
        this.cognome = cognome;
        this.sdo = sdo;
    }

    public String getSdo() {
        return sdo;
    }

    public String getCognome() {
        return cognome;
    }

    /** Nome leggibile COGNOME_SDO. */
    public String getDisplayName() {
        if (cognome == null || cognome.isEmpty()) {
            return sdo;
        }
        return cognome + "_" + sdo;
    }

    public List<Path> getFolders() {
        return folders;
    }

    public List<Path> getPdfFiles() {
        return pdfFiles;
    }

    public void addFolder(Path folder) {
        if (!folders.contains(folder)) {
            folders.add(folder);
        }
    }

    public void addPdf(Path pdf) {
        pdfFiles.add(pdf);
    }

    /** Cartella principale da aprire dall'interfaccia. */
    public Path getPrimaryFolder() {
        return folders.isEmpty() ? null : folders.get(0);
    }
}
