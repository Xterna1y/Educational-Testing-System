package com.ets.dto;

import com.ets.api.TriviaApiService;
import com.ets.model.Question;
import com.google.gson.annotations.SerializedName;

import java.util.List;
/**
 * Data transfer object mapping a single question object in the Open
 * Trivia Database API's JSON response.
 * <p>
 * Holds the raw API representation (category, type, difficulty,
 * question text, correct answer, and incorrect answers) before
 * conversion into the domain {@link Question} model by
 * {@link TriviaApiService}. Snake_case JSON keys are bound via
 * {@link com.google.gson.annotations.SerializedName}.
 */

public class TriviaQuestionDTO {

    private String category;
    private String type;
    private String difficulty;
    private String question;

    @SerializedName("correct_answer")
    private String correctAnswer;

    @SerializedName("incorrect_answers")
    private List<String> incorrectAnswers;

    public String getCategory() {
        return category;
    }

    public String getType() {
        return type;
    }

    public String getDifficulty() {
        return difficulty;
    }

    public String getQuestion() {
        return question;
    }

    public String getCorrectAnswer() {
        return correctAnswer;
    }

    public List<String> getIncorrectAnswers() {
        return incorrectAnswers;
    }
}