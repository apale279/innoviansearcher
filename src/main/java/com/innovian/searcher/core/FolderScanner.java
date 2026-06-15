package com.innovian.searcher.core;

import com.innovian.searcher.model.PatientFolder;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Analizza una o due cartelle radice e costruisce l'elenco dei pazienti.
 * Ogni sottocartella immediata della radice rappresenta un paziente con nome
 * nel formato COGNOME_SDO. Con due radici (diarie infermieristiche) i pazienti
 * con lo stesso SDO vengono uniti.
 */
public class FolderScanner {

    /**
     * @param roots una o due cartelle radice
     * @return mappa SDO -> paziente, in ordine alfabetico per nome cartella
     */
    public Map<String, PatientFolder> scan(List<Path> roots) throws IOException {
        Map<String, PatientFolder> patients = new LinkedHashMap<>();
        List<Path> patientDirs = new ArrayList<>();

        for (Path root : roots) {
            if (root == null || !Files.isDirectory(root)) {
                continue;
            }
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(root)) {
                for (Path child : stream) {
                    if (Files.isDirectory(child)) {
                        patientDirs.add(child);
                    }
                }
            }
        }

        patientDirs.sort(Comparator.comparing(p -> p.getFileName().toString().toLowerCase(Locale.ITALIAN)));

        for (Path dir : patientDirs) {
            String folderName = dir.getFileName().toString();
            String sdo = extractSdo(folderName);
            String cognome = extractCognome(folderName);
            String key = sdo.toLowerCase(Locale.ITALIAN);

            PatientFolder patient = patients.computeIfAbsent(key, k -> new PatientFolder(cognome, sdo));
            patient.addFolder(dir);
            collectPdfs(dir, patient);
        }

        return patients;
    }

    private void collectPdfs(Path dir, PatientFolder patient) throws IOException {
        List<Path> pdfs = new ArrayList<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path child : stream) {
                if (Files.isRegularFile(child)
                        && child.getFileName().toString().toLowerCase(Locale.ITALIAN).endsWith(".pdf")) {
                    pdfs.add(child);
                }
            }
        }
        pdfs.sort(Comparator.comparing(p -> p.getFileName().toString().toLowerCase(Locale.ITALIAN)));
        for (Path pdf : pdfs) {
            patient.addPdf(pdf);
        }
    }

    /** Estrae la parte dopo l'ultimo '_' come numero SDO. */
    public static String extractSdo(String folderName) {
        int idx = folderName.lastIndexOf('_');
        if (idx >= 0 && idx < folderName.length() - 1) {
            return folderName.substring(idx + 1).trim();
        }
        return folderName.trim();
    }

    /** Estrae la parte prima dell'ultimo '_' come cognome. */
    public static String extractCognome(String folderName) {
        int idx = folderName.lastIndexOf('_');
        if (idx > 0) {
            return folderName.substring(0, idx).trim();
        }
        return "";
    }
}
