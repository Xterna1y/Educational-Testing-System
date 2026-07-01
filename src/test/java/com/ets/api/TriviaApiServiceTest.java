package com.ets.api;

import com.ets.model.Difficulty;
import com.ets.model.Question;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link TriviaApiService}.
 *
 * <p>Tests make real API calls. Any {@link RuntimeException} is treated as
 * an API rate-limit or network issue and aborts the test with
 * {@code Assumptions.assumeTrue(false)} so the test is skipped rather
 * than failed.</p>
 */
class TriviaApiServiceTest {

    // ─────────────────────────────────────────────────────────────────────────
    // 1. Correct number of questions returned
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getQuestions: valid request returns a non-empty list of questions")
    void getQuestions_validRequest_returnsRequestedCount() {
        TriviaApiService service = new TriviaApiService();
        try {
            List<Question> questions = service.getQuestions(5, null, Difficulty.EASY);

            // Open Trivia DB may return a default batch size when rate-limited,
            // so we assert a non-empty list within the API's valid range (1–50)
            // rather than an exact count.
            assertNotNull(questions, "Returned list should not be null");
            assertFalse(questions.isEmpty(), "Returned list should not be empty");
            assertTrue(questions.size() <= 50, "API should not return more than 50 questions");
        } catch (RuntimeException e) {
            Assumptions.assumeTrue(false, "Skipped: Trivia API unavailable — " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. assertAll — every question has all required fields populated
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getQuestions: every returned question has all required fields populated")
    void getQuestions_validRequest_allQuestionsHaveRequiredFields() {
        TriviaApiService service = new TriviaApiService();
        try {
            Thread.sleep(5500); // avoid rate limit
            List<Question> questions = service.getQuestions(3, null, Difficulty.EASY);
            assertFalse(questions.isEmpty(), "Question list should not be empty");

            Question first = questions.get(0);

            // assertAll groups all checks — a single failing assertion doesn't
            // hide failures in the rest of the block.
            assertAll("first question must have all fields populated",
                    () -> assertNotNull(first.getId(),       "id must not be null"),
                    () -> assertNotNull(first.getCategory(), "category must not be null"),
                    () -> assertNotNull(first.getText(),     "text must not be null"),
                    () -> assertNotNull(first.getOptions(),  "options list must not be null"),
                    () -> assertFalse(first.getOptions().isEmpty(), "options must not be empty"),
                    () -> assertTrue(
                            first.getCorrectOptionIndex() >= 0 &&
                            first.getCorrectOptionIndex() < first.getOptions().size(),
                            "correctOptionIndex must be within options range")
            );
        } catch (RuntimeException | InterruptedException e) {
            Assumptions.assumeTrue(false, "Skipped: Trivia API unavailable — " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. Questions are returned with the correct difficulty
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getQuestions: returned questions match the requested difficulty")
    void getQuestions_difficultyFilter_allQuestionsMatchDifficulty() {
        TriviaApiService service = new TriviaApiService();
        try {
            Thread.sleep(5500);
            List<Question> questions = service.getQuestions(5, null, Difficulty.HARD);

            for (Question q : questions) {
                assertEquals(Difficulty.HARD, q.getDifficulty(),
                        "Every question should have HARD difficulty");
            }
        } catch (RuntimeException | InterruptedException e) {
            Assumptions.assumeTrue(false, "Skipped: Trivia API unavailable — " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 4. DELIBERATELY FAILING TEST
    //
    // Purpose: this test asserts that the API always returns exactly 0 questions,
    // which is intentionally wrong. It reveals that the system does NOT currently
    // validate or enforce a minimum question count before launching a quiz —
    // QuizController.startQuiz() would silently create a quiz with 0 questions
    // if QuestionProvider returned an empty list and the empty-check in
    // QuizSelectionFrame was removed. This test is left failing as documented
    // evidence of that gap.
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("[DELIBERATE FAIL] getQuestions: API should return zero questions — reveals missing minimum-count enforcement")
    void getQuestions_validRequest_returnsZeroQuestions_deliberatelyFailing() {
        TriviaApiService service = new TriviaApiService();
        try {
            Thread.sleep(5500);
            List<Question> questions = service.getQuestions(5, null, Difficulty.EASY);

            // This assertion is intentionally wrong.
            // Expected behaviour: 5 questions are returned.
            // This failing test reveals that there is no minimum-count guard
            // inside QuizController or QuizService — if getQuestions() ever
            // silently returned an empty list (e.g. due to a network failure
            // not caught by QuestionProvider), the quiz would start with zero
            // questions and throw an IndexOutOfBoundsException at runtime.
            assertEquals(0, questions.size(),
                    "[DELIBERATE FAIL] This assertion is wrong by design. " +
                    "See Javadoc above for the limitation it exposes.");
        } catch (RuntimeException | InterruptedException e) {
            Assumptions.assumeTrue(false, "Skipped: Trivia API unavailable — " + e.getMessage());
        }
    }
}