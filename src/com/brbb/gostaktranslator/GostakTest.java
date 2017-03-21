package com.brbb.gostaktranslator;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the Gostak Translation program.
 * 
 * This class is one big TODO for now; nothing useful is being done here yet.
 */
public class GostakTest {
    String dictFileName;
    String badDictFileName;
    String inputFileName;
    String outputFileName;
    
    //test parser with bad input filename
    
    @Before
    public void setUp() {
        dictFileName = "gostak_dictionary.txt";
        badDictFileName = "bad_dict_filename";
    }

    @Test
    public void badDictFileTest() {
        File dictFile = new File(badDictFileName);
        assertFalse(dictFile.exists());
        try {
            Dictionary dict = new Dictionary("bad_filename.txt");
            fail("Dictionary constructor should have thrown an IOException");
        }
        catch (IOException exc) {
            // Expected.
        }
    }


    @Test
    public void defaultDictTest() {
        try {
            Dictionary defaultDict = new Dictionary(dictFileName);
        }
        catch (IOException exc) {
            fail("Dictionary constructor threw an IOException");
        }
    }
    
    @After
    public void tearDown() {
        // Cleanup resources.
    }
}
