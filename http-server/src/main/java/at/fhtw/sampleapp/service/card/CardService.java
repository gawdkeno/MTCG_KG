package at.fhtw.sampleapp.service.card;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;

import java.lang.module.ResolutionException;

public class CardService implements Service {
    private final CardController cardController;

    public CardService(){
        this.cardController =new CardController();
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.GET) {
            return this.cardController.showCards(request);
        }
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
