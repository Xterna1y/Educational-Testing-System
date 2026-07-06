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
 * API-success and API-failure scenarios without making real network calls.
 * The cache and seed repositories are always separate temp files, mirroring
 * production where the writable cache must never share a path with the
 * read-only fallback seed.</p>
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
    // 1. When API succeeds, questions are cached and the seed is untouched
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getQuestions: successful API call caches live questions and leaves the seed untouched")
    void getQuestions_apiSucceeds_writesCacheAndLeavesSeedUntouched() {
        CachedQuestionRepository cache = new CachedQuestionRepository(tempDir.resolve("cache.json"));
        CachedQuestionRepository seed = new CachedQuestionRepository(tempDir.resolve("seed.json"));

        // Pre-seed the read-only fallback file with its own distinct data
        seed.saveQuestions(Arrays.asList(makeQuestion("seed1"), makeQuestion("seed2")));

        // Stub: always returns 2 live questions without a real network call
        TriviaApiService stubApi = new TriviaApiService() {
            @Override
            public List<Question> getQuestions(int amount, Integer category, Difficulty difficulty) {
                return Arrays.asList(makeQuestion("live1"), makeQuestion("live2"));
            }
        };

        QuestionProvider provider = new QuestionProvider(stubApi, cache, seed);

        List<Question> result = provider.getQuestions(2, null, Difficulty.EASY);

        assertEquals(2, result.size(), "Should return the 2 questions from the API");
        assertEquals("live1", result.get(0).getId(), "First question should be the live result");

        List<Question> cached = cache.loadQuestions();
        assertEquals(2, cached.size(), "Live questions should have been saved to the cache");
        assertEquals("live1", cached.get(0).getId(), "Cache should hold the live questions");

        List<Question> seedAfter = seed.loadQuestions();
        assertEquals(2, seedAfter.size(), "Seed file must not be modified by a successful API call");
        assertEquals("seed1", seedAfter.get(0).getId(), "Seed content must remain exactly what it was");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. When API fails and the cache has data, provider uses the cache
    //    (not the seed)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getQuestions: API failure with cache present uses cached questions, not the seed")
    void getQuestions_apiFailsWithCachePresent_usesCachedQuestions() {
        CachedQuestionRepository cache = new CachedQuestionRepository(tempDir.resolve("cache.json"));
        CachedQuestionRepository seed = new CachedQuestionRepository(tempDir.resolve("seed.json"));

        cache.saveQuestions(Arrays.asList(makeQuestion("cached1"), makeQuestion("cached2")));
        seed.saveQuestions(Arrays.asList(makeQuestion("seed1")));

        // Stub: always throws to simulate network failure
        TriviaApiService failingApi = new TriviaApiService() {
            @Override
            public List<Question> getQuestions(int amount, Integer category, Difficulty difficulty) {
                throw new RuntimeException("Simulated network failure");
            }
        };

        QuestionProvider provider = new QuestionProvider(failingApi, cache, seed);

        List<Question> result = provider.getQuestions(2, null, Difficulty.EASY);

        assertEquals(2, result.size(), "Should fall back to the 2 cached questions on API failure");
        assertEquals("cached1", result.get(0).getId(), "Result should come from the cache, not the seed");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. When API fails and there is no cache, provider falls back to the seed
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getQuestions: API failure with no cache falls back to the seed")
    void getQuestions_apiFailsWithNoCache_usesSeedQuestions() {
        // Cache file is never written to, so it does not exist on disk
        CachedQuestionRepository cache = new CachedQuestionRepository(tempDir.resolve("cache.json"));
        CachedQuestionRepository seed = new CachedQuestionRepository(tempDir.resolve("seed.json"));

        seed.saveQuestions(Arrays.asList(makeQuestion("seed1"), makeQuestion("seed2")));

        TriviaApiService failingApi = new TriviaApiService() {
            @Override
            public List<Question> getQuestions(int amount, Integer category, Difficulty difficulty) {
                throw new RuntimeException("Simulated network failure");
            }
        };

        QuestionProvider provider = new QuestionProvider(failingApi, cache, seed);

        List<Question> result = provider.getQuestions(2, null, Difficulty.EASY);

        assertEquals(2, result.size(), "Should fall back to the 2 seed questions when no cache exists");
        assertEquals("seed1", result.get(0).getId(), "Result should come from the seed");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 4. When API fails and both cache and seed are empty, returns empty list
    //    (no exception thrown)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getQuestions: API failure with empty cache and empty seed returns empty list without throwing")
    void getQuestions_apiFailsAndCacheAndSeedEmpty_returnsEmptyListWithoutThrowing() {
        CachedQuestionRepository emptyCache = new CachedQuestionRepository(tempDir.resolve("empty_cache.json"));
        CachedQuestionRepository emptySeed = new CachedQuestionRepository(tempDir.resolve("empty_seed.json"));

        TriviaApiService failingApi = new TriviaApiService() {
            @Override
            public List<Question> getQuestions(int amount, Integer category, Difficulty difficulty) {
                throw new RuntimeException("Simulated network failure");
            }
        };

        QuestionProvider provider = new QuestionProvider(failingApi, emptyCache, emptySeed);

        // Should not throw — graceful degradation to empty list
        List<Question> result = assertDoesNotThrow(
                () -> provider.getQuestions(5, null, Difficulty.EASY),
                "QuestionProvider must never throw even when the API, cache, and seed all fail"
        );

        assertNotNull(result, "Result must not be null even on total failure");
        assertTrue(result.isEmpty(), "Result should be empty when the API, cache, and seed all fail");
    }
}
