package com.ets.repo;

import com.ets.model.Result;
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
/**
 * JSON file-based implementation of {@link ResultRepository}.
 * <p>
 * Persists quiz {@link Result} records to a single JSON file using
 * Gson, and reads them back for the results-history feature. Results
 * are appended on save and can be filtered by username on retrieval.
 */

public class JsonResultRepository
        implements ResultRepository {

    private final Path filePath;

    private final Gson gson =
            new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

    public JsonResultRepository(Path filePath) {
        this.filePath = filePath;
    }

    @Override
    public void saveResult(Result result) {
        try {
            List<Result> results =
                    getAllResults();

            results.add(result);

            String json =
                    gson.toJson(results);

            Files.writeString(
                    filePath,
                    json,
                    StandardCharsets.UTF_8
            );
        }
        catch (IOException e) {
            throw new RuntimeException(
                    "Failed to save result.",
                    e
            );
        }
    }

    @Override
    public List<Result> getAllResults() {
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
                    new TypeToken<List<Result>>() {
                    }.getType();

            List<Result> results =
                    gson.fromJson(
                            json,
                            type
                    );

            return results == null
                    ? new ArrayList<>()
                    : results;
        }
        catch (IOException e) {
            throw new RuntimeException(
                    "Failed to load results.",
                    e
            );
        }
    }

    @Override
    public List<Result> getResultsByQuiz(
            String quizId
    ) {

        List<Result> matches =
                new ArrayList<>();

        for (Result result :
                getAllResults()) {

            if (result.getQuizId()
                    .equals(quizId)) {
                matches.add(result);
            }
        }

        return matches;
    }

    /**
     * Returns all results for the given student, most recently completed first.
     */
    @Override
    public List<Result> getResultsByUsername(String username) {
        List<Result> matches = new ArrayList<>();

        for (Result result : getAllResults()) {
            if (result.getUsername().equalsIgnoreCase(username)) {
                matches.add(result);
            }
        }

        matches.sort((a, b) -> b.getCompletedAt().compareTo(a.getCompletedAt()));
        return matches;
    }
}