package com.ets.repo;

import com.ets.api.TriviaApiService;
import com.ets.model.Difficulty;
import com.ets.model.Question;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link QuestionProvider}.
 *
 * <p>Uses anonymous subclasses of {@link TriviaApiService} to simulate
 * API-success and API-failure scenarios without making real network calls.</p>
 */
class QuestionProviderTest {

    @TempDir
    Path tempDir;

    private Question makeQuestion(String id) {
        return new Question(
                id, "Science", "Sample question?",
                Arrays.asList("A", "B", "C", "D"),
                0, Difficulty.EASY
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 1. When API succeeds, questions are returned and cached to disk
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getQuestions: successful API call returns live questions and caches them")
    void getQuestions_apiSucceeds_returnsLiveQuestionsAndCachesThem() {
        Path cacheFile = tempDir.resolve("cache.json");
        CachedQuestionRepository cache = new CachedQuestionRepository(cacheFile);

        // Stub: always returns 2 questions without a real network call
        TriviaApiService stubApi = new TriviaApiService() {
            @Override
            public List<Question> getQuestions(int amount, Integer category, Difficulty difficulty) {
                return Arrays.asList(makeQuestion("live1"), makeQuestion("live2"));
            }
        };

        QuestionProvider provider = new QuestionProvider(stubApi, cache);

        List<Question> result = provider.getQuestions(2, null, Difficulty.EASY);

        assertEquals(2, result.size(), "Should return the 2 questions from the API");
        assertEquals("live1", result.get(0).getId(), "First question should be the live result");

        // Verify the questions were also cached
        List<Question> cached = cache.loadQuestions();
        assertEquals(2, cached.size(), "Live questions should have been saved to cache");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. When API fails, provider falls back to the cache
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getQuestions: API failure falls back to cached questions silently")
    void getQuestions_apiFails_fallsBackToCachedQuestions() {
        Path cacheFile = tempDir.resolve("cache.json");
        CachedQuestionRepository cache = new CachedQuestionRepository(cacheFile);

        // Pre-seed the cache with fallback questions
        cache.saveQuestions(Arrays.asList(makeQuestion("cached1"), makeQuestion("cached2")));

        // Stub: always throws to simulate network failure
        TriviaApiService failingApi = new TriviaApiService() {
            @Override
            public List<Question> getQuestions(int amount, Integer category, Difficulty difficulty) {
                throw new RuntimeException("Simulated network failure");
            }
        };

        QuestionProvider provider = new QuestionProvider(failingApi, cache);

        List<Question> result = provider.getQuestions(2, null, Difficulty.EASY);

        assertEquals(2, result.size(), "Should fall back to 2 cached questions on API failure");
        assertEquals("cached1", result.get(0).getId(), "First fallback question should be 'cached1'");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. When API fails and cache is empty, returns empty list (no exception thrown)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getQuestions: API failure with empty cache returns empty list without throwing")
    void getQuestions_apiFailsAndCacheEmpty_returnsEmptyListWithoutThrowing() {
        Path cacheFile = tempDir.resolve("empty_cache.json");
        CachedQuestionRepository emptyCache = new CachedQuestionRepository(cacheFile);

        TriviaApiService failingApi = new TriviaApiService() {
            @Override
            public List<Question> getQuestions(int amount, Integer category, Difficulty difficulty) {
                throw new RuntimeException("Simulated network failure");
            }
        };

        QuestionProvider provider = new QuestionProvider(failingApi, emptyCache);

        // Should not throw — graceful degradation to empty list
        List<Question> result = assertDoesNotThrow(
                () -> provider.getQuestions(5, null, Difficulty.EASY),
                "QuestionProvider must never throw even when both API and cache fail"
        );

        assertNotNull(result, "Result must not be null even on total failure");
        assertTrue(result.isEmpty(), "Result should be empty when both API and cache fail");
    }
}