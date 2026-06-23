package com.ets.strategy;

import com.ets.model.Difficulty;
import com.ets.model.Question;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link WeakTopicStrategy}.
 * Verifies count limiting, empty bank handling, and result integrity.
 * Note: current implementation is a random placeholder pending full adaptive logic.
 */
class WeakTopicStrategyTest {

    private static final List<String> OPTIONS = List.of("A", "B", "C", "D");

    private Question makeQuestion(String id, Difficulty difficulty) {
        return new Question(id, "Math", "What is 2+2?", OPTIONS, 0, difficulty);
    }

    private List<Question> mixedBank() {
        return List.of(
                makeQuestion("q1", Difficulty.EASY),
                makeQuestion("q2", Difficulty.MEDIUM),
                makeQuestion("q3", Difficulty.HARD)
        );
    }

    @Test
    void selectQuestions_countLessThanBank_returnsExactCount() {
        WeakTopicStrategy strategy = new WeakTopicStrategy();
        assertEquals(2, strategy.selectQuestions(mixedBank(), 2).size());
    }

    @Test
    void selectQuestions_countExceedsBankSize_returnsEntireBank() {
        WeakTopicStrategy strategy = new WeakTopicStrategy();
        assertEquals(3, strategy.selectQuestions(mixedBank(), 100).size());
    }

    @Test
    void selectQuestions_emptyBank_returnsEmptyList() {
        WeakTopicStrategy strategy = new WeakTopicStrategy();
        assertTrue(strategy.selectQuestions(List.of(), 5).isEmpty());
    }

    @Test
    void selectQuestions_countZero_returnsEmptyList() {
        WeakTopicStrategy strategy = new WeakTopicStrategy();
        assertTrue(strategy.selectQuestions(mixedBank(), 0).isEmpty());
    }

    @Test
    void selectQuestions_returnedQuestions_areSubsetOfBank() {
        WeakTopicStrategy strategy = new WeakTopicStrategy();
        List<Question> bank = mixedBank();
        List<Question> result = strategy.selectQuestions(bank, 2);
        assertTrue(bank.containsAll(result));
    }
}