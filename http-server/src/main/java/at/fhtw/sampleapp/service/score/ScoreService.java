package at.fhtw.sampleapp.service.score;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;

public class ScoreService implements Service {
    private final ScoreController scoreController;

    public ScoreService() {
        this.scoreController = new ScoreController();
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.GET) {
            return this.scoreController.showScore(request);
        }
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
