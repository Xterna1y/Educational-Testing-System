package com.ets.model;

import java.util.List;
import java.util.Objects;

/**
 * Represents a single quiz question with multiple choice answers.
 * Immutable value object.
 */
public final class Question {

    private final String id;
    private final String category;
    private final String text;
    private final List<String> options;
    private final int correctOptionIndex;
    private final Difficulty difficulty;

    public Question(String id, String category, String text,
                    List<String> options, int correctOptionIndex,
                    Difficulty difficulty) {
        this.id = Objects.requireNonNull(id);
        this.category = Objects.requireNonNull(category);
        this.text = Objects.requireNonNull(text);
        this.options = List.copyOf(Objects.requireNonNull(options));
        this.correctOptionIndex = correctOptionIndex;
        this.difficulty = Objects.requireNonNull(difficulty);
    }

    public String getId() { return id; }
    public String getCategory() { return category; }
    public String getText() { return text; }
    public List<String> getOptions() { return options; }
    public int getCorrectOptionIndex() { return correctOptionIndex; }
    public Difficulty getDifficulty() { return difficulty; }

    public boolean isCorrect(int selectedIndex) {
        return selectedIndex == correctOptionIndex;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Question)) return false;
        Question other = (Question) o;
        return id.equals(other.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return String.format("Question[%s: %s (%s)]", id, category, difficulty);
    }
}