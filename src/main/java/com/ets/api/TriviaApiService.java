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