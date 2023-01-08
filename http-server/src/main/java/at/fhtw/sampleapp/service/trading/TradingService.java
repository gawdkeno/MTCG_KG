package at.fhtw.sampleapp.service.trading;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;

public class TradingService implements Service {
    private final TradingController tradingController;

    public TradingService() {
        this.tradingController = new TradingController();
    }

    @Override
    public Response handleRequest(Request request) {
        return new Response(
                HttpStatus.NOT_IMPLEMENTED,
                ContentType.JSON,
                "Failed, not implemented yet"
        );
    }
}
