package com.ets.repo;

import com.ets.model.Answer;
import com.ets.model.Result;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link JsonResultRepository}.
 *
 * <p>Uses JUnit 5 temp directories so tests never touch the real results.json.</p>
 */
class ResultRepositoryTest {

    @TempDir
    Path tempDir;

    // ─────────────────────────────────────────────────────────────────────────
    // 1. saveResult persists a single result and it is retrievable
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("saveResult: single result is persisted and retrievable")
    void saveResult_singleResult_persistedAndRetrievable() {
        Path file = tempDir.resolve("results.json");
        ResultRepository repo = new JsonResultRepository(file);

        repo.saveResult(new Result("quiz1", new ArrayList<>(), 80, 100));

        List<Result> results = repo.getAllResults();
        assertEquals(1, results.size(), "Exactly one result should be stored");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. assertAll — saved result has all fields correctly persisted
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("saveResult: all fields of the saved result are correctly persisted")
    void saveResult_validResult_allFieldsPersistedCorrectly() {
        Path file = tempDir.resolve("results.json");
        ResultRepository repo = new JsonResultRepository(file);

        repo.saveResult(new Result("quiz99", new ArrayList<>(), 70, 100));

        Result saved = repo.getAllResults().get(0);

        assertAll("saved result must have all fields intact",
                () -> assertEquals("quiz99", saved.getQuizId(), "quizId should match"),
                () -> assertEquals(70, saved.getScore(),        "score should match"),
                () -> assertEquals(100, saved.getTotalPoints(), "totalPoints should match"),
                () -> assertNotNull(saved.getAnswers(),         "answers list must not be null")
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. getAllResults returns all saved results
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getAllResults: returns all results saved across multiple calls")
    void getAllResults_multipleResultsSaved_returnsAllResults() {
        Path file = tempDir.resolve("results.json");
        ResultRepository repo = new JsonResultRepository(file);

        repo.saveResult(new Result("quiz1", new ArrayList<>(), 80, 100));
        repo.saveResult(new Result("quiz2", new ArrayList<>(), 60, 100));

        assertEquals(2, repo.getAllResults().size(), "Should return 2 results");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 4. getAllResults returns an empty list when no results have been saved
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getAllResults: returns empty list when no results are stored")
    void getAllResults_noResultsSaved_returnsEmptyList() {
        Path file = tempDir.resolve("empty_results.json");
        ResultRepository repo = new JsonResultRepository(file);

        List<Result> results = repo.getAllResults();

        assertNotNull(results, "getAllResults should never return null");
        assertTrue(results.isEmpty(), "List should be empty when no results have been saved");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 5. getResultsByQuiz filters results correctly by quiz ID
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getResultsByQuiz: filters results correctly by quiz ID")
    void getResultsByQuiz_mixedResults_returnsOnlyMatchingQuizId() {
        Path file = tempDir.resolve("results.json");
        ResultRepository repo = new JsonResultRepository(file);

        repo.saveResult(new Result("quiz1", new ArrayList<>(), 80, 100));
        repo.saveResult(new Result("quiz1", new ArrayList<>(), 70, 100));
        repo.saveResult(new Result("quiz2", new ArrayList<>(), 90, 100));

        List<Result> quiz1Results = repo.getResultsByQuiz("quiz1");

        assertEquals(2, quiz1Results.size(), "Should return only the 2 results for quiz1");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 6. getResultsByQuiz returns empty list for a quiz ID with no results
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getResultsByQuiz: returns empty list for a quiz ID with no saved results")
    void getResultsByQuiz_unknownQuizId_returnsEmptyList() {
        Path file = tempDir.resolve("results.json");
        ResultRepository repo = new JsonResultRepository(file);

        repo.saveResult(new Result("quiz1", new ArrayList<>(), 80, 100));

        List<Result> results = repo.getResultsByQuiz("unknownQuiz");

        assertTrue(results.isEmpty(), "Should return empty list for unrecognised quiz ID");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 7. @ParameterizedTest — saving results with various score values
    //    all persist correctly with their exact score
    // ─────────────────────────────────────────────────────────────────────────

    @ParameterizedTest(name = "score={0}, totalPoints={1}")
    @CsvSource({"0, 100", "50, 100", "100, 100", "30, 50", "10, 10"})
    @DisplayName("saveResult: various score values are persisted correctly")
    void saveResult_variousScoreValues_persistedCorrectly(int score, int totalPoints) {
        Path file = tempDir.resolve("results_param_" + score + ".json");
        ResultRepository repo = new JsonResultRepository(file);

        repo.saveResult(new Result("quizX", new ArrayList<>(), score, totalPoints));

        Result saved = repo.getAllResults().get(0);

        assertEquals(score, saved.getScore(),
                "Score " + score + " should be persisted and retrieved correctly");
        assertEquals(totalPoints, saved.getTotalPoints(),
                "TotalPoints " + totalPoints + " should be persisted and retrieved correctly");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 8. assertThrows — saveResult propagates RuntimeException on bad file path
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("saveResult: throws RuntimeException when the file path is invalid")
    void saveResult_invalidFilePath_throwsRuntimeException() {
        // Use a path inside a non-existent directory to force an IOException
        Path badPath = Path.of("/nonexistent_dir_xyz/results.json");
        ResultRepository repo = new JsonResultRepository(badPath);

        assertThrows(RuntimeException.class,
                () -> repo.saveResult(new Result("quiz1", new ArrayList<>(), 80, 100)),
                "saveResult should throw RuntimeException when it cannot write to the file");
    }
}