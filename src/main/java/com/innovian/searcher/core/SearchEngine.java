package com.innovian.searcher.core;

import com.innovian.searcher.model.PatientFolder;
import com.innovian.searcher.model.SearchOptions;
import com.innovian.searcher.model.SearchResult;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Esegue la ricerca sull'indice gia' caricato.
 *
 * Modello di "positivita'": ogni PDF rappresenta una giornata di ricovero.
 * - logica OR: il PDF e' positivo se contiene almeno uno dei termini;
 * - logica AND: il PDF e' positivo se contiene tutti i termini.
 * Un paziente e' positivo se almeno un suo PDF e' positivo.
 */
public class SearchEngine {

    public List<SearchResult> search(PdfIndex index, SearchOptions options, ProgressListener listener) {
        List<SearchResult> results = new ArrayList<>();

        List<String> terms = new ArrayList<>();
        for (String t : options.getTerms()) {
            String norm = PdfIndexer.normalizeTerm(t);
            if (!norm.isEmpty()) {
                terms.add(norm);
            }
        }
        if (terms.isEmpty()) {
            return results;
        }

        Set<String> sdoFilter = normalizeSdoFilter(options.getSdoFilter());

        List<PatientFolder> patients = index.getPatients();
        int total = patients.size();
        int done = 0;

        for (PatientFolder patient : patients) {
            if (listener != null && listener.isCancelled()) {
                break;
            }
            done++;
            if (listener != null) {
                listener.onProgress(done, total, patient.getDisplayName());
            }

            if (!sdoFilter.isEmpty()
                    && !sdoFilter.contains(patient.getSdo().toLowerCase(Locale.ITALIAN))) {
                continue;
            }

            List<SearchResult.MatchedPdf> matched = new ArrayList<>();
            for (Path pdf : patient.getPdfFiles()) {
                List<String> words = index.getWords(pdf);
                if (words == null || words.isEmpty()) {
                    continue;
                }
                Set<String> termsFoundInPdf = matchPdf(words, terms, options);
                boolean positive = isPositive(termsFoundInPdf, terms, options.getLogic());
                if (positive) {
                    matched.add(new SearchResult.MatchedPdf(pdf, termsFoundInPdf));
                    if (options.isStopAtFirstMatch()) {
                        break;
                    }
                }
            }

            if (!matched.isEmpty()) {
                results.add(new SearchResult(patient, matched));
            }
        }

        return results;
    }

    private boolean isPositive(Set<String> found, List<String> terms, SearchOptions.Logic logic) {
        if (found.isEmpty()) {
            return false;
        }
        if (logic == SearchOptions.Logic.OR) {
            return true;
        }
        // AND: tutti i termini devono essere presenti nello stesso PDF.
        return found.size() == new HashSet<>(terms).size();
    }

    /** Restituisce i termini (normalizzati) trovati in questo PDF. */
    private Set<String> matchPdf(List<String> words, List<String> terms, SearchOptions options) {
        Set<String> wordSet = new HashSet<>(words);
        Set<String> found = new LinkedHashSet<>();

        for (String term : terms) {
            if (wordSet.contains(term)) {
                found.add(term);
                continue;
            }
            int allowed = Math.min(options.getMaxTypos(), Math.max(0, term.length() - 1));
            if (allowed <= 0) {
                continue;
            }
            for (String word : words) {
                if (Math.abs(word.length() - term.length()) > allowed) {
                    continue;
                }
                if (Levenshtein.withinDistance(term, word, allowed)) {
                    found.add(term);
                    break;
                }
            }
        }
        return found;
    }

    private Set<String> normalizeSdoFilter(Set<String> raw) {
        Set<String> out = new HashSet<>();
        if (raw == null) {
            return out;
        }
        for (String s : raw) {
            if (s == null) {
                continue;
            }
            String v = s.trim().toLowerCase(Locale.ITALIAN);
            if (!v.isEmpty()) {
                out.add(v);
            }
        }
        return out;
    }
}
