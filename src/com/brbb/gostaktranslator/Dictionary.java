package com.brbb.gostaktranslator;

import java.io.LineNumberReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class Dictionary {
    private HashMap<String, String> map;
    
    private static final int DEFAULT_CAPACITY = 50;
    private static final float DEFAULT_LOAD = .75f;
    
    /**
     * Constructor. Builds the dictionary map from the provided input file.
     * 
     * @param dictFileName the input filename for the dictionary word mappings
     * @throws IOException
     */
    public Dictionary(String dictFileName) throws IOException {
        map = new HashMap<String, String>(DEFAULT_CAPACITY, DEFAULT_LOAD);
        LineNumberReader infile = new LineNumberReader(new InputStreamReader
                (new FileInputStream(dictFileName), "UTF-8"));

        String line;
        String tokens[] = new String[3];
        while ((line = infile.readLine()) != null) {
            line.trim();
            if (!line.startsWith("#") && !line.isEmpty()) {
                tokens = line.split(",");
                if (tokens.length == 3) {
                    map.put(tokens[0].trim(), tokens[1].trim());
                }
                else {
                    int lineNum = infile.getLineNumber();
                    System.err.printf("Unexpected number of tokens on dictionary line %d\n", lineNum); // LOG
                }
            }
        }
        infile.close();
    }
    
    // Essentially a renamed wrapper for HashMap.get()
    protected String translate(String word) {
        return map.get(word);
    }
}
