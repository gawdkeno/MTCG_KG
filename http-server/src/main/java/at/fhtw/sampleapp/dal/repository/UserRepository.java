package at.fhtw.sampleapp.dal.repository;

import at.fhtw.sampleapp.dal.DataAccessException;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.model.Weather;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class UserRepository {
    private UnitOfWork unitOfWork;

    public UserRepository(UnitOfWork unitOfWork)
    {
        this.unitOfWork = unitOfWork;
    }

    public void postUser(String username, String userPassword) {
        try (PreparedStatement preparedStatement =
                     this.unitOfWork.prepareStatement("""
                    INSERT INTO users VALUES (DEFAULT, ?,?)
                """))
        {
            ResultSet resultSet = preparedStatement.executeQuery();
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, userPassword);
            preparedStatement.execute();
            unitOfWork.commitTransaction();
        } catch (SQLException e) {
            System.err.println("postUser() geht nicht");
            throw new DataAccessException("INSERT NICHT ERFOLGREICH", e);
        }
    }
}
