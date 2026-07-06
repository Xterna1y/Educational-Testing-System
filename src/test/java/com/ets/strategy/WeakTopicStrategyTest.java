package com.ets.strategy;

import com.ets.model.Answer;
import com.ets.model.Difficulty;
import com.ets.model.Question;
import com.ets.model.Result;
import com.ets.repo.ResultRepository;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link WeakTopicStrategy}.
 *
 * <p>Verifies weak-category prioritisation using historical results, the
 * random fallback for users with no usable history, and general count/bank
 * handling. Uses an in-memory {@link ResultRepository} stub so history can
 * be set up directly without touching disk via {@code JsonResultRepository}.</p>
 */
class WeakTopicStrategyTest {

    private static final String USERNAME = "student1";
    private static final List<String> OPTIONS = List.of("A", "B", "C", "D");

    private Question makeQuestion(String id, String category) {
        return new Question(id, category, "Sample question?", OPTIONS, 0, Difficulty.EASY);
    }

    private List<Question> mixedBank() {
        return List.of(
                makeQuestion("q1", "Math"),
                makeQuestion("q2", "Math"),
                makeQuestion("q3", "Math")
        );
    }

    private WeakTopicStrategy strategyWithNoHistory() {
        return new WeakTopicStrategy(USERNAME, new StubResultRepository(new ArrayList<>()));
    }

    /** In-memory stand-in for {@link ResultRepository}, backed by a plain list. */
    private static class StubResultRepository implements ResultRepository {
        private final List<Result> results;

        StubResultRepository(List<Result> results) {
            this.results = results;
        }

        @Override
        public void saveResult(Result result) {
            results.add(result);
        }

        @Override
        public List<Result> getAllResults() {
            return results;
        }

        @Override
        public List<Result> getResultsByQuiz(String quizId) {
            return List.of();
        }

        @Override
        public List<Result> getResultsByUsername(String username) {
            List<Result> matches = new ArrayList<>();
            for (Result r : results) {
                if (r.getUsername().equalsIgnoreCase(username)) {
                    matches.add(r);
                }
            }
            return matches;
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 1. User with history gets questions from their weakest category first
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void selectQuestions_userWithHistory_prioritisesWeakestCategory() {
        // Science: 0/2 correct historically (weak). History: 2/2 correct (strong).
        List<Answer> answers = List.of(
                new Answer("old1", 0, "Science", false),
                new Answer("old2", 0, "Science", false),
                new Answer("old3", 0, "History", true),
                new Answer("old4", 0, "History", true)
        );
        Result pastResult = new Result(USERNAME, "quiz1", answers, 0, 40);
        ResultRepository repo = new StubResultRepository(new ArrayList<>(List.of(pastResult)));

        WeakTopicStrategy strategy = new WeakTopicStrategy(USERNAME, repo);

        List<Question> bank = List.of(
                makeQuestion("h1", "History"),
                makeQuestion("s1", "Science")
        );

        List<Question> selected = strategy.selectQuestions(bank, 1);

        assertEquals(1, selected.size());
        assertEquals("Science", selected.get(0).getCategory(),
                "The single returned question should come from the weaker category (Science)");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. User with no history falls back to random selection
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void selectQuestions_userWithNoHistory_fallsBackToRandomSelection() {
        WeakTopicStrategy strategy = strategyWithNoHistory();

        List<Question> bank = List.of(
                makeQuestion("h1", "History"),
                makeQuestion("s1", "Science"),
                makeQuestion("m1", "Math")
        );

        List<Question> selected = strategy.selectQuestions(bank, 3);

        assertEquals(3, selected.size());
        assertTrue(selected.containsAll(bank) && bank.containsAll(selected),
                "With no history, all questions should still be returned (order may be shuffled)");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. A weak category with fewer available questions than requested
    //    doesn't crash — it just returns what's available
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void selectQuestions_weakCategoryHasFewerQuestionsThanRequested_returnsAvailableWithoutCrashing() {
        List<Answer> answers = List.of(new Answer("old1", 0, "Science", false));
        Result pastResult = new Result(USERNAME, "quiz1", answers, 0, 10);
        ResultRepository repo = new StubResultRepository(new ArrayList<>(List.of(pastResult)));

        WeakTopicStrategy strategy = new WeakTopicStrategy(USERNAME, repo);

        // Only 1 question exists in the bank for the (weak) Science category
        List<Question> bank = List.of(makeQuestion("s1", "Science"));

        List<Question> selected = assertDoesNotThrow(
                () -> strategy.selectQuestions(bank, 10),
                "Requesting more questions than exist for the weak category must not throw"
        );

        assertEquals(1, selected.size(), "Should return all available questions, capped at bank size");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // General count / bank handling (no-history fallback path)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    void selectQuestions_countLessThanBank_returnsExactCount() {
        assertEquals(2, strategyWithNoHistory().selectQuestions(mixedBank(), 2).size());
    }

    @Test
    void selectQuestions_countExceedsBankSize_returnsEntireBank() {
        assertEquals(3, strategyWithNoHistory().selectQuestions(mixedBank(), 100).size());
    }

    @Test
    void selectQuestions_emptyBank_returnsEmptyList() {
        assertTrue(strategyWithNoHistory().selectQuestions(List.of(), 5).isEmpty());
    }

    @Test
    void selectQuestions_countZero_returnsEmptyList() {
        assertTrue(strategyWithNoHistory().selectQuestions(mixedBank(), 0).isEmpty());
    }

    @Test
    void selectQuestions_returnedQuestions_areSubsetOfBank() {
        List<Question> bank = mixedBank();
        List<Question> result = strategyWithNoHistory().selectQuestions(bank, 2);
        assertTrue(bank.containsAll(result));
    }
}
