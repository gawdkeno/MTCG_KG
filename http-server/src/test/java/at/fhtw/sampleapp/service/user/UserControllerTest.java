package at.fhtw.sampleapp.service.user;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class UserControllerTest {
    @Test
    void testRegisterExistingUser() {
        Assertions.assertThrows(IOException.class, () -> {
            URL url = new URL("http://localhost:10001/users");
            URLConnection urlConnection = url.openConnection();
            urlConnection.setDoOutput(true);
            OutputStream outputStream = urlConnection.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream);
            printWriter.write("{\"Username\": \"admin\", \r\n \"Password\":\"istrator\"}");
            printWriter.close();
            InputStream inputStream = urlConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        });
    }
    @Test
    void testGetAdminUser() throws IOException {
        URL url = new URL("http://localhost:10001/users/admin");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Authorization", "Basic admin-mtcgToken");
        int responseCode = urlConnection.getResponseCode();

        if(responseCode == HttpURLConnection.HTTP_OK){
            InputStream inputStream = urlConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String userInfo = bufferedReader.readLine();
            System.out.println(userInfo);
            Assertions.assertTrue(true);
        }else {
            Assertions.assertFalse(true);
        }
    }
    @Test
    void testGetNonExistingUser() throws IOException {
        URL url = new URL("http://localhost:10001/users/thisUserDoesntExist");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setRequestProperty("Authorization", "Basic admin-mtcgToken");
        int httpCode = urlConnection.getResponseCode();

        if(httpCode == HttpURLConnection.HTTP_OK) {
            Assertions.fail();
        }
        Assertions.assertTrue(true);
    }
    @Test
    void testUserServiceGetUserWithoutAuthorization() throws IOException {
        URL url = new URL("http://localhost:10001/users/admin");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        int responseCode = urlConnection.getResponseCode();
        Assertions.assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED, responseCode);
    }
}
