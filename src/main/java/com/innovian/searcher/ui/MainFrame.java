package com.innovian.searcher.ui;

import com.innovian.searcher.core.PdfIndex;
import com.innovian.searcher.core.PdfIndexer;
import com.innovian.searcher.core.ProgressListener;
import com.innovian.searcher.core.SearchEngine;
import com.innovian.searcher.model.SearchOptions;
import com.innovian.searcher.model.SearchResult;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.table.AbstractTableModel;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/** Finestra principale dell'applicazione. */
public class MainFrame extends JFrame {

    private final JRadioButton modeMedical = new JRadioButton("Diarie mediche (1 cartella)", true);
    private final JRadioButton modeNursing = new JRadioButton("Diarie infermieristiche (2 cartelle)");

    private final JTextField dir1Field = new JTextField();
    private final JTextField dir2Field = new JTextField();
    private final JButton dir1Button = new JButton("Sfoglia\u2026");
    private final JButton dir2Button = new JButton("Sfoglia\u2026");

    private final JButton loadButton = new JButton("Carica indici");
    private final JProgressBar loadProgress = new JProgressBar();
    private final JLabel loadStatus = new JLabel("Nessun indice caricato.");

    private final JTextField termsField = new JTextField();
    private final JRadioButton logicAnd = new JRadioButton("AND (tutti i termini)");
    private final JRadioButton logicOr = new JRadioButton("OR (almeno un termine)", true);
    private final JSpinner typoSpinner = new JSpinner(new SpinnerNumberModel(2, 0, 2, 1));
    private final JCheckBox stopAtFirst =
            new JCheckBox("Fermati al primo PDF positivo per paziente", true);
    private final JTextArea sdoArea = new JTextArea(4, 20);

    private final JButton searchButton = new JButton("Cerca");
    private final JProgressBar searchProgress = new JProgressBar();
    private final JLabel resultStatus = new JLabel("Pronto.");

    private final ResultsTableModel resultsModel = new ResultsTableModel();
    private final JTable resultsTable = new JTable(resultsModel);
    private final JButton openFolderButton = new JButton("Apri cartella paziente");
    private final JButton exportButton = new JButton("Esporta risultati (CSV)");

    private PdfIndex currentIndex;

    public MainFrame() {
        super("InnovianSearcher \u2013 ricerca nei PDF dei pazienti");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        add(buildTopPanel(), BorderLayout.NORTH);
        add(buildResultsPanel(), BorderLayout.CENTER);

        wireEvents();
        updateModeState();

        setMinimumSize(new Dimension(820, 640));
        setSize(960, 720);
        setLocationRelativeTo(null);
    }

    private JPanel buildTopPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 6, 10));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 3, 3, 3);
        c.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;

        ButtonGroup modeGroup = new ButtonGroup();
        modeGroup.add(modeMedical);
        modeGroup.add(modeNursing);
        JPanel modePanel = new JPanel();
        modePanel.add(modeMedical);
        modePanel.add(modeNursing);

        c.gridx = 0;
        c.gridy = row;
        c.gridwidth = 3;
        c.weightx = 1;
        panel.add(titled(modePanel, "1) Tipo di cartelle"), c);
        row++;

        // Cartella 1
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = row;
        c.weightx = 0;
        panel.add(new JLabel("Cartella pazienti:"), c);
        c.gridx = 1;
        c.weightx = 1;
        panel.add(dir1Field, c);
        c.gridx = 2;
        c.weightx = 0;
        panel.add(dir1Button, c);
        row++;

        // Cartella 2
        c.gridx = 0;
        c.gridy = row;
        c.weightx = 0;
        panel.add(new JLabel("Seconda cartella (infermieristiche):"), c);
        c.gridx = 1;
        c.weightx = 1;
        panel.add(dir2Field, c);
        c.gridx = 2;
        c.weightx = 0;
        panel.add(dir2Button, c);
        row++;

        // Caricamento
        c.gridx = 0;
        c.gridy = row;
        c.weightx = 0;
        panel.add(loadButton, c);
        c.gridx = 1;
        c.weightx = 1;
        panel.add(loadProgress, c);
        c.gridx = 2;
        c.weightx = 0;
        panel.add(new JLabel(""), c);
        row++;

        c.gridx = 1;
        c.gridy = row;
        c.gridwidth = 2;
        c.weightx = 1;
        panel.add(loadStatus, c);
        row++;

        // Sezione ricerca
        c.gridx = 0;
        c.gridy = row;
        c.gridwidth = 3;
        panel.add(buildSearchPanel(), c);

        dir1Field.setEditable(false);
        dir2Field.setEditable(false);
        loadProgress.setStringPainted(true);
        return panel;
    }

    private JPanel buildSearchPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 3, 3, 3);
        c.fill = GridBagConstraints.HORIZONTAL;
        int row = 0;

        c.gridx = 0;
        c.gridy = row;
        c.weightx = 0;
        panel.add(new JLabel("Termini (separati da virgola):"), c);
        c.gridx = 1;
        c.gridwidth = 2;
        c.weightx = 1;
        panel.add(termsField, c);
        row++;

        ButtonGroup logicGroup = new ButtonGroup();
        logicGroup.add(logicAnd);
        logicGroup.add(logicOr);
        JPanel logicPanel = new JPanel();
        logicPanel.add(logicOr);
        logicPanel.add(logicAnd);
        logicPanel.add(new JLabel("   Errori di battitura tollerati:"));
        logicPanel.add(typoSpinner);

        c.gridx = 0;
        c.gridy = row;
        c.gridwidth = 3;
        c.weightx = 1;
        panel.add(logicPanel, c);
        row++;

        c.gridx = 0;
        c.gridy = row;
        c.gridwidth = 3;
        panel.add(stopAtFirst, c);
        row++;

        c.gridx = 0;
        c.gridy = row;
        c.gridwidth = 3;
        sdoArea.setLineWrap(true);
        JScrollPane sdoScroll = new JScrollPane(sdoArea);
        panel.add(titled(sdoScroll,
                "Filtro SDO (uno per riga o separati da virgola; vuoto = tutti i pazienti)"), c);
        row++;

        c.gridx = 0;
        c.gridy = row;
        c.gridwidth = 1;
        c.weightx = 0;
        panel.add(searchButton, c);
        c.gridx = 1;
        c.gridwidth = 2;
        c.weightx = 1;
        searchProgress.setStringPainted(true);
        panel.add(searchProgress, c);

        return titled(panel, "2) Ricerca");
    }

    private JPanel buildResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout(6, 6));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        resultsTable.setFillsViewportHeight(true);
        resultsTable.getColumnModel().getColumn(0).setPreferredWidth(180);
        resultsTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        resultsTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        resultsTable.getColumnModel().getColumn(3).setPreferredWidth(360);

        JPanel buttons = new JPanel();
        buttons.add(openFolderButton);
        buttons.add(exportButton);

        JPanel south = new JPanel(new BorderLayout());
        south.add(buttons, BorderLayout.WEST);
        south.add(resultStatus, BorderLayout.EAST);

        panel.add(titled(new JScrollPane(resultsTable), "3) Pazienti positivi alla ricerca"),
                BorderLayout.CENTER);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    private void wireEvents() {
        modeMedical.addActionListener(e -> updateModeState());
        modeNursing.addActionListener(e -> updateModeState());

        dir1Button.addActionListener(e -> chooseDirectory(dir1Field));
        dir2Button.addActionListener(e -> chooseDirectory(dir2Field));

        loadButton.addActionListener(e -> loadIndices());
        searchButton.addActionListener(e -> runSearch());

        openFolderButton.addActionListener(e -> openSelectedFolder());
        exportButton.addActionListener(e -> exportResults());

        resultsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    openSelectedFolder();
                }
            }
        });
    }

    private void updateModeState() {
        boolean nursing = modeNursing.isSelected();
        dir2Field.setEnabled(nursing);
        dir2Button.setEnabled(nursing);
        if (!nursing) {
            dir2Field.setText("");
        }
    }

    private void chooseDirectory(JTextField target) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Seleziona la cartella che contiene le cartelle dei pazienti");
        if (!target.getText().isEmpty()) {
            chooser.setCurrentDirectory(new File(target.getText()));
        }
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            target.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private List<Path> selectedRoots() {
        List<Path> roots = new ArrayList<>();
        if (!dir1Field.getText().trim().isEmpty()) {
            roots.add(new File(dir1Field.getText().trim()).toPath());
        }
        if (modeNursing.isSelected() && !dir2Field.getText().trim().isEmpty()) {
            roots.add(new File(dir2Field.getText().trim()).toPath());
        }
        return roots;
    }

    private void loadIndices() {
        List<Path> roots = selectedRoots();
        if (roots.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Seleziona almeno una cartella.", "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }
        for (Path r : roots) {
            if (!Files.isDirectory(r)) {
                JOptionPane.showMessageDialog(this,
                        "La cartella non esiste:\n" + r, "Attenzione", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        setBusy(true);
        loadProgress.setIndeterminate(true);
        loadProgress.setValue(0);
        loadStatus.setText("Analisi cartelle in corso\u2026");

        SwingWorker<PdfIndex, int[]> worker = new SwingWorker<>() {
            private String lastMessage = "";

            @Override
            protected PdfIndex doInBackground() throws Exception {
                PdfIndexer indexer = new PdfIndexer();
                ProgressListener listener = new ProgressListener() {
                    @Override
                    public void onProgress(int done, int total, String message) {
                        lastMessage = message;
                        publish(new int[]{done, total});
                    }

                    @Override
                    public boolean isCancelled() {
                        return MainFrame.this.cancelRequested;
                    }
                };
                return indexer.buildIndex(roots, listener);
            }

            @Override
            protected void process(List<int[]> chunks) {
                int[] last = chunks.get(chunks.size() - 1);
                int done = last[0];
                int total = last[1];
                if (total > 0) {
                    loadProgress.setIndeterminate(false);
                    loadProgress.setMaximum(total);
                    loadProgress.setValue(done);
                }
                loadStatus.setText("Indicizzazione " + done + "/" + total + "  \u2013  " + lastMessage);
            }

            @Override
            protected void done() {
                setBusy(false);
                loadProgress.setIndeterminate(false);
                try {
                    currentIndex = get();
                    int errors = currentIndex.getErrors().size();
                    loadProgress.setMaximum(Math.max(1, currentIndex.getPdfCount()));
                    loadProgress.setValue(currentIndex.getPdfCount());
                    loadStatus.setText("Indice pronto: " + currentIndex.getPatientCount()
                            + " pazienti, " + currentIndex.getPdfCount() + " PDF"
                            + (errors > 0 ? "  (" + errors + " PDF non leggibili)" : ""));
                } catch (Exception ex) {
                    loadStatus.setText("Errore durante il caricamento: " + ex.getMessage());
                    JOptionPane.showMessageDialog(MainFrame.this,
                            "Errore durante il caricamento degli indici:\n" + ex.getMessage(),
                            "Errore", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    private void runSearch() {
        if (currentIndex == null) {
            JOptionPane.showMessageDialog(this,
                    "Carica prima gli indici (pulsante \"Carica indici\").",
                    "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }
        List<String> terms = parseTerms(termsField.getText());
        if (terms.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Inserisci almeno un termine da cercare.",
                    "Attenzione", JOptionPane.WARNING_MESSAGE);
            return;
        }

        SearchOptions.Logic logic = logicAnd.isSelected()
                ? SearchOptions.Logic.AND : SearchOptions.Logic.OR;
        int maxTypos = (Integer) typoSpinner.getValue();
        boolean stop = stopAtFirst.isSelected();
        Set<String> sdoFilter = parseSdoFilter(sdoArea.getText());

        SearchOptions options = new SearchOptions(terms, logic, maxTypos, stop, sdoFilter);

        setBusy(true);
        searchProgress.setIndeterminate(false);
        searchProgress.setValue(0);
        resultStatus.setText("Ricerca in corso\u2026");
        resultsModel.setResults(new ArrayList<>());

        SwingWorker<List<SearchResult>, int[]> worker = new SwingWorker<>() {
            @Override
            protected List<SearchResult> doInBackground() {
                SearchEngine engine = new SearchEngine();
                ProgressListener listener = (done, total, message) -> publish(new int[]{done, total});
                return engine.search(currentIndex, options, listener);
            }

            @Override
            protected void process(List<int[]> chunks) {
                int[] last = chunks.get(chunks.size() - 1);
                if (last[1] > 0) {
                    searchProgress.setMaximum(last[1]);
                    searchProgress.setValue(last[0]);
                }
            }

            @Override
            protected void done() {
                setBusy(false);
                try {
                    List<SearchResult> results = get();
                    resultsModel.setResults(results);
                    searchProgress.setValue(searchProgress.getMaximum());
                    resultStatus.setText("Trovati " + results.size() + " pazienti positivi.");
                } catch (Exception ex) {
                    resultStatus.setText("Errore durante la ricerca: " + ex.getMessage());
                }
            }
        };
        worker.execute();
    }

    private void openSelectedFolder() {
        int viewRow = resultsTable.getSelectedRow();
        if (viewRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Seleziona un paziente nella tabella.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int modelRow = resultsTable.convertRowIndexToModel(viewRow);
        SearchResult result = resultsModel.getResultAt(modelRow);
        Path folder = result.getPatient().getPrimaryFolder();
        if (folder == null) {
            return;
        }
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(folder.toFile());
            } else {
                JOptionPane.showMessageDialog(this,
                        "Apertura cartelle non supportata su questo sistema.\nPercorso:\n" + folder,
                        "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Impossibile aprire la cartella:\n" + folder + "\n" + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportResults() {
        if (resultsModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Nessun risultato da esportare.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Salva i risultati");
        chooser.setSelectedFile(new File("risultati_ricerca.csv"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }
        File file = chooser.getSelectedFile();
        try (BufferedWriter w = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            w.write("Cognome;SDO;PDF positivi;Termini trovati;Cartella");
            w.newLine();
            for (SearchResult r : resultsModel.getResults()) {
                w.write(csv(r.getPatient().getCognome()) + ";"
                        + csv(r.getPatient().getSdo()) + ";"
                        + r.getMatchedPdfCount() + ";"
                        + csv(String.join(" ", r.getMatchedTerms())) + ";"
                        + csv(String.valueOf(r.getPatient().getPrimaryFolder())));
                w.newLine();
            }
            resultStatus.setText("Risultati esportati in: " + file.getAbsolutePath());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Errore durante l'esportazione:\n" + ex.getMessage(),
                    "Errore", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static String csv(String value) {
        if (value == null) {
            return "";
        }
        return value.replace(";", ",").replace("\n", " ").replace("\r", " ");
    }

    private boolean cancelRequested;

    private void setBusy(boolean busy) {
        loadButton.setEnabled(!busy);
        searchButton.setEnabled(!busy);
    }

    private static List<String> parseTerms(String text) {
        List<String> out = new ArrayList<>();
        if (text == null) {
            return out;
        }
        for (String part : text.split(",")) {
            String t = part.trim();
            if (!t.isEmpty()) {
                out.add(t);
            }
        }
        return out;
    }

    private static Set<String> parseSdoFilter(String text) {
        Set<String> out = new LinkedHashSet<>();
        if (text == null) {
            return out;
        }
        for (String part : text.split("[,;\\n\\r\\t ]+")) {
            String t = part.trim();
            if (!t.isEmpty()) {
                out.add(t);
            }
        }
        return out;
    }

    private static JPanel titled(JComponent inner, String title) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(title));
        p.add(inner, BorderLayout.CENTER);
        return p;
    }

    /** Modello tabella dei risultati. */
    private static class ResultsTableModel extends AbstractTableModel {
        private final String[] columns = {"Cognome", "SDO", "PDF positivi", "Termini trovati"};
        private List<SearchResult> results = new ArrayList<>();

        void setResults(List<SearchResult> results) {
            this.results = results;
            fireTableDataChanged();
        }

        List<SearchResult> getResults() {
            return results;
        }

        SearchResult getResultAt(int row) {
            return results.get(row);
        }

        @Override
        public int getRowCount() {
            return results.size();
        }

        @Override
        public int getColumnCount() {
            return columns.length;
        }

        @Override
        public String getColumnName(int column) {
            return columns[column];
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            SearchResult r = results.get(rowIndex);
            switch (columnIndex) {
                case 0:
                    return r.getPatient().getCognome();
                case 1:
                    return r.getPatient().getSdo();
                case 2:
                    return r.getMatchedPdfCount();
                case 3:
                    return String.join(", ", r.getMatchedTerms());
                default:
                    return "";
            }
        }
    }
}
