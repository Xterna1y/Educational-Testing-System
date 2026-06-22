package com.ets.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Question}.
 * Verifies correctness checking, immutability, equality, and null guards.
 */
class QuestionTest {

    private static final List<String> OPTIONS = List.of("A", "B", "C", "D");

    private Question makeQuestion(String id, int correctIndex, Difficulty difficulty) {
        return new Question(id, "Math", "What is 2+2?", OPTIONS, correctIndex, difficulty);
    }

    // ── isCorrect ─────────────────────────────────

    @Test
    void isCorrect_correctIndex_returnsTrue() {
        Question q = makeQuestion("q1", 0, Difficulty.EASY);
        assertTrue(q.isCorrect(0));
    }

    @Test
    void isCorrect_wrongIndex_returnsFalse() {
        Question q = makeQuestion("q1", 0, Difficulty.EASY);
        assertFalse(q.isCorrect(2));
    }

    @ParameterizedTest(name = "Selected={0}, correctIndex=0 → isCorrect={1}")
    @CsvSource({"0,true", "1,false", "2,false", "3,false"})
    void isCorrect_allOptionIndices_matchExpected(int selected, boolean expected) {
        Question q = makeQuestion("q1", 0, Difficulty.EASY);
        assertEquals(expected, q.isCorrect(selected));
    }

    // ── Null guards ───────────────────────────────

    @Test
    void constructor_nullId_throwsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> new Question(null, "Math", "Text?", OPTIONS, 0, Difficulty.EASY));
    }

    @Test
    void constructor_nullCategory_throwsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> new Question("q1", null, "Text?", OPTIONS, 0, Difficulty.EASY));
    }

    @Test
    void constructor_nullOptions_throwsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> new Question("q1", "Math", "Text?", null, 0, Difficulty.EASY));
    }

    // ── Equality ──────────────────────────────────

    @Test
    void equals_sameId_returnsTrue() {
        Question q1 = new Question("same", "Math", "Q1?", OPTIONS, 0, Difficulty.EASY);
        Question q2 = new Question("same", "Science", "Q2?", OPTIONS, 1, Difficulty.HARD);
        assertEquals(q1, q2);
    }

    @Test
    void equals_differentId_returnsFalse() {
        Question q1 = makeQuestion("q1", 0, Difficulty.EASY);
        Question q2 = makeQuestion("q2", 0, Difficulty.EASY);
        assertNotEquals(q1, q2);
    }

    // ── Immutability ──────────────────────────────

    @Test
    void getOptions_returnedList_isImmutable() {
        Question q = makeQuestion("q1", 0, Difficulty.EASY);
        assertThrows(UnsupportedOperationException.class,
                () -> q.getOptions().add("E"));
    }
}