package com.brbb.gostaktranslator;

/**
 * Bare-bones helper class to group a word's translation and category in the
 * dictionary.
 */
final class Definition {
    protected enum Category {
        CREATURE, OBJECT, VAGUE, GUESS, UNKNOWN, NONE;
    }

    protected String translation;
    protected Category category;

    /**
     * Constructor.
     */
    protected Definition(String translation, String category) {
        this.translation = translation;
        this.category = Category.valueOf(category.toUpperCase());
    }
}