package com.ets.strategy;

import com.ets.model.Difficulty;
import com.ets.model.Question;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link RandomStrategy}.
 * Verifies count limiting, empty bank handling, and that all returned questions
 * come from the original bank.
 */
class RandomStrategyTest {

    private static final List<String> OPTIONS = List.of("A", "B", "C", "D");

    private Question makeQuestion(String id) {
        return new Question(id, "Math", "What is 2+2?", OPTIONS, 0, Difficulty.EASY);
    }

    private List<Question> bankOfFive() {
        return List.of(
                makeQuestion("q1"),
                makeQuestion("q2"),
                makeQuestion("q3"),
                makeQuestion("q4"),
                makeQuestion("q5")
        );
    }

    @Test
    void selectQuestions_countLessThanBank_returnsExactCount() {
        RandomStrategy strategy = new RandomStrategy();
        assertEquals(3, strategy.selectQuestions(bankOfFive(), 3).size());
    }

    @Test
    void selectQuestions_countExceedsBankSize_returnsEntireBank() {
        RandomStrategy strategy = new RandomStrategy();
        assertEquals(5, strategy.selectQuestions(bankOfFive(), 100).size());
    }

    @Test
    void selectQuestions_emptyBank_returnsEmptyList() {
        RandomStrategy strategy = new RandomStrategy();
        assertTrue(strategy.selectQuestions(List.of(), 5).isEmpty());
    }

    @Test
    void selectQuestions_countZero_returnsEmptyList() {
        RandomStrategy strategy = new RandomStrategy();
        assertTrue(strategy.selectQuestions(bankOfFive(), 0).isEmpty());
    }

    @Test
    void selectQuestions_returnedQuestions_areSubsetOfBank() {
        RandomStrategy strategy = new RandomStrategy();
        List<Question> bank = bankOfFive();
        List<Question> result = strategy.selectQuestions(bank, 3);
        assertTrue(bank.containsAll(result));
    }

    @ParameterizedTest(name = "count={0} from bank of 5 → result size never exceeds 5")
    @ValueSource(ints = {0, 1, 3, 5, 10, 100})
    void selectQuestions_variousCounts_neverExceedsBankSize(int count) {
        RandomStrategy strategy = new RandomStrategy();
        assertTrue(strategy.selectQuestions(bankOfFive(), count).size() <= 5);
    }
}