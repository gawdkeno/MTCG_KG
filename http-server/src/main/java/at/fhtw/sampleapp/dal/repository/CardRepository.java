package at.fhtw.sampleapp.dal.repository;

import at.fhtw.sampleapp.dal.DataAccessException;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.model.Card;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class CardRepository {

    public Collection<Card> getCards(int playerId, UnitOfWork unitOfWork) {
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    SELECT card_name, card_dmg, card_element, card_type, card_player_id FROM card WHERE card_player_id = ?
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
}
