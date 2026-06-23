package com.ets.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Result}.
 * Verifies percentage calculation including boundary and edge cases.
 */
class ResultTest {

    // ── getPercentage ─────────────────────────────

    @Test
    void getPercentage_fullScore_returns100() {
        Result result = new Result("q1", List.of(), 30, 30);
        assertEquals(100.0, result.getPercentage(), 0.01);
    }

    @Test
    void getPercentage_zeroScore_returnsZero() {
        Result result = new Result("q1", List.of(), 0, 30);
        assertEquals(0.0, result.getPercentage(), 0.01);
    }

    @ParameterizedTest(name = "score={0}, total={1} → {2}%")
    @CsvSource({"10,30,33.33", "20,30,66.67", "15,30,50.0", "1,10,10.0"})
    void getPercentage_partialScores_returnsCorrectPercentage(int score, int total, double expected) {
        Result result = new Result("q1", List.of(), score, total);
        assertEquals(expected, result.getPercentage(), 0.01);
    }

    /**
     * DELIBERATE FAILING INTENT — reveals a system limitation.
     *
     * A Result with totalPoints=0 returns 0.0% due to the guard in getPercentage().
     * However, QuizService has no validation preventing evaluation of an empty quiz,
     * meaning a zero-question quiz silently produces a 0/0 result with no error.
     * This test passes technically, but exposes a missing business rule:
     * evaluating an empty quiz should throw an exception rather than return silently.
     */
    @Test
    void getPercentage_zeroTotalPoints_returnsZeroWithNoException() {
        Result result = new Result("q1", List.of(), 0, 0);
        assertEquals(0.0, result.getPercentage(), 0.01);
        // Limitation: QuizService allows this state to occur silently.
        // A guard in evaluateQuiz() checking quiz.getTotalPoints() > 0 would be safer.
    }

    // ── Null guards ───────────────────────────────

    @Test
    void constructor_nullQuizId_throwsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> new Result(null, List.of(), 10, 30));
    }

    @Test
    void constructor_nullAnswers_throwsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> new Result("q1", null, 10, 30));
    }

    // ── Getters ───────────────────────────────────

    @Test
    void getScore_andGetTotalPoints_returnCorrectValues() {
        Result result = new Result("q1", List.of(), 20, 40);
        assertAll(
                () -> assertEquals(20, result.getScore()),
                () -> assertEquals(40, result.getTotalPoints()),
                () -> assertEquals("q1", result.getQuizId())
        );
    }
}