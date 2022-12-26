package at.fhtw.sampleapp.service.pack;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.controller.Controller;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.dal.repository.Packagerepository;
import at.fhtw.sampleapp.model.Pckg;
import at.fhtw.sampleapp.model.User;

public class PackageController extends Controller {
    private final Packagerepository packagerepository;

    public PackageController() {
        this.packagerepository = new Packagerepository();
    }
    public Response addPackage(Request request) {
        if(request.getCurrentToken() == null) {
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.JSON,
                    "{ \"message\": \"Failed, no token was given!\" }"
            );
        }
        else if(!(request.getCurrentToken().equals("Basic admin-mtcgToken"))) {
            return new Response(
                    HttpStatus.FORBIDDEN,
                    ContentType.JSON,
                    "{ \"message\":\"Failed, you don't have the rights to do that!\" }"
            );
        }
        else if(request.getCurrentToken().equals("Basic admin-mtcgToken")) {
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"message\":\"Success, You are the admin!\" }"
            );
        }
        else {

        }





    return null;
    }
}
