package com.innovian.searcher.model;

import java.util.List;
import java.util.Set;

/** Parametri scelti dall'utente per una ricerca. */
public class SearchOptions {

    public enum Logic {
        AND,
        OR
    }

    private final List<String> terms;
    private final Logic logic;
    private final int maxTypos;
    private final boolean stopAtFirstMatch;
    private final Set<String> sdoFilter;

    public SearchOptions(List<String> terms,
                         Logic logic,
                         int maxTypos,
                         boolean stopAtFirstMatch,
                         Set<String> sdoFilter) {
        this.terms = terms;
        this.logic = logic;
        this.maxTypos = maxTypos;
        this.stopAtFirstMatch = stopAtFirstMatch;
        this.sdoFilter = sdoFilter;
    }

    public List<String> getTerms() {
        return terms;
    }

    public Logic getLogic() {
        return logic;
    }

    public int getMaxTypos() {
        return maxTypos;
    }

    public boolean isStopAtFirstMatch() {
        return stopAtFirstMatch;
    }

    /** Insieme di SDO da considerare; se vuoto/null si cercano tutti i pazienti. */
    public Set<String> getSdoFilter() {
        return sdoFilter;
    }
}
