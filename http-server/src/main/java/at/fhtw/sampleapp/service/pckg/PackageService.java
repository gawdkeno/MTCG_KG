package at.fhtw.sampleapp.service.pckg;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;

public class PackageService implements Service {

    private final PackageController packageController;
    private final TransactionController transactionController;

    public PackageService() {
        this.packageController = new PackageController();
        this.transactionController = new TransactionController();
    }

    @Override
    public Response handleRequest(Request request) {
        if (request.getMethod() == Method.POST) {
            if (request.getServiceRoute().equals("/packages")) {
                return this.packageController.addPackage(request);
            } else if (request.getServiceRoute().equals("/transactions")) {
                return this.transactionController.buyPackage(request);
            }
        }
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
