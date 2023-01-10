package at.fhtw.sampleapp.service.stats;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class StatsControllerTest {
    @Test
    void testScoreboardNoAuth() throws IOException {
        URL url = new URL("http://localhost:10001/score");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        int responseCode = urlConnection.getResponseCode();

        if(responseCode == HttpURLConnection.HTTP_UNAUTHORIZED){
            Assertions.assertTrue(true);
        }else {
            Assertions.fail();
        }
    }
    @Test
    void testScoreboardWrongToken() throws IOException {
        URL url = new URL("http://localhost:10001/score");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Authorization", "Wrong Token");
        int responseCode = urlConnection.getResponseCode();

        if(responseCode == HttpURLConnection.HTTP_UNAUTHORIZED){
            Assertions.assertTrue(true);
        }else {
            Assertions.fail();
        }
    }
}
