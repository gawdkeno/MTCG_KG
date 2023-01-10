package at.fhtw.sampleapp.service.score;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ScoreControllerTest {
    @Test
    void testGetScoreboard() throws IOException {
        URL url = new URL("http://localhost:10001/score");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Authorization", "Basic kienboec-mtcgToken");
        int responseCode = urlConnection.getResponseCode();

        if(responseCode == HttpURLConnection.HTTP_OK){
            InputStream inputStream = urlConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                System.out.println(inputLine);
            }
        }else {
            Assertions.fail();
        }
    }
    @Test
    void testGetScoreboardNoAuth() throws IOException {
        URL url = new URL("http://localhost:10001/score");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        int responseCode = urlConnection.getResponseCode();

        if(responseCode == HttpURLConnection.HTTP_OK){
            Assertions.fail();
        }else {
            Assertions.assertTrue(true);
        }
    }
}
