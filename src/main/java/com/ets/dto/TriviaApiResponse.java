package com.ets.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

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