package at.fhtw.sampleapp.service.deck;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;

public class DeckService implements Service {
    private final DeckController deckController;

    public DeckService() {
        this.deckController = new DeckController();
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.GET) {
            return this.deckController.showDeck(request);
        } else if (request.getMethod() == Method.PUT) {
            return this.deckController.configureDeck(request);
        }
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
