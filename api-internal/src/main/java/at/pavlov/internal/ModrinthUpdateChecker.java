package at.pavlov.internal;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ModrinthUpdateChecker {
    private static final String API_URL = "https://api.modrinth.com/v2/project/cannons-revamped/version";
    private static final String DOWNLOAD = "https://modrinth.com/plugin/cannons-revamped/version/";

    private final Logger logger;
    private @Nullable String downloadUrl = null;

    public static final String YELLOW = "\u001B[33m";
    public static final String GREEN = "\u001B[32m";
    public static final String RESET = "\u001B[0m";

    public ModrinthUpdateChecker(Logger logger) {
        this.logger = logger;
    }

    public @Nullable Boolean isLatest(String current) {
        String response;

        try(BufferedReader in = getBufferedReader()) {
            response = in.lines().collect(Collectors.joining());
        } catch (IOException e) {
            logger.severe("Failed to check for latest version.");
            return null;
        }

        Gson gson = new Gson();
        JsonArray versions = gson.fromJson(response, JsonArray.class);

        if (versions.isEmpty()) {
            logger.severe("No versions found for this project.");
            return null;
        }

        JsonObject latestVersion = extractLatest(versions);
        String latestVersionNumber = latestVersion.get("version_number").getAsString();
        logger.info(() -> GREEN + "Latest version: " + latestVersionNumber + RESET);

        // Compare versions
        if (latestVersionNumber.equals(current)) {
            logger.info(() -> GREEN + "You are using the latest version" + RESET);
            return true;
        }

        downloadUrl = DOWNLOAD + latestVersionNumber;
        logger.info(() -> YELLOW + "A new version is available: " + current + " --> " + latestVersionNumber + RESET);
        logger.info(() -> YELLOW + "Download it here: " + downloadUrl + RESET);
        return false;
    }

    public @NotNull String getDownloadUrl() {
        return downloadUrl==null ? "ERROR" : downloadUrl;
    }

    private static @NotNull BufferedReader getBufferedReader() throws IOException {
        URL url = new URL(API_URL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");

        int responseCode = connection.getResponseCode();
        if (responseCode != 200) {
            throw new RuntimeException("Failed to fetch data from Modrinth API: HTTP " + responseCode);
        }

        // Read the response
        return new BufferedReader(new InputStreamReader(connection.getInputStream()));
    }

    private static JsonObject extractLatest(@NotNull JsonArray versions) {
        return versions.get(0).getAsJsonObject();
    }
}
