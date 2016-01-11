package com.brbb.gostaktranslator;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test suite for the Gostak Translation program.
 * 
 * This class is one big TODO for now; nothing useful is being done here yet.
 */
public class GostakTest {

    @Test
    public void badDictTest() {
        new TranslatorGui("bad_dictionary_file.txt");
        
    }
    
}
