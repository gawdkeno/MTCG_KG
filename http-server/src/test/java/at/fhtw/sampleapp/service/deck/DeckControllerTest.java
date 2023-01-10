package at.fhtw.sampleapp.service.deck;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DeckControllerTest {
    @Test
    void testShowDeckNoAuth() throws IOException {
        URL url = new URL("http://localhost:10001/deck");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");

        int httpCode = urlConnection.getResponseCode();
        assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED, httpCode);
    }
    @Test
    void testShowDeckWrongAuth() throws IOException {
        URL url = new URL("http://localhost:10001/deck");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Authorization", "This token doesn't exist");

        int httpCode = urlConnection.getResponseCode();
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, httpCode);
    }
    @Test
    void testConfigureDeckCardNotAvailable() throws IOException {
        URL url = new URL("http://localhost:10001/deck");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Authorization", "Basic admin-mtcgToken");
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("PUT");
        OutputStream outputStream = urlConnection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write("[\"845f0dc7-37d0-426e-994e-43fc3ac83c08\", \"99f8f8dc-e25e-4a95-aa2c-782823f36e2a\", \"e85e3976-7c86-4d06-9a80-641c2019a79f\", \"171f6076-4eb5-4a7d-b3f2-2d650cc3d237\"]");
        printWriter.close();

        int httpCode = urlConnection.getResponseCode();

        assertEquals(HttpURLConnection.HTTP_FORBIDDEN, httpCode);
    }
    @Test
    void testConfigureDeckOnly3Cards() throws IOException {
        URL url = new URL("http://localhost:10001/deck");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Authorization", "Basic admin-mtcgToken");
        urlConnection.setDoOutput(true);
        urlConnection.setRequestMethod("PUT");
        OutputStream outputStream = urlConnection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write("[\"845f0dc7-37d0-426e-994e-43fc3ac83c08\", \"99f8f8dc-e25e-4a95-aa2c-782823f36e2a\", \"e85e3976-7c86-4d06-9a80-641c2019a79f\"]");
        printWriter.close();

        int httpCode = urlConnection.getResponseCode();

        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, httpCode);
    }
}
