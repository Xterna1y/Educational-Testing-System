package com.ets.repo;

import com.ets.model.Difficulty;
import com.ets.model.Question;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link CachedQuestionRepository}.
 */
class CachedQuestionRepositoryTest {

    @TempDir
    Path tempDir;

    private Question makeQuestion(String id) {
        return new Question(
                id,
                "Science",
                "What is 2 + 2?",
                Arrays.asList("3", "4", "5", "6"),
                1,
                Difficulty.EASY
        );
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 1. loadQuestions returns empty list when file does not exist
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("loadQuestions: non-existent file returns empty list")
    void loadQuestions_fileDoesNotExist_returnsEmptyList() {
        Path file = tempDir.resolve("nonexistent.json");
        CachedQuestionRepository repo = new CachedQuestionRepository(file);

        List<Question> result = repo.loadQuestions();

        assertNotNull(result, "loadQuestions should never return null");
        assertTrue(result.isEmpty(), "loadQuestions should return empty list when file does not exist");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. saveQuestions then loadQuestions returns the same questions
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("saveQuestions then loadQuestions: persisted questions are retrievable")
    void saveQuestions_validList_loadQuestionsReturnsSameQuestions() {
        Path file = tempDir.resolve("cache.json");
        CachedQuestionRepository repo = new CachedQuestionRepository(file);

        List<Question> toSave = Arrays.asList(makeQuestion("q1"), makeQuestion("q2"));
        repo.saveQuestions(toSave);

        List<Question> loaded = repo.loadQuestions();

        assertEquals(2, loaded.size(), "Should load exactly the 2 questions that were saved");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. saveQuestions overwrites previous data rather than appending
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("saveQuestions: second save overwrites first, not appends")
    void saveQuestions_calledTwice_overwritesPreviousData() {
        Path file = tempDir.resolve("cache.json");
        CachedQuestionRepository repo = new CachedQuestionRepository(file);

        repo.saveQuestions(Arrays.asList(makeQuestion("q1"), makeQuestion("q2")));
        repo.saveQuestions(Arrays.asList(makeQuestion("q3")));

        List<Question> loaded = repo.loadQuestions();

        assertEquals(1, loaded.size(),
                "Second save should overwrite the first — only 1 question should remain");
        assertEquals("q3", loaded.get(0).getId(), "Remaining question should be 'q3'");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 4. loadQuestions returns empty list for an empty file
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("loadQuestions: empty file returns empty list without throwing")
    void loadQuestions_emptyFile_returnsEmptyList() throws Exception {
        Path file = tempDir.resolve("empty.json");
        java.nio.file.Files.writeString(file, "");
        CachedQuestionRepository repo = new CachedQuestionRepository(file);

        List<Question> result = repo.loadQuestions();

        assertNotNull(result);
        assertTrue(result.isEmpty(), "An empty cache file should produce an empty list");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 5. assertThrows — saveQuestions throws RuntimeException on invalid path
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("saveQuestions: invalid file path throws RuntimeException")
    void saveQuestions_invalidFilePath_throwsRuntimeException() {
        Path badPath = Path.of("/nonexistent_dir_xyz/cache.json");
        CachedQuestionRepository repo = new CachedQuestionRepository(badPath);

        assertThrows(RuntimeException.class,
                () -> repo.saveQuestions(List.of(makeQuestion("q1"))),
                "saveQuestions should throw RuntimeException when the path is not writable");
    }
}