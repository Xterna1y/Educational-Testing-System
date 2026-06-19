package com.ets.repo;

import com.ets.model.Admin;
import com.ets.model.Student;
import com.ets.model.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads and stores User records from the users.json resource file.
 * Parses JSON manually to avoid adding a JSON library dependency.
 */
public class UserRepository {

    private final List<User> users = new ArrayList<>();

    public UserRepository() {
        loadUsers();
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
     * Returns all loaded users (defensive copy).
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
}
