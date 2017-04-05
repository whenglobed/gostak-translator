package com.brbb.gostaktranslator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextPane;

import com.brbb.gostaktranslator.Definition.Category;

public class Parser {
    private Scanner sc;
    private PrintWriter outfile;
    private StringBuilder output;
    private JTextPane outputPane;
    private Dictionary dictionary;
    private String outputType;
    private ArrayList<String> suffixes;


    /**
     * Constructor. Prepares to parse a string and output the translation to
     * a JTextPane.
     * 
     * @param input the String to be parsed and translated
     * @param outputPane the JTextPane for the translated output
     * @param dictionary the Dictionary to use for translation
     */
    public Parser(String input, JTextPane outputPane, Dictionary dictionary) {
        outputType = "gui";
        sc = new Scanner(input);
        output = new StringBuilder(256);
        this.outputPane = outputPane;
        this.dictionary = dictionary;
        initializeSuffixes();
    }


    /**
     * Constructor. Prepares to parse input from the specified file and write
     * the translation to an output file.
     * 
     * @param inputFileName the filename for the input text
     * @param outputFileName the filename for the output file
     * @param dictionary the Dictionary to use for translation
     */
    public Parser(String inputFileName, String outputFileName, Dictionary dictionary) {
        try {
            if (outputFileName.endsWith(".html")) {
                outputType = "html";
            }
            else {
                outputType = "txt";
            }
            sc = new Scanner(new FileInputStream(inputFileName), "UTF-8");
            outfile = new PrintWriter(outputFileName, "UTF-8");
            output = new StringBuilder(256);
            if (outputType.equals("html")) {
                addOpeningHtml();
            }
            this.dictionary = dictionary;
            initializeSuffixes();
        }
        catch (IOException exc) {
            exc.printStackTrace(); // LOG
            System.exit(1);
        }
    }


    /**
     * Populates the suffix array with common suffixes.
     */
    private void initializeSuffixes() {
        suffixes = new ArrayList<String>();
        suffixes.add("able");
        suffixes.add("ing");
        suffixes.add("age");
        suffixes.add("ly");
        suffixes.add("ers");
        suffixes.add("er");
        suffixes.add("ed");
        suffixes.add("\'s"); // This needs to be added (and checked) before "s" by itself.
        suffixes.add("s");
    }


    /**
     * Reads input and outputs a translation.
     */
    protected void parse() {
        // TODO: Treat hyphens as delimiters (e.g. "glaud-with-roggler")
        while (sc.hasNextLine()) {
            Scanner lineScanner = new Scanner(sc.nextLine());
            while (lineScanner.hasNext()) {
                Token t = new Token();
                Definition d = null;
                boolean wasTranslated = false;

                // Get next token.
                t.gostakian = lineScanner.next();

                t.trimAndRememberPunct();
                t.rememberFirstCapital();

                t.gostakian = t.gostakian.toLowerCase();

                // Attempt to find the token in the dictionary.
                d = dictionary.translate(t.gostakian);
                if (d != null) {
                    t.english.append(d.translation);
                    t.category = d.category;
                    wasTranslated = true;
                }
                if (!wasTranslated) {
                    // Try to remove suffixes (remembering them so they can be re-added later)
                    // and match shortened versions of the token.
                    // TODO: Handle cases where the word has a suffix and a possessive, e.g.
                    // "the glaud-with-roggler's juffet")
                    for (String suffix : suffixes) {
                        Pattern p = Pattern.compile(suffix + "$");
                        Matcher m = p.matcher(t.gostakian);
                        if (m.find()) {
                            StringBuilder trimmed = new StringBuilder(t.gostakian);

                            while (!wasTranslated && trimmed.length() > 1) {
                                trimmed.deleteCharAt(trimmed.length() - 1);

                                d = dictionary.translate(trimmed.toString());
                                if (d != null) {
                                    t.english.append(d.translation);
                                    t.hasSuffix = true;
                                    t.suffix = new StringBuilder(suffix);
                                    t.category = d.category;
                                    wasTranslated = true;
                                }
                            }
                            if (wasTranslated) {
                                break; // Stop looking for suffixes.
                            }
                        }
                    }
                }

                if (!wasTranslated) {
                    // If translation has failed, simply output the token as read.
                    // Once the dictionary and parsing rules are robust, this will
                    // handle English words that don't need translating.
                    // TODO: keep a list of unique untranslated words.
                    t.english.append(t.gostakian);
                }

                // Add back punctuation and suffixes, and recapitalize if needed.
                t.rebuildEnglish();

                if (outputType.equals("html")) {
                    wrapHtmlByCategory(t);
                }
                else {
                    output.append(t.english);
                }
                if (lineScanner.hasNext()) {
                    output.append(" ");
                }
            }
            lineScanner.close();
            output.append("\n");
        }
        if (outputType.equals("gui")) {
            outputPane.setText(output.toString());
        }
        else if (outputType.equals("html") || outputType.equals("txt")) {
            if (outputType.equals("html")) {
                addClosingHtml();
            }
            outfile.print(output.toString());
            outfile.close();
        }
        else {
            System.err.println("Error: unexpected input type."); // LOG
        }
    }


    /**
     * Adds HTML boilerplate and hardcoded CSS class info.
     */
    private void addOpeningHtml() {
        output.append("<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"utf-8\">"
                + "<title>Gostakian-to-English Translation</title>"
                + "<style type=\"text/css\">"
                + "body {white-space: pre-wrap;}"
                + ".creature {color: #228B22;}"
                + ".object {color: #0000FF;}"
                + ".vague {color: #FF8C00;}"
                + ".guess {color: #FF1493;}"
                + ".unknown {color: #FF0000;}"
                + ".none {color: #000000;}"
                + "</style></head><body><p>");
    }


    /**
     * Closes HTML tags.
     */
    private void addClosingHtml() {
        output.append("</p></body></html>");
    }


    /**
     * Wraps a translated word in HTML tags according to its category.
     * 
     * @param t the current translation token
     */
    private void wrapHtmlByCategory(Token t) {
        if (t.category == Category.NONE) {
            output.append(t.english);
            return;
        }

        if (t.category == Category.CREATURE) {
            output.append("<span class=\"creature\">");
        }
        else if (t.category == Category.OBJECT) {
            output.append("<span class=\"object\">");
        }
        else if (t.category == Category.VAGUE) {
            output.append("<span class=\"vague\">");
        }
        else if (t.category == Category.GUESS) {
            output.append("<span class=\"guess\">");
        }
        else if (t.category == Category.UNKNOWN) {
            output.append("<span class=\"unknown\">");
        }
        else {
            System.err.println("Error: unexepected word category."); // LOG
        }
        output.append(t.english);
        output.append("</span>");
    }
}