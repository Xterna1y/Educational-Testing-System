package com.ets.api;

import com.ets.dto.TriviaApiResponse;
import com.ets.exception.ApiException;

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