package at.fhtw.sampleapp.service.card;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CardTest {
    @Test
    void testShowCardsWithFalseAuthToken() throws IOException {
        URL url = new URL("http://localhost:10001/cards");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Authorization", "This token doesn't exist");

        int httpCode = urlConnection.getResponseCode();
        assertEquals(HttpURLConnection.HTTP_BAD_REQUEST, httpCode);
    }
    @Test
    void testShowCardsNoAuth() throws IOException {
        URL url = new URL("http://localhost:10001/cards");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");

        int httpCode = urlConnection.getResponseCode();
        assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED, httpCode);
    }
}
