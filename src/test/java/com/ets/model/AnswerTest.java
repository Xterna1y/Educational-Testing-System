package com.ets.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link Answer}.
 * Verifies correct evaluation, mismatched IDs, and null guards.
 */
class AnswerTest {

    private static final List<String> OPTIONS = List.of("A", "B", "C", "D");

    private Question makeQuestion(String id, int correctIndex) {
        return new Question(id, "Math", "What is 2+2?", OPTIONS, correctIndex, Difficulty.EASY);
    }

    // ── isCorrectFor ──────────────────────────────

    @Test
    void isCorrectFor_correctIndexAndMatchingId_returnsTrue() {
        Question q = makeQuestion("q1", 0);
        Answer answer = new Answer("q1", 0);
        assertTrue(answer.isCorrectFor(q));
    }

    @Test
    void isCorrectFor_wrongOptionIndex_returnsFalse() {
        Question q = makeQuestion("q1", 0);
        Answer answer = new Answer("q1", 3);
        assertFalse(answer.isCorrectFor(q));
    }

    @Test
    void isCorrectFor_mismatchedQuestionId_returnsFalse() {
        Question q = makeQuestion("q1", 0);
        Answer answer = new Answer("q999", 0); // ID does not match
        assertFalse(answer.isCorrectFor(q));
    }

    @Test
    void isCorrectFor_negativeIndex_returnsFalse() {
        Question q = makeQuestion("q1", 0);
        Answer answer = new Answer("q1", -1); // no selection
        assertFalse(answer.isCorrectFor(q));
    }

    // ── Null guards ───────────────────────────────

    @Test
    void constructor_nullQuestionId_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Answer(null, 0));
    }

    // ── Equality ──────────────────────────────────

    @Test
    void equals_sameQuestionIdAndIndex_returnsTrue() {
        Answer a1 = new Answer("q1", 2);
        Answer a2 = new Answer("q1", 2);
        assertEquals(a1, a2);
    }

    @Test
    void equals_differentIndex_returnsFalse() {
        Answer a1 = new Answer("q1", 0);
        Answer a2 = new Answer("q1", 1);
        assertNotEquals(a1, a2);
    }
}