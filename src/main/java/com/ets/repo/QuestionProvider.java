package com.ets.repo;

import com.ets.api.TriviaApiService;
import com.ets.model.Difficulty;
import com.ets.model.Question;

import java.util.List;

/**
 * Single entry point for obtaining quiz questions.
 *
 * <p>Tries the live Open Trivia DB API first via {@link TriviaApiService}.
 * On success, the fetched questions are cached to disk via
 * {@link CachedQuestionRepository} so subsequent calls and offline
 * sessions still have data available.</p>
 *
 * <p>On any failure (network error, API error response, malformed JSON),
 * this falls back to whatever is already cached on disk
 * (seeded from {@code fallback_questions.json}). The caller is never
 * shown a raw exception or stack trace — only an empty list if both
 * sources fail, which the GUI layer is responsible for messaging.</p>
 */
public final class QuestionProvider {

    private final TriviaApiService apiService;
    private final CachedQuestionRepository cacheRepository;

    public QuestionProvider(TriviaApiService apiService, CachedQuestionRepository cacheRepository) {
        this.apiService = apiService;
        this.cacheRepository = cacheRepository;
    }

    /**
     * Fetches questions for a quiz, preferring the live API and
     * gracefully degrading to cached/local data on failure.
     *
     * @param amount     number of questions requested from the API
     * @param category   Open Trivia DB category id, or {@code null} for any category
     * @param difficulty difficulty filter passed to the API
     * @return a list of questions; never {@code null}, may be empty if both
     *         the API and the local cache are unavailable
     */
    public List<Question> getQuestions(int amount, Integer category, Difficulty difficulty) {
        try {
            List<Question> fetched = apiService.getQuestions(amount, category, difficulty);

            if (fetched != null && !fetched.isEmpty()) {
                cacheRepository.saveQuestions(fetched);
                return fetched;
            }
        } catch (Exception e) {
            // Never surface the raw exception/stack trace to the user.
            // Log for developer diagnostics and fall through to the cache.
            System.err.println("[QuestionProvider] Live API fetch failed, falling back to cache: "
                    + e.getMessage());
        }

        return cacheRepository.loadQuestions();
    }
}