package at.fhtw.sampleapp.service.user;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.http.Method;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.httpserver.server.Service;

public class UserService implements Service {
    private final UserController userController;

    public UserService() {
        this.userController = new UserController();
    }

    @Override
    public Response handleRequest(Request request) {
//        if (request.getMethod() == Method.GET &&
//                request.getPathParts().size() > 1) {
//            //return this.userController.getWeather(request.getPathParts().get(1));
//        } else if (request.getMethod() == Method.GET) {
//            //return this.weatherController.getWeather();
//            return this.userController.getWeatherPerRepository(); }
         if (request.getMethod() == Method.POST) {
            return this.userController.postUser(request);
        }
        return new Response(
                HttpStatus.BAD_REQUEST,
                ContentType.JSON,
                "[]"
        );
    }
}
