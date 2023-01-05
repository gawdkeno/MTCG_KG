package at.fhtw.sampleapp.service.score;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.controller.Controller;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.dal.repository.UserRepository;
import at.fhtw.sampleapp.model.User;

import java.util.Collection;

public class ScoreController extends Controller {
    private final UserRepository userRepository;

    public ScoreController() {
        this.userRepository = new UserRepository();
    }

    public Response showScore(Request request) {
        UnitOfWork unitOfWork = new UnitOfWork();
        String currentToken = request.getCurrentToken();
        if (!(userRepository.userExists(currentToken, unitOfWork))) {
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.JSON,
                    "{ \"message\": \"Failed, invalid or no token was given\" }"
            );
        }
        try (unitOfWork){
            Collection<User> userScore = this.userRepository.getUserScore(unitOfWork);
            String userScoreJSON = this.getObjectMapper().writeValueAsString(userScore);

            unitOfWork.commitTransaction();
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    (userScoreJSON + "\n")
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
}
