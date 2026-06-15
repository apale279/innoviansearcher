package com.innovian.searcher.core;

import com.innovian.searcher.model.PatientFolder;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Indice in memoria: per ogni PDF conserva l'insieme delle parole uniche
 * (in minuscolo). Cosi' una volta caricato, le ricerche successive sono
 * immediate e non occorre rileggere i file dal disco.
 */
public class PdfIndex {

    private final List<PatientFolder> patients = new ArrayList<>();
    private final Map<Path, List<String>> wordsByPdf = new HashMap<>();
    private final Map<Path, String> errorByPdf = new HashMap<>();

    public void addPatients(Collection<PatientFolder> p) {
        patients.addAll(p);
    }

    public List<PatientFolder> getPatients() {
        return patients;
    }

    public void putWords(Path pdf, List<String> words) {
        wordsByPdf.put(pdf, words);
    }

    public List<String> getWords(Path pdf) {
        return wordsByPdf.get(pdf);
    }

    public void putError(Path pdf, String error) {
        errorByPdf.put(pdf, error);
    }

    public Map<Path, String> getErrors() {
        return errorByPdf;
    }

    public int getPatientCount() {
        return patients.size();
    }

    public int getPdfCount() {
        int total = 0;
        for (PatientFolder p : patients) {
            total += p.getPdfFiles().size();
        }
        return total;
    }
}
