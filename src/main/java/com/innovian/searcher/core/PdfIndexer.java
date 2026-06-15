package com.innovian.searcher.core;

import com.innovian.searcher.model.PatientFolder;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Costruisce l'indice estraendo il testo da tutti i PDF dei pazienti.
 * Comunica l'avanzamento tramite {@link ProgressListener}.
 */
public class PdfIndexer {

    private final FolderScanner scanner = new FolderScanner();

    /**
     * Analizza le cartelle radice ed estrae il testo di ogni PDF.
     *
     * @param roots    una o due cartelle radice
     * @param listener callback di avanzamento (puo' essere null)
     * @return indice pronto per la ricerca
     */
    public PdfIndex buildIndex(List<Path> roots, ProgressListener listener) throws IOException {
        Map<String, PatientFolder> patients = scanner.scan(roots);

        PdfIndex index = new PdfIndex();
        index.addPatients(patients.values());

        int total = index.getPdfCount();
        int done = 0;

        if (listener != null) {
            listener.onProgress(0, total, "Pazienti trovati: " + index.getPatientCount());
        }

        for (PatientFolder patient : index.getPatients()) {
            for (Path pdf : patient.getPdfFiles()) {
                if (listener != null && listener.isCancelled()) {
                    return index;
                }
                try {
                    List<String> words = extractWords(pdf);
                    index.putWords(pdf, words);
                } catch (Exception ex) {
                    index.putWords(pdf, new ArrayList<>());
                    index.putError(pdf, ex.getMessage() == null ? ex.toString() : ex.getMessage());
                }
                done++;
                if (listener != null) {
                    listener.onProgress(done, total,
                            patient.getDisplayName() + " - " + pdf.getFileName().toString());
                }
            }
        }
        return index;
    }

    /** Estrae il testo di un PDF e lo trasforma in elenco di parole uniche minuscole. */
    private List<String> extractWords(Path pdf) throws IOException {
        String text;
        try (PDDocument document = Loader.loadPDF(pdf.toFile())) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(true);
            text = stripper.getText(document);
        }
        return tokenize(text);
    }

    /** Spezza il testo in parole (lettere/cifre), in minuscolo, senza duplicati. */
    public static List<String> tokenize(String text) {
        Set<String> words = new LinkedHashSet<>();
        if (text == null) {
            return new ArrayList<>();
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                sb.append(Character.toLowerCase(c));
            } else if (sb.length() > 0) {
                words.add(sb.toString());
                sb.setLength(0);
            }
        }
        if (sb.length() > 0) {
            words.add(sb.toString());
        }
        return new ArrayList<>(words);
    }

    public static String normalizeTerm(String term) {
        return tokenizeSingle(term);
    }

    private static String tokenizeSingle(String term) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < term.length(); i++) {
            char c = term.charAt(i);
            if (Character.isLetterOrDigit(c)) {
                sb.append(Character.toLowerCase(c));
            }
        }
        return sb.toString();
    }
}
