package com.ets.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;
/**
 * Data transfer object mapping the top-level JSON structure returned
 * by the Open Trivia Database API.
 * <p>
 * Field names are bound to the API's snake_case JSON keys via Gson's
 * {@link com.google.gson.annotations.SerializedName}. A response code
 * of {@code 0} indicates success; any other value indicates an API
 * error (see the Open Trivia DB documentation for code meanings).
 */

public class TriviaApiResponse {

    @SerializedName("response_code")
    private int responseCode;

    private List<TriviaQuestionDTO> results;

    public int getResponseCode() {
        return responseCode;
    }

    public List<TriviaQuestionDTO> getResults() {
        return results;
    }
}