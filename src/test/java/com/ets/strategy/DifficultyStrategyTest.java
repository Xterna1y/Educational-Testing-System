package com.ets.strategy;

import com.ets.model.Difficulty;
import com.ets.model.Question;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link DifficultyStrategy}.
 * Verifies filtering by difficulty level, count limiting, and empty bank handling.
 */
class DifficultyStrategyTest {

    private static final List<String> OPTIONS = List.of("A", "B", "C", "D");

    private Question makeQuestion(String id, Difficulty difficulty) {
        return new Question(id, "Math", "What is 2+2?", OPTIONS, 0, difficulty);
    }

    private List<Question> mixedBank() {
        return List.of(
                makeQuestion("q1", Difficulty.EASY),
                makeQuestion("q2", Difficulty.EASY),
                makeQuestion("q3", Difficulty.MEDIUM),
                makeQuestion("q4", Difficulty.HARD),
                makeQuestion("q5", Difficulty.HARD)
        );
    }

    @Test
    void selectQuestions_filterEasy_returnsOnlyEasyQuestions() {
        DifficultyStrategy strategy = new DifficultyStrategy(Difficulty.EASY);
        List<Question> result = strategy.selectQuestions(mixedBank(), 10);

        assertTrue(result.stream().allMatch(q -> q.getDifficulty() == Difficulty.EASY));
        assertEquals(2, result.size());
    }

    @Test
    void selectQuestions_filterHard_returnsOnlyHardQuestions() {
        DifficultyStrategy strategy = new DifficultyStrategy(Difficulty.HARD);
        List<Question> result = strategy.selectQuestions(mixedBank(), 10);

        assertTrue(result.stream().allMatch(q -> q.getDifficulty() == Difficulty.HARD));
        assertEquals(2, result.size());
    }

    @Test
    void selectQuestions_countLowerThanMatches_returnsAtMostCount() {
        List<Question> bank = List.of(
                makeQuestion("q1", Difficulty.HARD),
                makeQuestion("q2", Difficulty.HARD),
                makeQuestion("q3", Difficulty.HARD)
        );
        DifficultyStrategy strategy = new DifficultyStrategy(Difficulty.HARD);
        assertEquals(2, strategy.selectQuestions(bank, 2).size());
    }

    @Test
    void selectQuestions_noMatchingDifficulty_returnsEmptyList() {
        List<Question> bank = List.of(makeQuestion("q1", Difficulty.EASY));
        DifficultyStrategy strategy = new DifficultyStrategy(Difficulty.HARD);
        assertTrue(strategy.selectQuestions(bank, 5).isEmpty());
    }

    @Test
    void selectQuestions_emptyBank_returnsEmptyList() {
        DifficultyStrategy strategy = new DifficultyStrategy(Difficulty.MEDIUM);
        assertTrue(strategy.selectQuestions(List.of(), 5).isEmpty());
    }

    @Test
    void selectQuestions_countZero_returnsEmptyList() {
        DifficultyStrategy strategy = new DifficultyStrategy(Difficulty.EASY);
        assertTrue(strategy.selectQuestions(mixedBank(), 0).isEmpty());
    }
}