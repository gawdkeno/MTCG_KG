package at.fhtw.sampleapp.dal;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public enum DatabaseManager {
    INSTANCE;

    public Connection getConnection()
    {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "swe1user";
        String pw = "swe1pw";

        try {
            return DriverManager.getConnection(
                    url,
                    user,
                    pw);
        } catch (SQLException e) {
            throw new DataAccessException("Couldn't connect to database", e);
        }
    }
}
