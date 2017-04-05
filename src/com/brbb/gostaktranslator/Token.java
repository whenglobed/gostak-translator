package com.brbb.gostaktranslator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.brbb.gostaktranslator.Definition.Category;

/**
 * Groups metadata about the token and the token strings to be operated on.
 */
final class Token {
    String gostakian;
    StringBuilder beginPunct = new StringBuilder();
    StringBuilder endPunct = new StringBuilder();
    StringBuilder english = new StringBuilder();
    StringBuilder suffix = new StringBuilder();
    Category category = Category.NONE;
    boolean hasFirstCapital;
    boolean hasBeginPunct;
    boolean hasEndPunct;
    boolean hasSuffix;

    /**
     * Checks for leading/trailing punctuation, trims it from the token
     * if found, and stores it for later.
     */
    protected void trimAndRememberPunct() {
        Pattern beginPunctPattern = Pattern.compile("^[\\p{Punct}]+");
        Pattern endPunctPattern = Pattern.compile("[\\p{Punct}]+$");

        Matcher m = beginPunctPattern.matcher(gostakian);
        if (m.find() && !(m.group().equals(gostakian))) {
            hasBeginPunct = true;
            beginPunct.append(m.group());
            gostakian = m.replaceAll(""); // Remove leading punctuation.
        }

        m = endPunctPattern.matcher(gostakian);
        if (m.find() && !(m.group().equals(gostakian))) {
            hasEndPunct = true;
            endPunct.append(m.group());
            gostakian = m.replaceAll(""); // Remove trailing punctuation.
        }
    }


    /**
     * Remembers if the first letter was capitalized.
     */
    protected void rememberFirstCapital() {
        if (gostakian.length() > 0 && Character.isUpperCase(gostakian.charAt(0))) {
            hasFirstCapital = true;
        }
    }


    /**
     * Adds suffixes and punctuation back to the translated word, and
     * capitalizes the first letter if it was capitalized in the input.
     */
    protected void rebuildEnglish() {
        // Add back suffixes.
        // "d" and "ed" become "'d"
        // Plurals get a variety of plural endings per English rules.
        // "'s" (apostrophe s, for possessives) gets added back without a hyphen.
        // All other suffixes get added back with a preceding hyphen.
        if (hasSuffix) {
            if (suffix.toString().equals("d") || suffix.toString().equals("ed")) {
                suffix = new StringBuilder("\'d");
                english.append(suffix);
            }
            else if (suffix.toString().equals("s")) {
                String temp = english.toString();
                if (temp.endsWith("s") || temp.endsWith("ch")
                        || temp.endsWith("sh") || temp.endsWith("x")) {
                    english.append("es");
                }
                else if (temp.endsWith("y")) {
                    Matcher m = Pattern.compile("[^aeiou]y$").matcher(temp);
                    if (m.find()) {
                        english.deleteCharAt(english.length() - 1);
                        english.append("ies");
                    }
                    else {
                        english.append("s");
                    }
                }
                else {
                    english.append("s");
                }
            }
            else if (suffix.toString().equals("\'s")) {
                english.append(suffix);
            }
            else {
                english.append("-");
                english.append(suffix);
            }
        }

        if (hasFirstCapital) {
            english.replace(0, 1, english.substring(0, 1).toUpperCase());
        }

        if (hasBeginPunct) {
            english.insert(0, beginPunct);
        }
        if (hasEndPunct) {
            english.append(endPunct);
        }
    }
}