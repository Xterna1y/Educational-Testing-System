package com.ets.api;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link TriviaApiClient}.
 *
 * <p>Tests make real HTTP calls to Open Trivia DB. Each test includes a
 * short sleep to avoid the API's 5-second rate-limit window.</p>
 */
class TriviaApiClientTest {

    // ─────────────────────────────────────────────────────────────────────────
    // 1. Response is non-null and non-blank for a standard request
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("fetchQuestions: valid request returns a non-blank JSON string")
    void fetchQuestions_validRequest_returnsNonBlankJson() throws Exception {
        TriviaApiClient client = new TriviaApiClient();

        String json = client.fetchQuestions(5, null, "easy");

        assertNotNull(json, "Response body should never be null");
        assertFalse(json.isBlank(), "Response body should not be blank");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. Response body always contains the expected top-level fields
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("fetchQuestions: response always contains response_code and results fields")
    void fetchQuestions_validRequest_containsExpectedFields() throws Exception {
        Thread.sleep(5500); // avoid rate limit
        TriviaApiClient client = new TriviaApiClient();

        String json = client.fetchQuestions(5, null, "easy");

        assertTrue(json.contains("response_code"), "JSON should contain 'response_code'");
        assertTrue(json.contains("results"),       "JSON should contain 'results'");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. @ParameterizedTest — different difficulty values each return valid JSON
    // ─────────────────────────────────────────────────────────────────────────

    @ParameterizedTest(name = "difficulty={0} returns valid JSON")
    @ValueSource(strings = {"easy", "medium", "hard"})
    @DisplayName("fetchQuestions: each difficulty level returns valid JSON")
    void fetchQuestions_eachDifficultyLevel_returnsValidJson(String difficulty) throws Exception {
        Thread.sleep(5500); // avoid rate limit between parameterized runs
        TriviaApiClient client = new TriviaApiClient();

        String json = client.fetchQuestions(3, null, difficulty);

        assertNotNull(json);
        assertTrue(json.contains("response_code"),
                "Response for difficulty=" + difficulty + " should contain response_code");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 4. Requesting a specific category includes category field in results
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("fetchQuestions: category filter includes category in response")
    void fetchQuestions_withCategoryFilter_returnsCategoryInResponse() throws Exception {
        Thread.sleep(5500);
        TriviaApiClient client = new TriviaApiClient();

        // Category 18 = Computer Science in Open Trivia DB
        String json = client.fetchQuestions(3, 18, "easy");

        assertNotNull(json);
        assertTrue(json.contains("response_code"));
    }
}