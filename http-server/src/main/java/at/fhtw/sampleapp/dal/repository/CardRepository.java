package at.fhtw.sampleapp.dal.repository;

import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.sampleapp.dal.DataAccessException;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.model.Card;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CardRepository {

    public Collection<Card> getCardsWithId(int playerId, UnitOfWork unitOfWork) {
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    SELECT card_name, card_dmg, card_element, card_type FROM card WHERE card_player_id = ?
                """)) {

            preparedStatement.setInt(1, playerId);
            ResultSet resultSet = preparedStatement.executeQuery();
            Collection<Card> cardRows = new ArrayList<>();
            while(resultSet.next())
            {
                Card card = new Card(
                        resultSet.getString(1),
                        resultSet.getInt(2),
                        resultSet.getString(3),
                        resultSet.getString(4));
                        // resultSet.getInt(5));
                cardRows.add(card);
            }
            return cardRows;
        } catch (SQLException e) {
            System.err.println("getCards() doesn't work");
            throw new DataAccessException("SELECT NICHT ERFOLGREICH", e);
        }
    }

    public Collection<Card> getDeck(int playerId, UnitOfWork unitOfWork) {
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    SELECT card_name, card_dmg, card_element, card_type FROM card WHERE card_player_id = ? AND card_in_deck = ?
                """)) {

            preparedStatement.setInt(1, playerId);
            preparedStatement.setBoolean(2, true);
            // TODO: handle duplicate error in IntelliJ
            ResultSet resultSet = preparedStatement.executeQuery();
            Collection<Card> deck = new ArrayList<>();
            while(resultSet.next())
            {
                Card card = new Card(
                        resultSet.getString(1),
                        resultSet.getInt(2),
                        resultSet.getString(3),
                        resultSet.getString(4));
                // resultSet.getInt(5));
                deck.add(card);
            }
            if (deck.size() != 4) {
                return null;
            }
            return deck;
        } catch (SQLException e) {
            System.err.println("getDeck() doesn't work");
            throw new DataAccessException("SELECT NICHT ERFOLGREICH", e);
        }
    }

    public HttpStatus updateCardsInDeck(int playerId, List<String> cardCodeIds, UnitOfWork unitOfWork) {
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    UPDATE card SET card_in_deck = ? WHERE card_code_id IN (?,?,?,?) AND card_player_id = ?
                """))
        {
            // TODO: !!!UNSET TRUE IF NEW DECK GETS CONFIGURED
            // TODO: return error message when same deck wants to be configured

            preparedStatement.setBoolean(1, true);
            preparedStatement.setString(2, cardCodeIds.get(0));
            preparedStatement.setString(3, cardCodeIds.get(1));
            preparedStatement.setString(4, cardCodeIds.get(2));
            preparedStatement.setString(5, cardCodeIds.get(3));
            preparedStatement.setInt(6, playerId);

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows != 4) {
                return HttpStatus.CONFLICT;
            }

            return HttpStatus.OK;
        } catch (SQLException e) {
            System.err.println("putCardsInDeck() doesn't work");
            throw new DataAccessException("UPDATE NICHT ERFOLGREICH", e);
        }
    }
}
