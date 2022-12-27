package at.fhtw.sampleapp.service.pack;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.controller.Controller;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.dal.repository.PackageRepository;
import at.fhtw.sampleapp.model.Pckg;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

public class PackageController extends Controller {
    private final PackageRepository packagerepository;

    public PackageController() {
        this.packagerepository = new PackageRepository();
    }
    public Response addPackage(Request request) {
        if(!(request.getCurrentToken().equals("Basic admin-mtcgToken"))) {
            return new Response(
                    HttpStatus.FORBIDDEN,
                    ContentType.JSON,
                    "{ \"message\":\"Failed, you don't have the rights to do that!\" }"
            );
        }
        else if (request.getCurrentToken() == null) {
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.JSON,
                    "{ \"message\": \"Failed, no token was given!\" }"
            );
        }
        UnitOfWork unitOfWork = new UnitOfWork();
        Pckg pckg = new Pckg("Ultimate Booster Pack", 5);
        HttpStatus httpStatus = this.packagerepository.postPckg(pckg, unitOfWork);

        if (httpStatus == HttpStatus.CREATED) {
            unitOfWork.commitTransaction();
        }
        else {
            unitOfWork.rollbackTransaction();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\": \"Something went wrong\" }"
            );
        }
        
        return null;
    }
}
