package com.ets.repo;

import com.ets.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link UserRepository}.
 *
 * <p>Covers the repository's own behaviour: reading users from a JSON file,
 * finding by username, registering new users, persisting to disk, and
 * returning a defensive copy of the user list.</p>
 *
 * <p>Each test gets its own isolated temp directory so the real
 * {@code users.json} resource file is never touched.</p>
 */
class UserRepositoryTest {

    /** JUnit 5 injects a fresh temp directory for every test method. */
    @TempDir
    Path tempDir;

    /** Path to the temporary users.json written before each test. */
    private Path tempJson;

    /** Initial JSON content seeded before each test. */
    private static final String SEED_JSON =
            "[\n" +
            "  {\n" +
            "    \"username\": \"student01\",\n" +
            "    \"password\": \"\",\n" +
            "    \"role\": \"STUDENT\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"username\": \"admin01\",\n" +
            "    \"password\": \"\",\n" +
            "    \"role\": \"ADMIN\"\n" +
            "  }\n" +
            "]";

    @BeforeEach
    void setUp() throws IOException {
        tempJson = tempDir.resolve("users.json");
        Files.write(tempJson, SEED_JSON.getBytes(StandardCharsets.UTF_8));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 1. findByUsername returns a user for a known username
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findByUsername: known username returns the correct User")
    void findByUsername_knownUsername_returnsUser() {
        UserRepository repo = new UserRepository(tempJson);

        User user = repo.findByUsername("student01");

        assertNotNull(user, "User should be found for a valid username");
        assertEquals("student01", user.getUsername());
        assertEquals("STUDENT",   user.getRole());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. findByUsername returns null for an unknown username
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findByUsername: unknown username returns null")
    void findByUsername_unknownUsername_returnsNull() {
        UserRepository repo = new UserRepository(tempJson);

        assertNull(repo.findByUsername("ghost_user"), "Unknown username should return null");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. findByUsername is case-insensitive
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findByUsername: lookup is case-insensitive")
    void findByUsername_caseInsensitive() {
        UserRepository repo = new UserRepository(tempJson);

        assertNotNull(repo.findByUsername("STUDENT01"), "Upper-case lookup should succeed");
        assertNotNull(repo.findByUsername("Student01"), "Mixed-case lookup should succeed");
        assertNotNull(repo.findByUsername("student01"), "Lower-case lookup should succeed");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 4. registerUser persists a new user, and it can be found afterwards
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("registerUser: new user is stored in-memory and on disk")
    void registerUser_newUsername_persistsAndIsRetrievable() throws IOException {
        UserRepository repo = new UserRepository(tempJson);

        User created = repo.registerUser("newStudent");

        // Correct object is returned
        assertNotNull(created, "A brand-new username should be created successfully");
        assertEquals("newStudent", created.getUsername());
        assertEquals("STUDENT",    created.getRole());

        // Findable in the same in-memory instance
        assertNotNull(repo.findByUsername("newStudent"),
                "Newly registered user must be findable in-memory");

        // Findable by a fresh repo that re-reads the file (persistence check)
        UserRepository freshRepo = new UserRepository(tempJson);
        assertNotNull(freshRepo.findByUsername("newStudent"),
                "Registered user must be persisted to disk and loadable by a new instance");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 5. registerUser rejects a duplicate username
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("registerUser: duplicate username returns null, list size unchanged")
    void registerUser_duplicateUsername_returnsNull() throws IOException {
        UserRepository repo = new UserRepository(tempJson);
        int sizeBefore = repo.getAllUsers().size();

        User result = repo.registerUser("student01");   // already exists

        assertNull(result, "Registering a duplicate username should return null");
        assertEquals(sizeBefore, repo.getAllUsers().size(),
                "User list size should be unchanged after a duplicate attempt");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 6. registerUser duplicate detection is case-insensitive
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("registerUser: duplicate detection is case-insensitive")
    void registerUser_duplicateCaseInsensitive_returnsNull() throws IOException {
        UserRepository repo = new UserRepository(tempJson);

        assertNull(repo.registerUser("STUDENT01"),
                "Upper-case variant of existing username should be treated as duplicate");
        assertNull(repo.registerUser("Admin01"),
                "Mixed-case variant of existing username should be treated as duplicate");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 7. getAllUsers returns all loaded users and is a defensive copy
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("getAllUsers: returns correct count and provides a defensive copy")
    void getAllUsers_returnsDefensiveCopy() {
        UserRepository repo = new UserRepository(tempJson);

        List<User> users = repo.getAllUsers();
        assertEquals(2, users.size(), "Should load exactly 2 users from the seed file");

        // Mutating the returned list must not affect the repository's internal list
        users.clear();
        assertEquals(2, repo.getAllUsers().size(),
                "Internal user list must not be affected by mutations to the returned copy");
    }
}
