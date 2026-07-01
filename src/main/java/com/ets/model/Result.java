package com.ets.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Immutable result of a completed quiz attempt.
 * Stores which student took it, score, total points, and when it was
 * completed, for display and history purposes.
 */
public final class Result {

    private final String username;
    private final String quizId;
    private final List<Answer> answers;
    private final int score;
    private final int totalPoints;
    private final String completedAt;

    /**
     * Convenience constructor that stamps {@code completedAt} as the current time.
     */
    public Result(String username, String quizId, List<Answer> answers,
                  int score, int totalPoints) {
        this(username, quizId, answers, score, totalPoints, LocalDateTime.now().toString());
    }

    /**
     * Full constructor, used when reconstructing a Result from storage
     * with a known completion timestamp.
     *
     * @param completedAt ISO-8601 formatted timestamp string (e.g. from
     *                     {@link LocalDateTime#toString()})
     */
    public Result(String username, String quizId, List<Answer> answers,
                  int score, int totalPoints, String completedAt) {
        this.username = Objects.requireNonNull(username);
        this.quizId = Objects.requireNonNull(quizId);
        this.answers = Collections.unmodifiableList(new ArrayList<>(Objects.requireNonNull(answers)));
        this.score = score;
        this.totalPoints = totalPoints;
        this.completedAt = completedAt != null ? completedAt : LocalDateTime.now().toString();
    }

    public String getUsername() { return username; }
    public String getQuizId() { return quizId; }
    public List<Answer> getAnswers() { return answers; }
    public int getScore() { return score; }
    public int getTotalPoints() { return totalPoints; }
    public String getCompletedAt() { return completedAt; }

    public double getPercentage() {
        return totalPoints == 0 ? 0.0 : (100.0 * score) / totalPoints;
    }

    @Override
    public String toString() {
        return String.format("Result[%s/%s: %d/%d (%.0f%%) @ %s]",
                username, quizId, score, totalPoints, getPercentage(), completedAt);
    }
}