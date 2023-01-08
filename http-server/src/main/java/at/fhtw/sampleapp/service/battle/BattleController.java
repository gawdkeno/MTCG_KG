package at.fhtw.sampleapp.service.battle;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.controller.Controller;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.dal.repository.BattleRepository;
import at.fhtw.sampleapp.dal.repository.CardRepository;
import at.fhtw.sampleapp.dal.repository.UserRepository;
import at.fhtw.sampleapp.model.Battle;
import at.fhtw.sampleapp.model.Card;

import java.util.*;

public class BattleController extends Controller {
    private final UserRepository userRepository;
    private final BattleRepository battleRepository;
    private final CardRepository cardRepository;

    static final Object lock = new Object();

    public BattleController() {
        this.userRepository = new UserRepository();
        this.battleRepository = new BattleRepository();
        this.cardRepository = new CardRepository();
    }
    public Response initializeBattle(Request request) {
        UnitOfWork unitOfWork = new UnitOfWork();
        String currentToken = request.getCurrentToken();
        if (!(userRepository.userExists(currentToken, unitOfWork))) {
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.JSON,
                    "{ \"message\": \"Failed, invalid or no token was given\" }"
            );
        }
        int currentPlayerId = this.userRepository.getPlayerId(currentToken, unitOfWork);
        try {
            Battle battle;
            synchronized (lock) {
                battle = this.battleRepository.getLobby(unitOfWork);
                this.battleRepository.addUserToLobby(currentPlayerId, battle, unitOfWork);
                if (battle.getBattle_player_a_id() == battle.getBattle_player_b_id()) {
                    unitOfWork.rollbackTransaction();
                    return new Response(
                            HttpStatus.BAD_REQUEST,
                            ContentType.JSON,
                            "{ \"message\": \"Failed, you can't fight against yourself\" }"
                    );
                }
                unitOfWork.commitTransaction();
                if(battle.getBattle_player_a_id() != -1 && battle.getBattle_player_b_id() != -1) {
                    int battleWinnerId = startBattle(battle, unitOfWork);
                    this.battleRepository.finishBattle(battle, unitOfWork);
                    if (battleWinnerId != -1) {
                        this.battleRepository.updateWinner(battle, unitOfWork);
                        unitOfWork.commitTransaction();
                    }
                    unitOfWork.commitTransaction();
                    return new Response(
                            HttpStatus.OK,
                            ContentType.JSON,
                            "{ \"message\":\"Success, battle concluded\" }"
                    );
                }else{
                    startQueue(battle, unitOfWork);
                    return new Response(
                            HttpStatus.OK,
                            ContentType.JSON,
                            "{ \"message\":\"Success, you are now queued\" }"
                    );
                }
//                Collection<BattleRound> battleLogRaw = this.battleRepository.getBattleLog(battle, unitOfWork);
//                List<BattleRound> battleLog = new ArrayList<>(battleLogRaw);
            }
        } catch (Exception e) {
            unitOfWork.rollbackTransaction();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\":\"Failed, something went wrong\" }"
            );
        }
    }

    private void startQueue(Battle battle, UnitOfWork unitOfWork) {
        // ???
    }

    private int startBattle(Battle battle, UnitOfWork unitOfWork) {
        String playerAName = this.userRepository.getPlayerUserName(battle.getBattle_player_a_id(), unitOfWork);
        String playerBName = this.userRepository.getPlayerUserName(battle.getBattle_player_b_id(), unitOfWork);
        Collection<Card> deckDataA = this.cardRepository.getDeck(battle.getBattle_player_a_id(), unitOfWork);
        Collection<Card> deckDataB = this.cardRepository.getDeck(battle.getBattle_player_b_id(), unitOfWork);
        List<Card> deckPlayerA = new ArrayList<>(deckDataA);
        List<Card> deckPlayerB = new ArrayList<>(deckDataB);
        Card cardA = null;
        Card cardB = null;
        Card previousCardA = null;
        Card previousCardB = null;

        for (int round = 1; round < 101; ++round) {
            // unique feature: can't use the same card two times in a row if you have card number advantage
            if (deckPlayerA.size() > 4) {
                while (cardA == previousCardA) {
                    cardA = deckPlayerA.get(new Random().nextInt(deckPlayerA.size()));
                }
            }
            else {
                cardA = deckPlayerA.get(new Random().nextInt(deckPlayerA.size()));
            }
            if (deckPlayerB.size() > 4) {
                while (cardB == previousCardB) {
                    cardB = deckPlayerB.get(new Random().nextInt(deckPlayerB.size()));
                }
            }
            else {
                cardB = deckPlayerB.get(new Random().nextInt(deckPlayerB.size()));
            }
            previousCardA = cardA;
            previousCardB = cardB;

            Card roundWinner = battleRound(cardA, cardB);
            if (roundWinner.equals(cardA)) {
                deckPlayerB.remove(cardB);
                deckPlayerA.add(cardB);
            } else if (roundWinner.equals(cardB)){
                deckPlayerA.remove(cardA);
                deckPlayerB.add(cardA);
            }
            System.out.println("\n----------------------------------------------------------------\n");

            System.out.println("Round " + round);
            System.out.println("'" + playerAName + "' plays " + cardA.getCard_name() + " (" + cardA.getCard_element() + ") with [" + cardA.getCard_dmg() + "] DMG");
            System.out.println("'" + playerBName + "' plays " + cardB.getCard_name() + " (" + cardB.getCard_element() + ") with [" + cardB.getCard_dmg() + "] DMG\n");
            if (roundWinner.equals(cardA)) {
                System.out.println("'" + playerAName + "' wins\n");
            } else if (roundWinner.equals(cardB)) {
                System.out.println("'" + playerBName + "' wins\n");
            } else {
                System.out.println("Both cards are equally strong. It's a Draw\n");
            }
            System.out.println("'" + playerAName + "' now has " + deckPlayerA.size() + " cards in his deck");
            System.out.println("'" + playerBName + "' now has " + deckPlayerB.size() + " cards in his deck");

            this.battleRepository.addBattleRound(cardA, cardB, roundWinner, battle, unitOfWork);
            if (deckPlayerA.isEmpty() || deckPlayerB.isEmpty()) {
                System.out.println("*+*+*+*+*+*+*+*+*+*+*+*+*+*+*+*+*+*+*+*+*+*+*+*+*+*+*+*+");
                if (deckPlayerA.isEmpty()) {
                    battle.setBattle_winner_id(battle.getBattle_player_b_id());
                    System.out.println("                     '" + playerBName + "' wins");
                } else if (deckPlayerB.isEmpty()) {
                    battle.setBattle_winner_id(battle.getBattle_player_a_id());
                    System.out.println("                     '" + playerAName + "' wins");
                }
                System.out.println("*+*+*+*+*+*+*+*+*+*+*+*+*+*+*+*+*+*+*+*+*+*+*+*+*+*+*+*+");
                System.out.println("\n----------------------------------------------------------------\n");
                this.userRepository.updateUserStats(battle, unitOfWork);
                return battle.getBattle_winner_id();
            }
        }
        System.out.println("More than 100 rounds were played, match has finished");
        System.out.println("\n----------------------------------------------------------------\n");

        this.userRepository.updateUserStats(battle, unitOfWork);
        return -1;
    }

    private Card battleRound(Card cardA, Card cardB) {
        if (cardA.getCard_name().equals(cardB.getCard_name())) {
            return fight(cardA, cardB, false);
        } else if (cardA.getCard_type().equals("monster") && cardB.getCard_type().equals("monster")) {
            return monsterFight(cardA, cardB);
        } else {
            return specialFight(cardA, cardB);
        }
    }

    private Card fight(Card cardA, Card cardB, boolean elementRelevance) {
        if (!elementRelevance) {
            if (cardA.getCard_dmg() > cardB.getCard_dmg())
                return cardA;
            if (cardA.getCard_dmg() < cardB.getCard_dmg())
                return cardB;
            else
                return new Card(null, "Draw", 0);
        }

        boolean cardBAdvantage =
                (cardB.getCard_element().equals("fire") && cardA.getCard_element().equals("normal")) ||
                (cardB.getCard_element().equals("water") && cardA.getCard_element().equals("fire")) ||
                (cardB.getCard_element().equals("normal") && cardA.getCard_element().equals("water"));

        if (cardBAdvantage) {
            if (cardA.getCard_dmg() / 2 > cardB.getCard_dmg() * 2)
                return cardA;
            if (cardA.getCard_dmg() / 2 < cardB.getCard_dmg() * 2)
                return cardB;
            else
                return new Card(null, "Draw", 0);
        } else {
            if (cardA.getCard_dmg() * 2 > cardB.getCard_dmg() / 2)
                return cardA;
            if (cardA.getCard_dmg() * 2 < cardB.getCard_dmg() / 2)
                return cardB;
            else
                return new Card(null, "Draw", 0);
        }
    }

    private Card monsterFight(Card cardA, Card cardB) {
        if (cardA.getCard_name().contains("Goblin")) {
             if (cardB.getCard_name().contains("Dragon")) {
                 if (cardB.getCard_dmg() > cardA.getCard_dmg())
                     return cardB;
                 else
                    return new Card(null, "Draw", 0);
             }
        } else if (cardA.getCard_name().contains("Dragon")) {
            if (cardB.getCard_name().contains("Goblin")) {
                if (cardA.getCard_dmg() > cardB.getCard_dmg())
                    return cardA;
                else
                    return new Card(null, "Draw", 0);
            } else if (cardB.getCard_name().equals("FireElf")) {
                if (cardB.getCard_dmg() > cardA.getCard_dmg())
                    return cardB;
                else
                    return new Card(null, "Draw", 0);
            }
        } else if (cardA.getCard_name().equals("FireElf")) {
            if (cardB.getCard_name().contains("Dragon")) {
                if (cardA.getCard_dmg() > cardB.getCard_dmg())
                    return cardA;
                else
                    return new Card(null, "Draw", 0);
            }
        } else if (cardA.getCard_name().contains("Wizard")) {
            if (cardB.getCard_name().contains("Ork")) {
                if (cardA.getCard_dmg() > cardB.getCard_dmg())
                    return cardA;
                else
                    return new Card(null, "Draw", 0);
            }
        } else if (cardA.getCard_name().contains("Ork")) {
            if (cardB.getCard_name().contains("Wizard")) {
                if (cardB.getCard_dmg() > cardA.getCard_dmg())
                    return cardB;
                else
                    return new Card(null, "Draw", 0);
            }
        }
        return fight(cardA, cardB, false);
    }

    private Card specialFight(Card cardA, Card cardB) {
        if (cardA.getCard_name().equals("Knight")) {
            if (cardB.getCard_name().equals("WaterSpell")) {
                return cardB;
            }
        } else if (cardA.getCard_name().equals("WaterSpell")) {
            if (cardB.getCard_name().equals("Knight")) {
               return cardA;
            }
        } else if (cardA.getCard_name().equals("Kraken")) {
            if (cardB.getCard_type().equals("spell")) {
                return cardA;
            }
        } else if (cardA.getCard_type().equals("spell")) {
            if (cardB.getCard_name().equals("Kraken")) {
                return cardB;
            }
        }

        if (cardA.getCard_element().equals(cardB.getCard_element())) {
            return fight(cardA, cardB, false);
        }
        return fight(cardA, cardB, true);
    }
}
