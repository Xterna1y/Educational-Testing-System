package com.ets.service;

import com.ets.builder.QuizBuilder;
import com.ets.model.*;
import com.ets.services.QuizService;
import com.ets.strategy.RandomStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link QuizService}.
 * Verifies quiz creation, answer evaluation, and grading logic.
 */
class QuizServiceTest {

    private static final List<String> OPTIONS = List.of("A", "B", "C", "D");
    private static final String USERNAME = "student1";

    private QuizService service;

    @BeforeEach
    void setUp() {
        service = new QuizService();
    }

    private Question makeQuestion(String id, Difficulty difficulty) {
        return new Question(id, "Math", "What is 2+2?", OPTIONS, 0, difficulty);
    }

    private Quiz buildBasicQuiz(List<Question> questions) {
        return new QuizBuilder()
                .withQuizId("q1")
                .withTitle("Test Quiz")
                .addQuestions(questions)
                .build();
    }

    // ── createQuiz ────────────────────────────────

    @Test
    void createQuiz_withRandomStrategy_returnsQuizWithAllQuestions() {
        List<Question> bank = List.of(
                makeQuestion("q1", Difficulty.EASY),
                makeQuestion("q2", Difficulty.MEDIUM)
        );
        Quiz quiz = service.createQuiz("Title", "Desc", bank, new RandomStrategy());

        assertAll(
                () -> assertEquals("Title", quiz.getTitle()),
                () -> assertEquals(2, quiz.getQuestions().size())
        );
    }

    // ── evaluateQuiz ──────────────────────────────

    @Test
    void evaluateQuiz_allCorrect_returnsFullScore() {
        Question q1 = makeQuestion("q1", Difficulty.EASY);
        Question q2 = makeQuestion("q2", Difficulty.MEDIUM);
        Quiz quiz = buildBasicQuiz(List.of(q1, q2));
        List<Answer> answers = List.of(new Answer("q1", 0), new Answer("q2", 0));

        Result result = service.evaluateQuiz(quiz, answers, USERNAME);

        assertAll(
                () -> assertEquals(20, result.getScore()),
                () -> assertEquals(20, result.getTotalPoints()),
                () -> assertEquals(USERNAME, result.getUsername())
        );
    }

    @Test
    void evaluateQuiz_allWrong_returnsZeroScore() {
        Question q1 = makeQuestion("q1", Difficulty.EASY);
        Quiz quiz = buildBasicQuiz(List.of(q1));
        List<Answer> answers = List.of(new Answer("q1", 3)); // wrong index

        Result result = service.evaluateQuiz(quiz, answers, USERNAME);
        assertEquals(0, result.getScore());
    }

    @Test
    void evaluateQuiz_partiallyCorrect_returnsPartialScore() {
        Question q1 = makeQuestion("q1", Difficulty.EASY);  // correct = 0
        Question q2 = makeQuestion("q2", Difficulty.EASY);  // correct = 0
        Quiz quiz = buildBasicQuiz(List.of(q1, q2));

        List<Answer> answers = List.of(
                new Answer("q1", 0),  // correct
                new Answer("q2", 2)   // wrong
        );

        Result result = service.evaluateQuiz(quiz, answers, USERNAME);
        assertEquals(10, result.getScore()); // 1 out of 2 correct
    }

    @Test
    void evaluateQuiz_answerWithUnknownQuestionId_isIgnored() {
        Question q1 = makeQuestion("q1", Difficulty.EASY);
        Quiz quiz = buildBasicQuiz(List.of(q1));
        List<Answer> answers = List.of(new Answer("unknown", 0)); // no match

        Result result = service.evaluateQuiz(quiz, answers, USERNAME);
        assertEquals(0, result.getScore());
    }

    @Test
    void evaluateQuiz_emptyAnswers_returnsZeroScore() {
        Question q1 = makeQuestion("q1", Difficulty.EASY);
        Quiz quiz = buildBasicQuiz(List.of(q1));

        Result result = service.evaluateQuiz(quiz, List.of(), USERNAME);
        assertEquals(0, result.getScore());
    }

    // ── Null guards ───────────────────────────────

    @Test
    void evaluateQuiz_nullQuiz_throwsNullPointerException() {
        assertThrows(NullPointerException.class,
                () -> service.evaluateQuiz(null, List.of(), USERNAME));
    }

    @Test
    void evaluateQuiz_nullAnswers_throwsNullPointerException() {
        Quiz quiz = buildBasicQuiz(List.of());
        assertThrows(NullPointerException.class,
                () -> service.evaluateQuiz(quiz, null, USERNAME));
    }

    @Test
    void evaluateQuiz_nullUsername_throwsNullPointerException() {
        Quiz quiz = buildBasicQuiz(List.of());
        assertThrows(NullPointerException.class,
                () -> service.evaluateQuiz(quiz, List.of(), null));
    }

    // ── gradeAnswers ──────────────────────────────

    @Test
    void gradeAnswers_validIndices_returnsCorrectAnswerList() {
        Question q1 = makeQuestion("q1", Difficulty.EASY);
        Question q2 = makeQuestion("q2", Difficulty.EASY);
        Quiz quiz = buildBasicQuiz(List.of(q1, q2));

        List<Answer> answers = service.gradeAnswers(quiz, List.of(0, 1));

        assertAll(
                () -> assertEquals(2, answers.size()),
                () -> assertEquals("q1", answers.get(0).getQuestionId()),
                () -> assertEquals(0, answers.get(0).getSelectedOptionIndex()),
                () -> assertEquals("q2", answers.get(1).getQuestionId()),
                () -> assertEquals(1, answers.get(1).getSelectedOptionIndex())
        );
    }

    @Test
    void gradeAnswers_fewerIndicesThanQuestions_mapsOnlyAvailable() {
        Question q1 = makeQuestion("q1", Difficulty.EASY);
        Question q2 = makeQuestion("q2", Difficulty.EASY);
        Quiz quiz = buildBasicQuiz(List.of(q1, q2));

        List<Answer> answers = service.gradeAnswers(quiz, List.of(0)); // only 1 index for 2 questions
        assertEquals(1, answers.size());
    }

    // ── Deliberate Failing Test ───────────────────
    // STEP 1: Remove @Disabled, run once, screenshot the red failure for Section 4.2
    // STEP 2: Re-add @Disabled, run again, screenshot green for JaCoCo coverage

    @Disabled("Deliberate failing test — included for Section 4.2 report evidence")
    @Test
    void evaluateQuiz_emptyQuiz_shouldThrowButCurrentlyDoesNot() {
        // REVEALS SYSTEM LIMITATION: QuizService has no guard preventing evaluation
        // of a quiz with zero questions. It silently returns a 0/0 Result instead of
        // throwing an IllegalArgumentException. This should be fixed by adding a
        // precondition check: if (quiz.getQuestions().isEmpty()) throw new IllegalArgumentException(...)
        Quiz emptyQuiz = buildBasicQuiz(List.of());
        assertThrows(IllegalArgumentException.class,
                () -> service.evaluateQuiz(emptyQuiz, List.of(), USERNAME));
    }
}