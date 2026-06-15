package com.innovian.searcher.model;

import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Esito positivo della ricerca per un singolo paziente. */
public class SearchResult {

    private final PatientFolder patient;
    private final List<MatchedPdf> matchedPdfs;
    private final Set<String> matchedTerms = new LinkedHashSet<>();

    public SearchResult(PatientFolder patient, List<MatchedPdf> matchedPdfs) {
        this.patient = patient;
        this.matchedPdfs = matchedPdfs;
        for (MatchedPdf m : matchedPdfs) {
            matchedTerms.addAll(m.getTerms());
        }
    }

    public PatientFolder getPatient() {
        return patient;
    }

    public List<MatchedPdf> getMatchedPdfs() {
        return matchedPdfs;
    }

    public Set<String> getMatchedTerms() {
        return matchedTerms;
    }

    public int getMatchedPdfCount() {
        return matchedPdfs.size();
    }

    /** Un PDF (= una giornata di ricovero) risultato positivo e i termini trovati. */
    public static class MatchedPdf {
        private final Path file;
        private final Set<String> terms;

        public MatchedPdf(Path file, Set<String> terms) {
            this.file = file;
            this.terms = terms;
        }

        public Path getFile() {
            return file;
        }

        public Set<String> getTerms() {
            return terms;
        }
    }
}
