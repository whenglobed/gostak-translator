package com.brbb.gostaktranslator;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class Dictionary {
    private HashMap<String, Definition> map;

    private static final int DEFAULT_CAPACITY = 50;
    private static final float DEFAULT_LOAD = .75f;

    /**
     * Constructor. Builds the dictionary map from the provided input file.
     * 
     * @param dictFileName the resource filename for the dictionary word mappings
     * @throws FileNotFoundException if dictFileName cannot be located
     * @throws IOException if an error occurs when reading the dictionary file
     */
    public Dictionary(String dictFileName) throws FileNotFoundException, IOException {
        map = new HashMap<String, Definition>(DEFAULT_CAPACITY, DEFAULT_LOAD);
        InputStream dictStream = Dictionary.class.getResourceAsStream("/" + dictFileName);
        if (dictStream == null) {
            throw new FileNotFoundException();
        }
        LineNumberReader infile = new LineNumberReader(new InputStreamReader
                (dictStream, "UTF-8"));

        String line;
        String tokens[] = new String[3];
        while ((line = infile.readLine()) != null) {
            line.trim();
            if (!line.startsWith("#") && !line.isEmpty()) {
                tokens = line.split(",");
                if (tokens.length == 3) {
                    map.put(tokens[0].trim(), new Definition(tokens[1].trim(), tokens[2].trim()));
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
    protected Definition translate(String word) {
        return map.get(word);
    }
}
