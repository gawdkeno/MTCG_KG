package at.fhtw.sampleapp.dal.repository;

import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.sampleapp.dal.DataAccessException;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.model.Pckg;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PackageRepository {
    public HttpStatus postPckg(Pckg pckg, UnitOfWork unitOfWork) {
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    INSERT INTO package VALUES (DEFAULT, ?,?)
                """))
        {
            preparedStatement.setString(1, pckg.getPackage_name());
            preparedStatement.setInt(2, pckg.getPackage_price());

            preparedStatement.executeUpdate();

            return HttpStatus.CREATED;

        }catch (SQLException e) {
            System.err.println("postPckg() doesn't work");
            throw new DataAccessException("INSERT NICHT ERFOLGREICH", e);
        }
    }
}
