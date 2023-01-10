package at.fhtw.sampleapp.service.session;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

public class SessionControllerTest {
    @Test
    void testLogin() throws IOException {
        URL url = new URL("http://localhost:10001/sessions");
        URLConnection urlConnection = url.openConnection();
        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();
        PrintWriter printWriter = new PrintWriter(outputStream);
        printWriter.write("{\"Username\": \"admin\", \r\n \"Password\":\"istrator\"}");
        printWriter.close();
        InputStream inputStream = urlConnection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        Assertions.assertEquals("{ \"message\": \"Success\" }", bufferedReader.readLine());
    }
    @Test
    void testLoginWrongPassword() {
        Assertions.assertThrows(IOException.class, () -> {
            URL url = new URL("http://localhost:10001/sessions");
            URLConnection urlConnection = url.openConnection();
            urlConnection.setDoOutput(true);
            OutputStream outputStream = urlConnection.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream);
            printWriter.write("{\"Username\": \"admin\", \r\n \"Password\":\"yeehaaw\"}");
            printWriter.close();
            InputStream inputStream = urlConnection.getInputStream();
        });
    }
    @Test
    void testLoginWrongUsername() {
        Assertions.assertThrows(IOException.class, () -> {
            URL url = new URL("http://localhost:10001/sessions");
            URLConnection urlConnection = url.openConnection();
            urlConnection.setDoOutput(true);
            OutputStream outputStream = urlConnection.getOutputStream();
            PrintWriter printWriter = new PrintWriter(outputStream);
            printWriter.write("{\"Username\": \"Admin\", \r\n \"Password\":\"istrator\"}");
            printWriter.close();
            InputStream inputStream = urlConnection.getInputStream();
        });
    }
}
