package at.fhtw.sampleapp.service.user;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.controller.Controller;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.dal.repository.UserRepository;
import at.fhtw.sampleapp.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;


public class UserController extends Controller {
    private final UserRepository userRepository;

    public UserController() {
        this.userRepository = new UserRepository();
    }

    public Response addUser(Request request) {
        UnitOfWork unitOfWork = null;
        try {
            User user = this.getObjectMapper().readValue(request.getBody(), User.class);
            user.setPlayer_coins(20);
            user.setPlayer_image(":-)");
            user.setPlayer_bio("MTCG Player");
            user.setPlayer_name("Sur/Prename");
            user.setPlayer_token("Basic "  + user.getPlayer_username() + "-mtcgToken");

            unitOfWork = new UnitOfWork();
            HttpStatus httpStatus = this.userRepository.postUser(user, unitOfWork);

            switch (httpStatus) {
                case CREATED -> {
                    unitOfWork.commitTransaction();
                    return new Response(
                            HttpStatus.CREATED,
                            ContentType.JSON,
                            "{ \"message\": \"Success\" }"
                    );
                }
                case CONFLICT -> {
                    unitOfWork.rollbackTransaction();
                    return new Response(
                            HttpStatus.CONFLICT,
                            ContentType.JSON,
                            "{ \"message\": \"Failed, user already exists\" }"
                    );
                }
                default -> {
                    unitOfWork.rollbackTransaction();
                    return new Response(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            ContentType.JSON,
                            "{ \"message\": \"Something went wrong\" }"
                    );
                }
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
            unitOfWork.rollbackTransaction();
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"message\" : \"Username not available\" }"
            );
        }

    }
}
