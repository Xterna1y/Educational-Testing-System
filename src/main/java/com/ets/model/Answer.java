package com.ets.model;

import java.util.Objects;

/**
 * Represents a user's answer to a specific question.
 * Links a selected option index to a question ID for evaluation.
 *
 * <p>{@code category} and {@code correct} are optional, additive metadata
 * captured at answer-creation time (while the originating {@link Question}
 * is still available) so that persisted {@link Result} history can later be
 * mined per-category — e.g. by {@link com.ets.strategy.WeakTopicStrategy} —
 * without needing to re-look-up questions that may no longer exist (each
 * live API fetch generates fresh question IDs). Both default to
 * {@code null}/{@code false} via the original two-argument constructor for
 * callers that don't need this metadata, and for results persisted before
 * this metadata existed.</p>
 */
public final class Answer {

    private final String questionId;
    private final int selectedOptionIndex;
    private final String category;
    private final boolean correct;

    public Answer(String questionId, int selectedOptionIndex) {
        this(questionId, selectedOptionIndex, null, false);
    }

    /**
     * @param questionId          the answered question's id
     * @param selectedOptionIndex the option index the user chose
     * @param category            the question's category at the time it was answered,
     *                            or {@code null} if unknown
     * @param correct             whether {@code selectedOptionIndex} was the correct answer
     */
    public Answer(String questionId, int selectedOptionIndex, String category, boolean correct) {
        this.questionId = Objects.requireNonNull(questionId);
        this.selectedOptionIndex = selectedOptionIndex;
        this.category = category;
        this.correct = correct;
    }

    public String getQuestionId() { return questionId; }
    public int getSelectedOptionIndex() { return selectedOptionIndex; }

    /** @return the question's category, or {@code null} if not recorded for this answer */
    public String getCategory() { return category; }

    /** @return whether this answer was correct, as recorded at answer-creation time */
    public boolean isCorrect() { return correct; }

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