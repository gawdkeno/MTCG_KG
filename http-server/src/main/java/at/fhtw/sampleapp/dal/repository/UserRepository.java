package at.fhtw.sampleapp.dal.repository;

import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.dal.DataAccessException;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.model.User;
import at.fhtw.sampleapp.model.Weather;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class UserRepository {
//    private UnitOfWork unitOfWork;

//    public UserRepository(UnitOfWork unitOfWork)
//    {
//        this.unitOfWork = unitOfWork;
//    }

    public UserRepository() {

    }

    public HttpStatus postUser(User user, UnitOfWork unitOfWork) {
        // CHECK IF USER ALREADY EXISTS
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    SELECT * FROM player
                """)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next())
            {
                String checkUsername = resultSet.getString("player_username");
                if(checkUsername.equals(user.getPlayer_username()))
                {
                    return HttpStatus.CONFLICT;
                }
            }
        } catch (SQLException e) {
            System.err.println("postUser() doesn't work");
            throw new DataAccessException("INSERT NICHT ERFOLGREICH", e);
        }
        // REGISTER USER
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    INSERT INTO player VALUES (DEFAULT, ?,?,?,?,?,?,?) RETURNING player_id
                """))
        {
            preparedStatement.setString(1, user.getPlayer_username());
            preparedStatement.setString(2, user.getPlayer_password());
            preparedStatement.setInt(3, user.getPlayer_coins());
            preparedStatement.setString(4, user.getPlayer_token());
            preparedStatement.setString(5, user.getPlayer_bio());
            preparedStatement.setString(6, user.getPlayer_image());
            preparedStatement.setString(7, user.getPlayer_name());

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
            {
                user.setPlayer_id(resultSet.getInt(1));
            }

            return HttpStatus.CREATED;

        } catch (SQLException e) {
            System.err.println("postUser() doesn't work");
            throw new DataAccessException("INSERT NICHT ERFOLGREICH", e);
        }

    }
}
