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

import java.util.Collection;
import java.util.List;


public class UserController extends Controller {
    private final UserRepository userRepository;

    public UserController() {
        this.userRepository = new UserRepository();
    }

    public Response addUser(Request request) {
        UnitOfWork unitOfWork = new UnitOfWork();
        try {
            User user = this.getObjectMapper().readValue(request.getBody(), User.class);
            user.setPlayer_coins(20);
            user.setPlayer_image(":-)");
            user.setPlayer_bio("MTCG Player");
            user.setPlayer_name("first name, last name");
            user.setPlayer_token("Basic "  + user.getPlayer_username() + "-mtcgToken");

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

    public Response showUserData(Request request) {
        UnitOfWork unitOfWork = new UnitOfWork();
        String username = getAuthorizedUsername(request, unitOfWork);
        if (username == null) {
            return new Response(
                    HttpStatus.FORBIDDEN,
                    ContentType.JSON,
                    "{ \"message\": \"Failed, you are not allowed to do that\" }"
            );
        }
        try (unitOfWork){
            Collection<User> userData = this.userRepository.getUserData(username, unitOfWork);
            if (userData == null) {
                unitOfWork.rollbackTransaction();
                return new Response(
                        HttpStatus.NOT_FOUND,
                        ContentType.JSON,
                        "{ \"message\" : \"Failed, this user doesn't exist\" }"
                );
            }
            String userDataJSON = this.getObjectMapper().writeValueAsString(userData);

            unitOfWork.commitTransaction();
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    (userDataJSON + "\n")
            );
        } catch (Exception e) {
            e.printStackTrace();

            unitOfWork.rollbackTransaction();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }

    private String getAuthorizedUsername(Request request, UnitOfWork unitOfWork) {
        String currentToken = request.getCurrentToken();
        if (currentToken == null) {
            return null;
        }
        String username = this.userRepository.getPlayerUserName(currentToken, unitOfWork);
        List<String> pathParts = request.getPathParts();
        String pathUsername = pathParts.get(1);

        if (!(username.equals(pathUsername))) {
            return null;
        }
        return username;
    }

    public Response editUserData(Request request) {
        UnitOfWork unitOfWork = new UnitOfWork();
        String username = getAuthorizedUsername(request, unitOfWork);
        if (username == null) {
            unitOfWork.rollbackTransaction();
            return new Response(
                    HttpStatus.FORBIDDEN,
                    ContentType.JSON,
                    "{ \"message\": \"Failed, you are not allowed to do that\" }"
            );
        }
        try {
            User user = this.getObjectMapper().readValue(request.getBody(), User.class);
            user.setPlayer_username(username);
            // TODO: maybe check if it was successful (probably better but idk)
            this.userRepository.updateUserData(user, unitOfWork);
            unitOfWork.commitTransaction();
            return new Response (
                    HttpStatus.OK,
                    ContentType.JSON,
                    "{ \"message\": \"Success, user data updated\" }"
            );
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
