package com.innovian.searcher;

import com.innovian.searcher.ui.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Punto di avvio dell'applicazione desktop. */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // In caso di problemi si usa l'aspetto predefinito.
        }
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}
