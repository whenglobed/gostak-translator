package com.brbb.gostaktranslator;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * GUI for the Gostak Translator.
 */
public class TranslatorGui implements ActionListener {
    private static final String DEFAULT_DICTIONARY_FILE = "gostak_dictionary.txt";
    private static final String DEFAULT_HTML_OUTPUT = "gostak_output.html";
    private static final String DEFAULT_TXT_OUTPUT = "gostak_output.txt";
    private static final String DEMO_TEXT = "\"This is the delcot of tondam,"
            + " where gitches frike and duscats glake. Across from a tophthed"
            + " curple, a gomway deaves to kiloff and kirf, gombing a samilen"
            + " to its hoff. Crenned in the loff lutt are five glauds.\"";

    private String dictFileName;

    private JFrame mainWindow = new JFrame ("Gostakian Translator");
    private JTextArea inputText;
    private JTextPane outputText;
    private JScrollPane inputScrollPane;
    private JScrollPane outputScrollPane;
    private JPanel inputPanel;
    private JPanel buttonPanel;
    private JPanel outputButtonPanel;
    private JButton translateButton;
    private JButton translateFileButton;
    private JPanel htmlOrTxtPanel;
    private JLabel htmlOrTxtLabel;
    private JRadioButton htmlButton;
    private JRadioButton txtButton;
    private ButtonGroup htmlOrTxt;

    /**
     * No-argument constructor.
     */
    public TranslatorGui() {
        dictFileName = DEFAULT_DICTIONARY_FILE;
        setupComponents();
    }

    /**
     * Constructor with a specified dictionary file name.
     * 
     * @param dictFileName the dictionary file name
     */
    public TranslatorGui(String dictFileName) {
        this.dictFileName = dictFileName;
        setupComponents();
    }

    /**
     * Initializes and arranges GUI components.
     */
    private void setupComponents() {
        mainWindow.setLayout(new BoxLayout(mainWindow.getContentPane(), BoxLayout.Y_AXIS));

        inputText = new JTextArea();
        inputText.setLineWrap(true);
        inputText.setWrapStyleWord(true);
        inputText.setText(DEMO_TEXT);

        outputText = new JTextPane();
        outputText.setEditable(false);

        inputScrollPane = new JScrollPane(inputText, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        inputScrollPane.setBorder(new TitledBorder("Input text:"));
        outputScrollPane = new JScrollPane(outputText, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        outputScrollPane.setBorder(new TitledBorder("Translation:"));

        inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(inputScrollPane, BorderLayout.CENTER);

        translateButton = new JButton("Translate Text");
        translateButton.addActionListener(this);
        translateFileButton = new JButton("Translate File");
        translateFileButton.addActionListener(this);

        htmlOrTxtLabel = new JLabel("Output type:", JLabel.CENTER);
        htmlButton = new JRadioButton(".html", true);
        txtButton = new JRadioButton(".txt", false);
        htmlOrTxt = new ButtonGroup();
        htmlOrTxt.add(htmlButton);
        htmlOrTxt.add(txtButton);

        htmlOrTxtPanel = new JPanel(new BorderLayout());
        htmlOrTxtPanel.add(htmlOrTxtLabel, BorderLayout.NORTH);
        htmlOrTxtPanel.add(htmlButton, BorderLayout.WEST);
        htmlOrTxtPanel.add(txtButton, BorderLayout.EAST);

        buttonPanel = new JPanel();
        buttonPanel.add(translateButton);
        buttonPanel.add(translateFileButton);
        buttonPanel.add(htmlOrTxtPanel);

        outputButtonPanel = new JPanel();
        outputButtonPanel.setLayout(new BorderLayout());
        outputButtonPanel.add(buttonPanel, BorderLayout.NORTH);
        outputButtonPanel.add(outputScrollPane, BorderLayout.CENTER);

        mainWindow.add(inputPanel);
        mainWindow.add(outputButtonPanel);

        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setSize(400, 400);
        mainWindow.setLocationRelativeTo(null); // Center on screen.
        mainWindow.setVisible(true);
    }


    /**
     * Handles actions.
     * 
     * @param e the event that triggered this action
     */
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        if (command.equals("Translate Text")) {
            String input = inputText.getText();
            try {
                Dictionary dictionary = new Dictionary(dictFileName);
                Parser p = new Parser(input, outputText, dictionary);
                p.parse();
            }
            catch (IOException exc) {
                String errorMessage = "Could not load dictionary file \"" + dictFileName + "\"";
                JOptionPane.showMessageDialog(mainWindow, errorMessage,
                        "Translation Failed", JOptionPane.WARNING_MESSAGE);
                exc.printStackTrace(); // LOG
            }

        }
        else if (command.equals("Translate File")) {
            translateFile();
        }
        else {
            System.err.printf("Error: unexpected action command %s\n", command); // LOG
        }
    }


    /**
     * Prompts the user to choose input and output files, then translates the
     * input file.
     */
    private void translateFile() {
        try {
            JFileChooser fileChooser = new JFileChooser(System.getProperty("user.dir")) {
                private static final long serialVersionUID = 1L; // default ID

                // Prompts the user before overwriting an existing file
                // in a save dialog.
                @Override
                public void approveSelection() {
                    File f = getSelectedFile();
                    String name = f.getName();
                    if (f.exists() && getDialogType() == SAVE_DIALOG) {
                        int result = JOptionPane.showConfirmDialog(this,
                                "\"" + name + "\" already exists. Overwrite?",
                                "Overwrite existing file",
                                JOptionPane.YES_NO_OPTION);
                        if (result == JOptionPane.NO_OPTION || result == JOptionPane.CLOSED_OPTION) {
                            return;
                        }
                        else if (result == JOptionPane.YES_OPTION) {
                            super.approveSelection();
                        }
                    }
                    super.approveSelection();
                }
            };

            fileChooser.setDialogTitle("Translate a file");
            FileNameExtensionFilter txtLogFilter = new FileNameExtensionFilter(
                    ".txt or .log files", "txt", "log");
            fileChooser.setFileFilter(txtLogFilter);

            if (fileChooser.showDialog(mainWindow, "Translate File") == JFileChooser.APPROVE_OPTION) {
                File inFile = fileChooser.getSelectedFile();
                if (!inFile.exists()) {
                    throw new FileNotFoundException(inFile.getCanonicalPath());
                }
                String inputFileName = inFile.getCanonicalPath();
                fileChooser.setDialogTitle("Save output file");
                fileChooser.resetChoosableFileFilters();
                if (htmlButton.isSelected()) {
                    FileNameExtensionFilter htmlFilter = new FileNameExtensionFilter(
                            ".html files", "html");
                    fileChooser.setFileFilter(htmlFilter);
                    fileChooser.setSelectedFile(new File(DEFAULT_HTML_OUTPUT));
                }
                else if (txtButton.isSelected()) {
                    FileNameExtensionFilter txtFilter = new FileNameExtensionFilter(
                            ".txt files", "txt");
                    fileChooser.setFileFilter(txtFilter);
                    fileChooser.setSelectedFile(new File(DEFAULT_TXT_OUTPUT));
                }
                else {
                    System.err.println("Error: no output file type selected."); // LOG
                }

                try {
                    Thread.sleep(250, 0); // Slight delay before opening the save dialog.
                }
                catch (InterruptedException exc) {
                    exc.printStackTrace(); // LOG
                }

                if (fileChooser.showSaveDialog(mainWindow) == JFileChooser.APPROVE_OPTION) {
                    String outputFileName = fileChooser.getSelectedFile().getCanonicalPath();
                    try {
                        Dictionary dictionary = new Dictionary(dictFileName);
                        Parser p = new Parser(inputFileName, outputFileName, dictionary);
                        p.parse();
                    }
                    catch (IOException exc) {
                        String errorMessage = "Could not open dictionary file \""
                                + dictFileName + "\"";
                        JOptionPane.showMessageDialog(mainWindow, errorMessage,
                                "Translation Failed", JOptionPane.WARNING_MESSAGE);
                    }
                }
            }
        }
        catch (IOException exc) {
            String errorMessage = "Error opening file: " + exc.getMessage();
            JOptionPane.showMessageDialog(mainWindow, errorMessage,
                    "Translation Failed", JOptionPane.WARNING_MESSAGE);
            exc.printStackTrace(); // LOG
        }
    }

}