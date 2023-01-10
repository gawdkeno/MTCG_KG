package at.fhtw.sampleapp.service.pckg;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TransactionControllerTest {
    @Test
    void testBuyPackageNoAuth() throws IOException {
        URL url = new URL("http://localhost:10001/packages");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setDoOutput(true);
        OutputStream outputStream = urlConnection.getOutputStream();

        int responseCode = urlConnection.getResponseCode();

        assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED, responseCode);
    }
}
