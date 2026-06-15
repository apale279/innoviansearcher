package com.innovian.searcher.core;

/**
 * Distanza di Levenshtein con uscita anticipata: appena la distanza minima
 * possibile supera il limite consentito, interrompe il calcolo.
 * Serve per gestire gli errori di battitura (fino a N lettere) nelle ricerche.
 */
public final class Levenshtein {

    private Levenshtein() {
    }

    /**
     * Verifica se due parole distano al massimo {@code maxDistance} modifiche
     * (inserimento, cancellazione, sostituzione di un singolo carattere).
     *
     * @param a           prima parola (gia' in minuscolo)
     * @param b           seconda parola (gia' in minuscolo)
     * @param maxDistance numero massimo di errori tollerati
     * @return true se la distanza e' minore o uguale a maxDistance
     */
    public static boolean withinDistance(String a, String b, int maxDistance) {
        if (a.equals(b)) {
            return true;
        }
        int la = a.length();
        int lb = b.length();

        // La differenza di lunghezza e' un limite inferiore della distanza.
        if (Math.abs(la - lb) > maxDistance) {
            return false;
        }
        if (la == 0) {
            return lb <= maxDistance;
        }
        if (lb == 0) {
            return la <= maxDistance;
        }

        int[] prev = new int[lb + 1];
        int[] curr = new int[lb + 1];

        for (int j = 0; j <= lb; j++) {
            prev[j] = j;
        }

        for (int i = 1; i <= la; i++) {
            curr[0] = i;
            int rowMin = curr[0];
            char ca = a.charAt(i - 1);
            for (int j = 1; j <= lb; j++) {
                int cost = (ca == b.charAt(j - 1)) ? 0 : 1;
                int val = Math.min(Math.min(prev[j] + 1, curr[j - 1] + 1), prev[j - 1] + cost);
                curr[j] = val;
                if (val < rowMin) {
                    rowMin = val;
                }
            }
            // Se l'intera riga supera il limite non potremo piu' rientrare.
            if (rowMin > maxDistance) {
                return false;
            }
            int[] tmp = prev;
            prev = curr;
            curr = tmp;
        }
        return prev[lb] <= maxDistance;
    }
}
