package com.ets.builder;

import com.ets.model.Difficulty;
import com.ets.model.Question;
import com.ets.model.Quiz;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
/**
 * Builder for constructing immutable {@link Quiz} instances.
 * Demonstrates the Builder design pattern for step-by-step quiz configuration.
 *
 * <p>Usage:
 * <pre>
 * Quiz quiz = new QuizBuilder()
 *     .withTitle("Java Basics")
 *     .addQuestion(q1)
 *     .build();
 * </pre>
 */

public final class QuizBuilder {

    private static final int DEFAULT_TIME_LIMIT_MINUTES = 30;
    private static final boolean DEFAULT_SHUFFLE_QUESTIONS = false;
    private static final boolean DEFAULT_SHOW_FEEDBACK_IMMEDIATELY = true;

    private String quizId;
    private String title;
    private String description;
    private final List<Question> questions = new ArrayList<>();
    private int timeLimitMinutes = DEFAULT_TIME_LIMIT_MINUTES;
    private boolean shuffleQuestions = DEFAULT_SHUFFLE_QUESTIONS;
    private boolean showFeedbackImmediately = DEFAULT_SHOW_FEEDBACK_IMMEDIATELY;

    public QuizBuilder withQuizId(String quizId) {
        this.quizId = quizId;
        return this;
    }

    public QuizBuilder withTitle(String title) {
        this.title = title;
        return this;
    }

    public QuizBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public QuizBuilder withTimeLimitMinutes(int timeLimitMinutes) {
        this.timeLimitMinutes = timeLimitMinutes;
        return this;
    }

    public QuizBuilder withShuffleQuestions(boolean shuffleQuestions) {
        this.shuffleQuestions = shuffleQuestions;
        return this;
    }

    public QuizBuilder withShowFeedbackImmediately(boolean showFeedbackImmediately) {
        this.showFeedbackImmediately = showFeedbackImmediately;
        return this;
    }

    public QuizBuilder addQuestion(Question question) {
        questions.add(Objects.requireNonNull(question));
        return this;
    }

    public QuizBuilder addQuestion(String id, String category, String text,
                                   List<String> options, int correctIndex,
                                   Difficulty difficulty) {
        questions.add(new Question(id, category, text, options, correctIndex, difficulty));
        return this;
    }

    public QuizBuilder addQuestions(List<Question> questionList) {
        questions.addAll(Objects.requireNonNull(questionList));
        return this;
    }

    public QuizBuilder clearQuestions() {
        questions.clear();
        return this;
    }

    public Quiz build() {
        if (quizId == null || title == null) {
            throw new IllegalStateException("quizId and title are required");
        }
        return new Quiz(quizId, title,
                description != null ? description : "",
                List.copyOf(questions),
                timeLimitMinutes, shuffleQuestions,
                showFeedbackImmediately);
    }
}