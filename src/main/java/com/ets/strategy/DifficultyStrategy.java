package com.ets.strategy;

import com.ets.model.Difficulty;
import com.ets.model.Question;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Selects questions filtered by difficulty level.
 */
public final class DifficultyStrategy implements QuizStrategy {

    private final Difficulty targetDifficulty;

    public DifficultyStrategy(Difficulty difficulty) {
        this.targetDifficulty = difficulty;
    }

    @Override
    public List<Question> selectQuestions(List<Question> questions, int count) {
        List<Question> filtered = questions.stream()
                .filter(q -> q.getDifficulty() == targetDifficulty)
                .collect(Collectors.toList());
        return filtered.subList(0, Math.min(count, filtered.size()));
    }
}