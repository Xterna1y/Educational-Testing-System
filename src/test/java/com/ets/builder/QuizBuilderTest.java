package com.ets.builder;

import com.ets.model.Difficulty;
import com.ets.model.Question;
import com.ets.model.Quiz;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link QuizBuilder}.
 * Verifies correct construction, default values, validation, and immutability.
 */
class QuizBuilderTest {

    private static final List<String> OPTIONS = List.of("A", "B", "C", "D");

    private Question makeQuestion(String id, Difficulty difficulty) {
        return new Question(id, "Math", "What is 2+2?", OPTIONS, 0, difficulty);
    }

    // ── Build success ─────────────────────────────

    @Test
    void build_withRequiredFieldsOnly_returnsQuizWithDefaults() {
        Quiz quiz = new QuizBuilder()
                .withQuizId("q1")
                .withTitle("Java Basics")
                .build();

        assertAll(
                () -> assertEquals("q1", quiz.getQuizId()),
                () -> assertEquals("Java Basics", quiz.getTitle()),
                () -> assertEquals("", quiz.getDescription()),
                () -> assertTrue(quiz.getQuestions().isEmpty()),
                () -> assertEquals(30, quiz.getTimeLimitMinutes()),
                () -> assertFalse(quiz.isShuffleQuestions()),
                () -> assertTrue(quiz.isShowFeedbackImmediately())
        );
    }

    @Test
    void build_withAllFields_setsCorrectValues() {
        Question q = makeQuestion("q1", Difficulty.EASY);

        Quiz quiz = new QuizBuilder()
                .withQuizId("q99")
                .withTitle("Full Quiz")
                .withDescription("A complete quiz")
                .withTimeLimitMinutes(45)
                .withShuffleQuestions(true)
                .withShowFeedbackImmediately(false)
                .addQuestion(q)
                .build();

        assertAll(
                () -> assertEquals("A complete quiz", quiz.getDescription()),
                () -> assertEquals(45, quiz.getTimeLimitMinutes()),
                () -> assertTrue(quiz.isShuffleQuestions()),
                () -> assertFalse(quiz.isShowFeedbackImmediately()),
                () -> assertEquals(1, quiz.getQuestions().size())
        );
    }

    @ParameterizedTest(name = "Adding {0} questions → quiz has {0} questions")
    @CsvSource({"1", "3", "5"})
    void build_addMultipleQuestions_allPresent(int count) {
        QuizBuilder builder = new QuizBuilder().withQuizId("q1").withTitle("T");
        for (int i = 0; i < count; i++) {
            builder.addQuestion(makeQuestion("q" + i, Difficulty.EASY));
        }
        assertEquals(count, builder.build().getQuestions().size());
    }

    // ── Validation ────────────────────────────────

    @Test
    void build_missingQuizId_throwsIllegalStateException() {
        QuizBuilder builder = new QuizBuilder().withTitle("No ID Quiz");
        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void build_missingTitle_throwsIllegalStateException() {
        QuizBuilder builder = new QuizBuilder().withQuizId("q1");
        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    void addQuestion_nullQuestion_throwsNullPointerException() {
        QuizBuilder builder = new QuizBuilder().withQuizId("q1").withTitle("T");
        assertThrows(NullPointerException.class, () -> builder.addQuestion(null));
    }

    // ── clearQuestions ────────────────────────────

    @Test
    void clearQuestions_afterAddingQuestions_buildsEmptyQuiz() {
        Quiz quiz = new QuizBuilder()
                .withQuizId("q1")
                .withTitle("T")
                .addQuestion(makeQuestion("q1", Difficulty.EASY))
                .clearQuestions()
                .build();

        assertTrue(quiz.getQuestions().isEmpty());
    }

    // ── Immutability ──────────────────────────────

    @Test
    void build_questionsListIsImmutable_throwsUnsupportedOperationException() {
        Quiz quiz = new QuizBuilder()
                .withQuizId("q1")
                .withTitle("T")
                .addQuestion(makeQuestion("q1", Difficulty.EASY))
                .build();

        assertThrows(UnsupportedOperationException.class,
                () -> quiz.getQuestions().add(makeQuestion("q2", Difficulty.HARD)));
    }
}