package com.ets.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
/**
 * Low-level HTTP client for the Open Trivia Database API
 * (https://opentdb.com).
 * <p>
 * Responsible only for building the request URL from the given
 * parameters and returning the raw JSON response body as a string.
 * Parsing and validation are handled by {@link TriviaApiService}
 * and {@link ApiResponseHandler} respectively.
 */
public class TriviaApiClient {

    private static final String BASE_URL =
            "https://opentdb.com/api.php";

    private final HttpClient client;

    public TriviaApiClient() {
        this.client = HttpClient.newHttpClient();
    }

    public String fetchQuestions(
            int amount,
            Integer category,
            String difficulty
    ) throws IOException, InterruptedException {

        StringBuilder url =
                new StringBuilder(
                        BASE_URL +
                                "?amount=" +
                                amount
                );

        if (category != null) {
            url.append("&category=")
                    .append(category);
        }

        if (difficulty != null &&
                !difficulty.isBlank()) {
            url.append("&difficulty=")
                    .append(difficulty);
        }

        HttpRequest request =
                HttpRequest.newBuilder()
                        .uri(
                                URI.create(
                                        url.toString()
                                )
                        )
                        .GET()
                        .build();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers
                                .ofString()
                );

        return response.body();
    }
}