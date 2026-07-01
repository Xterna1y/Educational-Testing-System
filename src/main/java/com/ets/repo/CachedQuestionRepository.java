package com.ets.repo;

import com.ets.model.Question;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class CachedQuestionRepository {

    private final Path filePath;

    private final Gson gson =
            new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

    public CachedQuestionRepository(
            Path filePath
    ) {
        this.filePath = filePath;
    }

    public void saveQuestions(
            List<Question> questions
    ) {

        try {
            String json =
                    gson.toJson(questions);

            Files.writeString(
                    filePath,
                    json,
                    StandardCharsets.UTF_8
            );
        }
        catch (IOException e) {
            throw new RuntimeException(
                    "Failed to save questions.",
                    e
            );
        }
    }

    public List<Question> loadQuestions() {

        try {

            if (!Files.exists(filePath)) {
                return new ArrayList<>();
            }

            String json =
                    Files.readString(
                            filePath,
                            StandardCharsets.UTF_8
                    );

            if (json.isBlank()) {
                return new ArrayList<>();
            }

            Type type =
                    new TypeToken<List<Question>>() {
                    }.getType();

            List<Question> questions =
                    gson.fromJson(
                            json,
                            type
                    );

            return questions == null
                    ? new ArrayList<>()
                    : questions;
        }
        catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load questions.",
                    e
            );
        }
    }
}