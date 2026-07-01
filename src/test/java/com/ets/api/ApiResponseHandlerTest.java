package com.ets.api;

import com.ets.dto.TriviaApiResponse;
import com.ets.exception.ApiException;
import com.google.gson.Gson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link ApiResponseHandler}.
 *
 * <p>TriviaApiResponse has no setters — instances are built via Gson
 * from JSON strings to match the real deserialization path.</p>
 */
class ApiResponseHandlerTest {

    private final Gson gson = new Gson();

    /** Builds a TriviaApiResponse with the given response_code via Gson. */
    private TriviaApiResponse responseWithCode(int code) {
        String json = "{\"response_code\":" + code + ",\"results\":[]}";
        return gson.fromJson(json, TriviaApiResponse.class);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 1. validate passes silently when response code is 0 (success)
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("validate: response code 0 passes without throwing")
    void validate_responseCodeZero_doesNotThrow() {
        ApiResponseHandler handler = new ApiResponseHandler();
        TriviaApiResponse response = responseWithCode(0);

        assertDoesNotThrow(() -> handler.validate(response),
                "Response code 0 should be valid and not throw");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. assertThrows — validate throws ApiException for null response
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("validate: null response throws ApiException")
    void validate_nullResponse_throwsApiException() {
        ApiResponseHandler handler = new ApiResponseHandler();

        assertThrows(ApiException.class,
                () -> handler.validate(null),
                "A null response should throw ApiException");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. @ParameterizedTest — all non-zero error codes throw ApiException
    //    (1=no results, 2=invalid param, 3=token not found, 4=token empty, 5=rate limit)
    // ─────────────────────────────────────────────────────────────────────────

    @ParameterizedTest(name = "response code {0} throws ApiException")
    @ValueSource(ints = {1, 2, 3, 4, 5})
    @DisplayName("validate: any non-zero response code throws ApiException")
    void validate_nonZeroResponseCode_throwsApiException(int errorCode) {
        ApiResponseHandler handler = new ApiResponseHandler();
        TriviaApiResponse response = responseWithCode(errorCode);

        assertThrows(ApiException.class,
                () -> handler.validate(response),
                "Response code " + errorCode + " should throw ApiException");
    }
}