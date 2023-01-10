package at.fhtw.sampleapp.service.session;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.controller.Controller;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.dal.repository.SessionRepository;
import at.fhtw.sampleapp.model.User;

public class SessionController extends Controller {

    private final SessionRepository sessionRepository;
    public SessionController() {
        this.sessionRepository = new SessionRepository();
    }
    public Response loginUser(Request request) {
        try {
            User user = this.getObjectMapper().readValue(request.getBody(), User.class);
            UnitOfWork unitOfWork = new UnitOfWork();
            HttpStatus httpStatus = this.sessionRepository.loginUser(user, unitOfWork);

            switch (httpStatus) {
                case OK -> {
                    unitOfWork.commitTransaction();
                    return new Response(
                            HttpStatus.OK,
                            ContentType.JSON,
                            "{ \"message\": \"Success\" }"
                    );
                }
                case UNAUTHORIZED -> {
                    unitOfWork.rollbackTransaction();
                    return new Response(
                            HttpStatus.UNAUTHORIZED,
                            ContentType.JSON,
                            "{ \"message\": \"Failed, invalid username or password\" }"
                    );
                }
                default -> {
                    unitOfWork.rollbackTransaction();
                    return new Response(
                            HttpStatus.INTERNAL_SERVER_ERROR,
                            ContentType.JSON,
                            "{ \"message\": \"Failed, something went wrong\" }"
                    );
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
