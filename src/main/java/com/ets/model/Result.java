package com.ets.model;

import java.util.List;
import java.util.Objects;

/**
 * Immutable result of a completed quiz attempt.
 */
public final class Result {

    private final String quizId;
    private final List<Answer> answers;
    private final int score;
    private final int totalPoints;

    public Result(String quizId, List<Answer> answers,
                      int score, int totalPoints) {
        this.quizId = Objects.requireNonNull(quizId);
        this.answers = List.copyOf(Objects.requireNonNull(answers));
        this.score = score;
        this.totalPoints = totalPoints;
    }

    public String getQuizId() { return quizId; }
    public List<Answer> getAnswers() { return answers; }
    public int getScore() { return score; }
    public int getTotalPoints() { return totalPoints; }

    public double getPercentage() {
        return totalPoints == 0 ? 0.0 : (100.0 * score) / totalPoints;
    }

    @Override
    public String toString() {
        return String.format("Result[%s: %d/%d (%.0f%%)]",
                quizId, score, totalPoints, getPercentage());
    }
}