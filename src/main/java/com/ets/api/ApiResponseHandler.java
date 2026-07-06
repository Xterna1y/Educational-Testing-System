package com.ets.api;

import com.ets.dto.TriviaApiResponse;
import com.ets.exception.ApiException;
/**
 * Validates responses returned by the Open Trivia Database API before
 * they are converted into domain objects.
 * <p>
 * A response is considered invalid if it is {@code null} or if its
 * response code is non-zero, in which case an {@link ApiException}
 * is thrown. This class has a single responsibility: response
 * validation. It performs no fetching, parsing, or conversion.
 */

public class ApiResponseHandler {

    public void validate(
            TriviaApiResponse response
    ) {

        if (response == null) {
            throw new ApiException(
                    "No response from API."
            );
        }

        if (response.getResponseCode() != 0) {
            throw new ApiException(
                    "Trivia API returned error code: "
                            + response.getResponseCode()
            );
        }
    }
}