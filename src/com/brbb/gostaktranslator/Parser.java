package com.brbb.gostaktranslator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextPane;

public class Parser {
    private Scanner sc;
    private PrintWriter outfile;
    private JTextPane outputPane;
    private Dictionary dictionary;
    private String inputType;
    private ArrayList<String> suffixes;
    
    /**
     * Constructor. Prepares to parse a string and output the translation to
     * a JTextPane.
     * 
     * @param input the String to be parsed and translated
     * @param outputPane the JTextPane for the translated output
     * @param dictFileName the filename for the dictionary
     */
    public Parser(String input, JTextPane outputPane, String dictFileName) {
        inputType = "string";
                
        sc = new Scanner(input);
        this.outputPane = outputPane;
        dictionary = new Dictionary(dictFileName);
        initializeSuffixes();
    }
    
    
    /**
     * Constructor. Prepares to parse input from the specified file and write
     * the translation to an output file.
     * 
     * @param inputFileName the filename for the input text
     * @param outputFileName the filename for the output file
     * @param dictFileName the filename for the dictionary
     */
    public Parser(String inputFileName, String outputFileName, String dictFileName) {
        try {
            inputType = "file";
            
            sc = new Scanner(new FileInputStream(inputFileName), "UTF-8");
            outfile = new PrintWriter(outputFileName, "UTF-8");
            dictionary = new Dictionary(dictFileName);
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
        // TODO: Color-code output for different word categories.
        StringBuilder output = new StringBuilder(256);
        
        while (sc.hasNextLine()) {
            Scanner lineScanner = new Scanner(sc.nextLine());
            while (lineScanner.hasNext()) {
                Token t = new Token();
                boolean wasTranslated = false;
    
                // Get next token.
                t.gostakian = lineScanner.next();
                
                t.trimAndRememberPunct();
                t.rememberFirstCapital();
          
                t.gostakian = t.gostakian.toLowerCase();
    
                // Attempt to find the token into the dictionary.
                if (dictionary.translate(t.gostakian) != null) {
                    t.english.append(dictionary.translate(t.gostakian));
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
                                if (dictionary.translate(trimmed.toString()) != null) {
                                    t.english.append(dictionary.translate(trimmed.toString()));
                                    t.hasSuffix = true;
                                    t.suffix = new StringBuilder(suffix);
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
                
                output.append(t.english);
                if (lineScanner.hasNext()) {
                    output.append(" ");
                }
            }
            lineScanner.close();
            output.append("\n");
        }
        if (inputType.equals("string")) {
            outputPane.setText(output.toString());
        }
        else if (inputType.equals("file")) {
            outfile.print(output.toString());
            outfile.close();
        }
        else {
            System.err.println("Error: unexpected input type."); // LOG
        }
    }

}