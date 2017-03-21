package com.brbb.gostaktranslator;

/**
 * Main class for the Gostakian translator.
 * 
 * @author Alex Johnson
 * @version 1.0, 12/27/2015
 * 
 * TODO: rewrite dictionary and parser to build a SQL dictionary with several
 * categories of words and use that for translation lookups, color-coding the
 * output by category.
 */
public class Main {

    /**
     * Main method.
     * 
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        new TranslatorGui();
    }
}
