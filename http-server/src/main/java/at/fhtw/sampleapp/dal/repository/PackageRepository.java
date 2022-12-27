package at.fhtw.sampleapp.dal.repository;

import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.sampleapp.dal.DataAccessException;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.model.Card;
import at.fhtw.sampleapp.model.Pckg;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PackageRepository {
    public int postPckg(Pckg pckg, UnitOfWork unitOfWork) {
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    INSERT INTO package(package_name, package_price) VALUES (?,?) RETURNING package_id
                """))
        {
            preparedStatement.setString(1, pckg.getPackage_name());
            preparedStatement.setInt(2, pckg.getPackage_price());

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
            {
                return resultSet.getInt(1);
            }
        }catch (SQLException e) {
            System.err.println("postPckg() doesn't work");
            throw new DataAccessException("INSERT NICHT ERFOLGREICH", e);
        }
        return -1;
    }

    public HttpStatus postCard(Card card, UnitOfWork unitOfWork) {
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    INSERT INTO card(
                                    card_code_id,
                                    card_name,
                                    card_dmg,
                                    card_element,
                                    card_type,
                                    card_in_package) VALUES (?,?,?,?,?,?)
                """))
        {
            preparedStatement.setString(1, card.getCard_code_id());
            preparedStatement.setString(2, card.getCard_name());
            preparedStatement.setInt(3, card.getCard_dmg());
            preparedStatement.setString(4, card.getCard_element());
            preparedStatement.setString(5, card.getCard_type());
            preparedStatement.setInt(6, card.getCard_in_package());

            preparedStatement.executeUpdate();

            return HttpStatus.CREATED;

        } catch (SQLException e) {
            System.err.println("postCard() doesn't work");
            throw new DataAccessException("INSERT NICHT ERFOLGREICH", e);
        }
    }
}
