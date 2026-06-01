package com.ets.model;

import java.util.Objects;

/**
 * Represents a user's answer to a specific question.
 */
public final class Answer {

    private final String questionId;
    private final int selectedOptionIndex;

    public Answer(String questionId, int selectedOptionIndex) {
        this.questionId = Objects.requireNonNull(questionId);
        this.selectedOptionIndex = selectedOptionIndex;
    }

    public String getQuestionId() { return questionId; }
    public int getSelectedOptionIndex() { return selectedOptionIndex; }

    public boolean isCorrectFor(Question question) {
        return question.getId().equals(questionId)
                && question.isCorrect(selectedOptionIndex);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Answer)) return false;
        Answer other = (Answer) o;
        return questionId.equals(other.questionId)
                && selectedOptionIndex == other.selectedOptionIndex;
    }

    @Override
    public int hashCode() { return Objects.hash(questionId, selectedOptionIndex); }
}