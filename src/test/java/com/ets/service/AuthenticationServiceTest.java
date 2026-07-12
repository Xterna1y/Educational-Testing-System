package com.ets.service;

import com.ets.model.User;
import com.ets.repo.UserRepository;
import com.ets.services.AuthenticationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link AuthenticationService}.
 *
 * <p>Each test builds an {@link AuthenticationService} backed by a
 * {@link UserRepository} that reads from an isolated temporary JSON file,
 * so the real {@code users.json} resource is never touched.</p>
 */
class AuthenticationServiceTest {

    /** JUnit 5 injects a fresh temp directory for every test method. */
    @TempDir
    Path tempDir;

    /** Path to the temporary users.json written before each test. */
    private Path tempJson;

    /** The service under test, wired with the temp-file repository. */
    private AuthenticationService authService;

    /** Initial JSON content seeded before each test. */
    private static final String SEED_JSON =
            "[\n" +
                    "  {\n" +
                    "    \"username\": \"student01\",\n" +
                    "    \"role\": \"STUDENT\"\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"username\": \"student02\",\n" +
                    "    \"role\": \"STUDENT\"\n" +
                    "  }\n" +
                    "]";

    @BeforeEach
    void setUp() throws IOException {
        tempJson    = tempDir.resolve("users.json");
        Files.write(tempJson, SEED_JSON.getBytes(StandardCharsets.UTF_8));
        // Wire AuthenticationService with a UserRepository backed by the temp file
        authService = new AuthenticationService(new UserRepository(tempJson));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 1. Successful login — username exists in the JSON file
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Login: existing username returns the correct User")
    void login_existingUsername_returnsUser() {
        User user = authService.authenticate("student01");

        assertNotNull(user, "User should be found for a valid username");
        assertEquals("student01", user.getUsername());
        assertEquals("STUDENT",   user.getRole());
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 2. Login with a username that is NOT in the JSON file
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Login: unknown username returns null")
    void login_unknownUsername_returnsNull() {
        User user = authService.authenticate("ghost_user");

        assertNull(user, "Unknown username should return null");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. Register with a username that already exists → duplicate rejected
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Register: duplicate username returns null")
    void register_duplicateUsername_returnsNull() throws IOException {
        User result = authService.registerUser("student01");   // already exists

        assertNull(result, "Registering a duplicate username should return null");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 4. Login with an empty / blank username
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Login: empty username returns null")
    void login_emptyUsername_returnsNull() {
        // AuthenticationService trims and rejects blank input before hitting the repo
        assertNull(authService.authenticate(""),
                "Empty username should return null");
        assertNull(authService.authenticate("   "),
                "Blank username (spaces only) should return null");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 5. Register with an empty / blank username
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Register: empty username returns null")
    void register_emptyUsername_returnsNull() throws IOException {
        // AuthenticationService guards blank input and returns null immediately
        assertNull(authService.registerUser(""),
                "Registering an empty username should return null");
        assertNull(authService.registerUser("   "),
                "Registering a blank (spaces-only) username should return null");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 6. Successful register — new user is saved and immediately authenticatable
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Register: new username is created and can be logged in afterwards")
    void register_newUsername_canLoginAfterwards() throws IOException {
        User created = authService.registerUser("newStudent");

        assertNotNull(created, "A brand-new username should be created successfully");
        assertEquals("newStudent", created.getUsername());
        assertEquals("STUDENT",    created.getRole());

        // The same service instance should now authenticate the new user
        User found = authService.authenticate("newStudent");
        assertNotNull(found, "Newly registered user must be authenticatable in the same session");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 7. Register persists to disk — a fresh service can authenticate the user
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Register: persisted user can be authenticated by a fresh service instance")
    void register_persistsToDisk_freshServiceCanAuthenticate() throws IOException {
        authService.registerUser("persistedUser");

        // Build a completely new service backed by a fresh repo reading the same temp file
        AuthenticationService freshService =
                new AuthenticationService(new UserRepository(tempJson));

        User found = freshService.authenticate("persistedUser");
        assertNotNull(found, "Registered user must survive a service restart (persist to disk)");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 8. authenticate() is case-insensitive
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Login: authentication is case-insensitive")
    void login_caseInsensitive() {
        assertNotNull(authService.authenticate("STUDENT01"), "Upper-case lookup should succeed");
        assertNotNull(authService.authenticate("Student01"), "Mixed-case lookup should succeed");
        assertNotNull(authService.authenticate("student01"), "Lower-case lookup should succeed");
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 9. registerUser() duplicate detection is case-insensitive
    // ─────────────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Register: duplicate detection is case-insensitive")
    void register_duplicateCaseInsensitive_returnsNull() throws IOException {
        assertNull(authService.registerUser("STUDENT01"),
                "Upper-case variant of existing username should be treated as duplicate");
        assertNull(authService.registerUser("Student02"),
                "Mixed-case variant of existing username should be treated as duplicate");
    }
}