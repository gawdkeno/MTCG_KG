package at.fhtw.sampleapp.dal.repository;

import at.fhtw.sampleapp.dal.DataAccessException;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.model.Battle;
import at.fhtw.sampleapp.model.BattleRound;
import at.fhtw.sampleapp.model.Card;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

public class BattleRepository {
    public Battle getLobby(UnitOfWork unitOfWork) {
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    SELECT battle_id, battle_player_a_id FROM battle WHERE battle_player_b_id IS NULL LIMIT 1
                """)) {
            ResultSet resultSet = preparedStatement.executeQuery();
            Battle battle = new Battle();
            // if lobby exists
            if (resultSet.next())
            {
                battle.setBattle_id(resultSet.getInt(1));
                battle.setBattle_player_a_id(resultSet.getInt(2));
                return battle;
            }
            // new lobby gets created and ID of battle saved
            battle.setBattle_id(createLobby(unitOfWork));
            if (battle.getBattle_id() != -1) {
                return battle;
            }
        } catch (SQLException e) {
            System.err.println("getLobby() doesn't work");
            throw new DataAccessException("SELECT NICHT ERFOLGREICH", e);
        }
        return null;
    }

    private int createLobby(UnitOfWork unitOfWork) {
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    INSERT INTO battle VALUES (DEFAULT) RETURNING battle_id
                """)) {
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("createLobby() doesn't work");
            throw new DataAccessException("INSERT NICHT ERFOLGREICH", e);
        }
        return -1;
    }

    public void addUserToLobby(int currentPlayerId, Battle battle, UnitOfWork unitOfWork) {
        PreparedStatement preparedStatement;
        try {
            if (battle.getBattle_player_a_id() == -1) {
                battle.setBattle_player_a_id(currentPlayerId);
                preparedStatement =
                        unitOfWork.prepareStatement("""
                        UPDATE battle SET battle_player_a_id = ? WHERE battle_id = ?
                    """);
            } else {
                battle.setBattle_player_b_id(currentPlayerId);
                preparedStatement =
                        unitOfWork.prepareStatement("""
                        UPDATE battle SET battle_player_b_id = ? WHERE battle_id = ?
                    """);
            }
            preparedStatement.setInt(1, currentPlayerId);
            preparedStatement.setInt(2, battle.getBattle_id());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("addUserToLobby() doesn't work");
            throw new DataAccessException("UPDATE NICHT ERFOLGREICH", e);
        }
    }

    public void addBattleRound(Card cardA, Card cardB, Card roundWinner, Battle battle, UnitOfWork unitOfWork) {
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    INSERT INTO battle_round VALUES (DEFAULT,?,?,?,?)
                """)) {
            preparedStatement.setInt(1, battle.getBattle_id());
            preparedStatement.setInt(2, cardA.getCard_id());
            preparedStatement.setInt(3, cardB.getCard_id());
            preparedStatement.setInt(4, roundWinner.getCard_id());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("addBattleRound() doesn't work");
            throw new DataAccessException("INSERT NICHT ERFOLGREICH", e);
        }
    }

    public void finishBattle(Battle battle, UnitOfWork unitOfWork) {
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    UPDATE battle SET battle_finished = ? WHERE battle_id = ?
                """)) {
            preparedStatement.setBoolean(1, true);
            preparedStatement.setInt(2, battle.getBattle_id());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("finishBattle() doesn't work");
            throw new DataAccessException("UPDATE NICHT ERFOLGREICH", e);
        }
    }

    public void updateWinner(Battle battle, UnitOfWork unitOfWork) {
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    UPDATE battle SET battle_winner_id = ? WHERE battle_id = ?
                """)) {
            preparedStatement.setInt(1, battle.getBattle_winner_id());
            preparedStatement.setInt(2, battle.getBattle_id());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("updateWinner() doesn't work");
            throw new DataAccessException("UPDATE NICHT ERFOLGREICH", e);
        }
    }

    public Collection<BattleRound> getBattleLog(Battle battle, UnitOfWork unitOfWork) {
        try (PreparedStatement preparedStatement =
                     unitOfWork.prepareStatement("""
                    SELECT battle_round_id,
                        cardA.card_name,
                        cardA.card_dmg,
                        cardB.card_name,
                        cardB.card_dmg,
                        cardW.card_name,
                        cardW.card_dmg
                    FROM battle_round
                        JOIN card cardA ON battle_round_card_a_id = cardA.card_id
                        JOIN card cardB ON battle_round_card_b_id = cardB.card_id
                        JOIN card cardW ON battle_round_winner_card_id = cardW.card_id
                    WHERE battle_round_battle_id = ?;
                             """)) {
            preparedStatement.setInt(1, battle.getBattle_id());
            ResultSet resultSet = preparedStatement.executeQuery();
            Collection<BattleRound> battleRoundRows = new ArrayList<>();

            while(resultSet.next()) {
                BattleRound battleRound = new BattleRound(
                        resultSet.getString(1),
                        resultSet.getInt(2),
                        resultSet.getString(3),
                        resultSet.getInt(4),
                        resultSet.getString(5),
                        resultSet.getInt(6));
                battleRoundRows.add(battleRound);
            }
            return battleRoundRows;
        } catch (SQLException e) {
            System.err.println("getBattleLog() doesn't work");
            throw new DataAccessException("SELECT NICHT ERFOLGREICH", e);
        }
    }
}
