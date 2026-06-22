package com.ets.repo;

import com.ets.model.Admin;
import com.ets.model.Student;
import com.ets.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads and stores User records from the users.json resource file.
 * Parses JSON manually to avoid adding a JSON library dependency.
 * Supports registering new users and persisting them back to disk.
 */
public class UserRepository {

    private final List<User> users = new ArrayList<>();

    /** Resolved path to the on-disk users.json so we can write back to it. */
    private Path usersFilePath;

    public UserRepository() {
        resolveFilePath();
        loadUsers();
    }

    /**
     * Package-private constructor for unit tests.
     * Bypasses classpath resource loading and reads/writes from {@code jsonFile} directly.
     *
     * @param jsonFile path to a temporary users JSON file created by the test
     */
    public UserRepository(Path jsonFile) {
        this.usersFilePath = jsonFile;
        loadUsersFromPath(jsonFile);
    }

    /**
     * Reads and parses users from an explicit file path (used by tests).
     */
    private void loadUsersFromPath(Path path) {
        try {
            if (!Files.exists(path)) return;
            String json = new String(Files.readAllBytes(path), StandardCharsets.UTF_8);
            parseUsersJson(json);
        } catch (IOException e) {
            System.err.println("[UserRepository] Failed to load from path: " + e.getMessage());
        }
    }

    /**
     * Resolves the real on-disk path of users.json so we can write to it.
     * First tries to find the file via the classpath URL, then falls back
     * to a path relative to the working directory.
     */
    private void resolveFilePath() {
        try {
            URL url = getClass().getResource("/users.json");
            if (url != null && "file".equals(url.getProtocol())) {
                usersFilePath = Paths.get(url.toURI());
            }
        } catch (Exception e) {
            System.err.println("[UserRepository] Could not resolve users.json path: " + e.getMessage());
        }
        if (usersFilePath == null) {
            // Fallback: write beside the working directory
            usersFilePath = Paths.get("users.json");
        }
    }

    /**
     * Reads users.json from the classpath and populates the users list.
     */
    private void loadUsers() {
        try (InputStream is = getClass().getResourceAsStream("/users.json")) {
            if (is == null) {
                System.err.println("[UserRepository] users.json not found on classpath.");
                return;
            }
            String json = readStream(is);
            parseUsersJson(json);
        } catch (IOException e) {
            System.err.println("[UserRepository] Failed to load users.json: " + e.getMessage());
        }
    }

    /**
     * Reads an InputStream fully into a String.
     */
    private String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString();
    }

    /**
     * Minimal JSON array parser — handles the flat structure in users.json.
     * Expects: [{"username":"...","password":"...","role":"..."},...]
     */
    private void parseUsersJson(String json) {
        // Remove outer brackets and split by object boundaries
        String stripped = json.trim();
        if (stripped.startsWith("[")) stripped = stripped.substring(1);
        if (stripped.endsWith("]")) stripped = stripped.substring(0, stripped.lastIndexOf("]"));

        // Split objects by "}," pattern
        String[] objects = stripped.split("},\\s*\\{");
        for (String obj : objects) {
            obj = obj.replace("{", "").replace("}", "").trim();
            String username = extractField(obj, "username");
            String password = extractField(obj, "password");
            String role     = extractField(obj, "role");

            if (username != null && password != null && role != null) {
                if ("ADMIN".equalsIgnoreCase(role)) {
                    users.add(new Admin(username, password, role));
                } else {
                    users.add(new Student(username, password, role));
                }
            }
        }
    }

    /**
     * Extracts the value of a JSON string field by key name.
     */
    private String extractField(String obj, String key) {
        String search = "\"" + key + "\"";
        int keyIndex = obj.indexOf(search);
        if (keyIndex < 0) return null;
        int colonIndex = obj.indexOf(":", keyIndex);
        if (colonIndex < 0) return null;
        String rest = obj.substring(colonIndex + 1).trim();
        if (rest.startsWith("\"")) {
            int start = 1;
            int end = rest.indexOf("\"", start);
            if (end > 0) return rest.substring(start, end);
        }
        return null;
    }

    /**
     * Finds a user by username (case-insensitive).
     *
     * @param username the username to search for
     * @return the matching User, or null if not found
     */
    public User findByUsername(String username) {
        for (User u : users) {
            if (u.getUsername().equalsIgnoreCase(username)) {
                return u;
            }
        }
        return null;
    }

    /**
     * Registers a new STUDENT user and persists the updated list to users.json.
     *
     * @param username desired username
     * @return the newly created {@link Student}, or {@code null} if the username is already taken
     * @throws IOException if the file cannot be written
     */
    public User registerUser(String username) throws IOException {
        if (findByUsername(username) != null) {
            return null; // duplicate
        }
        Student newUser = new Student(username, "", "STUDENT");
        users.add(newUser);
        persistUsers();
        return newUser;
    }

    /**
     * Writes the current in-memory user list back to the on-disk users.json.
     */
    private void persistUsers() throws IOException {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            sb.append("  {\n");
            sb.append("    \"username\": \"").append(escape(u.getUsername())).append("\",\n");
            sb.append("    \"password\": \"").append(escape(u.getPassword())).append("\",\n");
            sb.append("    \"role\": \"").append(escape(u.getRole())).append("\"\n");
            sb.append("  }");
            if (i < users.size() - 1) sb.append(",");
            sb.append("\n");
        }
        sb.append("]");
        Files.write(usersFilePath, sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    /** Escapes special characters inside a JSON string value. */
    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    /**
     * Returns all loaded users (defensive copy).
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
}
