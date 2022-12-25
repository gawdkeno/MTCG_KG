package at.fhtw.sampleapp.dal.repository;

import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.sampleapp.dal.DataAccessException;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SessionRepository {
    public SessionRepository() {

    }
    public HttpStatus loginUser(User user, UnitOfWork unitOfWork) {
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                        SELECT * FROM player WHERE player_username LIKE (?)
                    """)) {
            preparedStatement.setString(1, user.getPlayer_username());
            ResultSet resultSet = preparedStatement.executeQuery();
            String usernameCounter;
            String passwordCounter;
            if (resultSet.next())
            {
                usernameCounter = resultSet.getString("player_username");
                passwordCounter = resultSet.getString("player_password");
                if(usernameCounter.equals(user.getPlayer_username()) && passwordCounter.equals(user.getPlayer_password()))
                {
                    return HttpStatus.OK;
                }
            }
        } catch (SQLException e) {
            System.err.println("loginUser() doesn't work");
            throw new DataAccessException("LOGIN NICHT ERFOLGREICH", e);
        }
        return HttpStatus.UNAUTHORIZED;
    }

}
