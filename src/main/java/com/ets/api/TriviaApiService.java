package com.ets.api;

import com.ets.dto.TriviaApiResponse;
import com.ets.dto.TriviaQuestionDTO;
import com.ets.model.Difficulty;
import com.ets.model.Question;
import com.google.gson.Gson;
import org.apache.commons.text.StringEscapeUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
/**
 * High-level service that retrieves quiz questions from the Open
 * Trivia Database API and converts them into domain {@link Question}
 * objects.
 * <p>
 * Coordinates the full pipeline: fetch raw JSON via
 * {@link TriviaApiClient}, deserialize it with Gson into
 * {@link TriviaApiResponse}, validate it with
 * {@link ApiResponseHandler}, and map each
 * {@link TriviaQuestionDTO} to a {@link Question}. Any failure in
 * this pipeline is wrapped in an unchecked exception so callers can
 * apply a single fallback strategy.
 */

public class TriviaApiService {

    private final TriviaApiClient client;
    private final ApiResponseHandler handler;
    private final Gson gson;

    public TriviaApiService() {
        this.client = new TriviaApiClient();
        this.handler = new ApiResponseHandler();
        this.gson = new Gson();
    }

    public List<Question> getQuestions(
            int amount,
            Integer category,
            Difficulty difficulty
    ) {

        try {

            String json =
                    client.fetchQuestions(
                            amount,
                            category,
                            difficulty.name().toLowerCase()
                    );

            TriviaApiResponse response =
                    gson.fromJson(
                            json,
                            TriviaApiResponse.class
                    );

            handler.validate(response);

            return convertQuestions(
                    response.getResults()
            );

        } catch (Exception e) {
            throw new RuntimeException(
                    "Unable to fetch questions.",
                    e
            );
        }
    }

    private List<Question> convertQuestions(
            List<TriviaQuestionDTO> dtos
    ) {

        List<Question> questions =
                new ArrayList<>();

        for (TriviaQuestionDTO dto : dtos) {

            List<String> options =
                    new ArrayList<>(
                            dto.getIncorrectAnswers()
                    );

            options.add(
                    dto.getCorrectAnswer()
            );

            Collections.shuffle(options);

            int correctIndex =
                    options.indexOf(
                            dto.getCorrectAnswer()
                    );

            questions.add(
                    new Question(
                            UUID.randomUUID()
                                    .toString()
                                    .substring(0, 8),
                            StringEscapeUtils.unescapeHtml4(dto.getCategory()),
                            StringEscapeUtils.unescapeHtml4(dto.getQuestion()),
                            options,
                            correctIndex,
                            Difficulty.valueOf(
                                    dto.getDifficulty()
                                            .toUpperCase()
                            )
                    )
            );
        }

        return questions;
    }
}