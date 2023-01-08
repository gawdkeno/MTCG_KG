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
        } catch (SQLException e) {
            System.err.println("postPckg() doesn't work");
            throw new DataAccessException("INSERT NICHT ERFOLGREICH", e);
        }
        return -1;
    }

    // TODO: move to CardRepo
    public HttpStatus postCard(Card card, UnitOfWork unitOfWork) {
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    INSERT INTO card(
                                    card_code_id,
                                    card_name,
                                    card_dmg,
                                    card_element,
                                    card_type,
                                    card_package_id) VALUES (?,?,?,?,?,?)
                """))
        {
            preparedStatement.setString(1, card.getCard_code_id());
            preparedStatement.setString(2, card.getCard_name());
            preparedStatement.setFloat(3, card.getCard_dmg());
            preparedStatement.setString(4, card.getCard_element());
            preparedStatement.setString(5, card.getCard_type());
            preparedStatement.setInt(6, card.getCard_package_id());

            preparedStatement.executeUpdate();

            return HttpStatus.CREATED;

        } catch (SQLException e) {
            System.err.println("postCard() doesn't work");
            throw new DataAccessException("INSERT NICHT ERFOLGREICH", e);
        }
    }

    public int selectPackage(UnitOfWork unitOfWork) {
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    SELECT * FROM package LIMIT 1
                """)) {

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next())
            {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("selectPackage() doesn't work");
            throw new DataAccessException("SELECT NICHT ERFOLGREICH", e);
        }
        return -1;
    }

    // TODO: move to PlayerRepo
    public int checkCoins(String currentToken, UnitOfWork unitOfWork) {
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    SELECT player_coins FROM player WHERE player_token LIKE ?
                """)) {

            preparedStatement.setString(1, currentToken);
            ResultSet resultSet = preparedStatement.executeQuery();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            System.err.println("checkCoins() doesn't work");
            throw new DataAccessException("SELECT NICHT ERFOLGREICH", e);
        }
    }

    // TODO: move to CardRepo
    public HttpStatus addCardsToPlayer(int player_id, int selectedPackage_id, UnitOfWork unitOfWork) {
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    UPDATE card SET card_player_id = ? WHERE card_package_id = ?
                """))
        {
            preparedStatement.setInt(1, player_id);
            preparedStatement.setInt(2, selectedPackage_id);

            preparedStatement.executeUpdate();

            return HttpStatus.OK;

        } catch (SQLException e) {
            System.err.println("addCardsToPlayer() doesn't work");
            throw new DataAccessException("UPDATE NICHT ERFOLGREICH", e);
        }
    }

    // TODO: move to PlayerRepo
    public HttpStatus reducePlayerCoins(int playerCoins, int player_id, UnitOfWork unitOfWork) {
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    UPDATE player SET player_coins = ? WHERE player_id = ?
                """))
        {
            preparedStatement.setInt(1, playerCoins - 5);
            preparedStatement.setInt(2, player_id);

            preparedStatement.executeUpdate();

            return HttpStatus.OK;

        } catch (SQLException e) {
            System.err.println("addCardsToPlayer() doesn't work");
            throw new DataAccessException("UPDATE NICHT ERFOLGREICH", e);
        }
    }

    public HttpStatus deletePackage(int package_id, UnitOfWork unitOfWork) {
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    DELETE FROM package WHERE package_id = ?
                """))
        {
            preparedStatement.setInt(1, package_id);

            preparedStatement.executeUpdate();

            return HttpStatus.OK;

        } catch (SQLException e) {
            System.err.println("addCardsToPlayer() doesn't work");
            throw new DataAccessException("DELETE NICHT ERFOLGREICH", e);
        }
    }
}
