package com.ets.model;

import java.util.List;
import java.util.Objects;

/**
 * Immutable quiz object built via {@link com.ets.builder.QuizBuilder}.
 * Contains questions, time limit, and display preferences.
 */
public final class Quiz {

    private static final int POINTS_PER_QUESTION = 10;

    private final String quizId;
    private final String title;
    private final String description;
    private final List<Question> questions;
    private final int timeLimitMinutes;
    private final boolean shuffleQuestions;
    private final boolean showFeedbackImmediately;

    public Quiz(String quizId, String title, String description,
                List<Question> questions, int timeLimitMinutes,
                boolean shuffleQuestions, boolean showFeedbackImmediately) {
        this.quizId = Objects.requireNonNull(quizId);
        this.title = Objects.requireNonNull(title);
        this.description = Objects.requireNonNull(description);
        this.questions = List.copyOf(Objects.requireNonNull(questions));
        this.timeLimitMinutes = timeLimitMinutes;
        this.shuffleQuestions = shuffleQuestions;
        this.showFeedbackImmediately = showFeedbackImmediately;
    }

    public String getQuizId() { return quizId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public List<Question> getQuestions() { return questions; }
    public int getTimeLimitMinutes() { return timeLimitMinutes; }
    public boolean isShuffleQuestions() { return shuffleQuestions; }
    public boolean isShowFeedbackImmediately() { return showFeedbackImmediately; }

    public int getTotalPoints() {
        return questions.size() * POINTS_PER_QUESTION;
    }

    @Override
    public String toString() {
        return String.format("Quiz[%s: %s (%d questions)]", quizId, title, questions.size());
    }
}