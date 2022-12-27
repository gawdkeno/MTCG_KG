package at.fhtw.sampleapp.service.pack;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.controller.Controller;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.dal.repository.PackageRepository;
import at.fhtw.sampleapp.model.Card;
import at.fhtw.sampleapp.model.Pckg;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PackageController extends Controller {
    private final PackageRepository packagerepository;

    public PackageController() {
        this.packagerepository = new PackageRepository();
    }
    public Response addPackage(Request request) {
        // CHECK IF (ADMIN-)TOKEN WAS GIVEN
        if(!(request.getCurrentToken().equals("Basic admin-mtcgToken"))) {
            return new Response(
                    HttpStatus.FORBIDDEN,
                    ContentType.JSON,
                    "{ \"message\":\"Failed, you don't have the rights to do that\" }"
            );
        }
        else if (request.getCurrentToken() == null) {
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.JSON,
                    "{ \"message\": \"Failed, no token was given\" }"
            );
        }
        UnitOfWork unitOfWork = new UnitOfWork();

        // CREATE PACKAGE (NO CARDS YET)
        Pckg pckg = new Pckg("Ultimate Booster Pack", 5);
        pckg.setPackage_id(this.packagerepository.postPckg(pckg, unitOfWork));

        if (pckg.getPackage_id() > 0) {
            // TODO: wait for commitTransaction() until cards were also added to the pack,
            //  maybe create 2 unitOfWork's and committing/rollback both at the end
            //  GEHT VIELLEICHT NICHT WEIL ICH PACKAGE ID BRAUCHE
            unitOfWork.commitTransaction();

            // CREATE CARDS IN PACK
            unitOfWork = new UnitOfWork();
            try {
                Card[] cards = this.getObjectMapper().readValue(request.getBody(), Card[].class);
                for (Card card : cards) {
                    // set element
                    if(card.getCard_name().contains("Fire")) {
                        card.setCard_element("fire");
                    } else if (card.getCard_name().contains("Water")) {
                        card.setCard_element("water");
                    } else {
                        card.setCard_element("normal");
                    }
                    // set type
                    if(card.getCard_name().contains("Spell")) {
                        card.setCard_type("spell");
                    } else {
                        card.setCard_type("monster");
                    }
                    // set package_id
                    card.setCard_in_package(pckg.getPackage_id());

                    HttpStatus httpStatus = this.packagerepository.postCard(card, unitOfWork);
                    if(httpStatus != HttpStatus.CREATED) {
                        return new Response(
                                HttpStatus.CONFLICT,
                                ContentType.JSON,
                                "{ \"message\":\"Failed, couldn't add all cards to package\" }"
                        );
                    }
                }
            } catch (JsonProcessingException e) {
                unitOfWork.rollbackTransaction();
                return new Response(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        ContentType.JSON,
                        "{ \"message\": \"Something went wrong\" }"
                );
            }
            unitOfWork.commitTransaction();
            return new Response(
                    HttpStatus.CREATED,
                    ContentType.JSON,
                    "{ \"message\":\"Success, pack and cards created\" }"
            );
        }
        unitOfWork.rollbackTransaction();
        return new Response(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ContentType.JSON,
                "{ \"message\": \"Failed, couldn't create (empty) package\" }"
        );
    }
}
