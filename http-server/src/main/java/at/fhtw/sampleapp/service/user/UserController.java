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
    private UserRepository userRepository;

    public UserController() {
        this.userRepository = new UserRepository();
    }

    public Response addUser(Request request) {

        try {
            User user = this.getObjectMapper().readValue(request.getBody(), User.class);
            UnitOfWork unitOfWork = new UnitOfWork();
            this.userRepository.postUser(user, unitOfWork);
            unitOfWork.commitTransaction();
            return new Response(
                    HttpStatus.CREATED,
                    ContentType.JSON,
                    "{ \"message\": \"Success\" }"
            );
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"message\" : \"Username not available\" }"
            );
        }

    }
}
