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
                    startBattle(battle, unitOfWork);
//                    if (startBattle(battle, unitOfWork)){
//                        this.battleRepository.setWinner(battle, unitOfWork);
//                    }else{
//                        //draw
//                        battleRepo.setWinner(battle, unitOfWork);
//                        //update total played games
//                        new StatsRepo().updateTotal(battle.getPlayerA(), unitOfWork);
//                        new StatsRepo().updateTotal(battle.getPlayerB(), unitOfWork);
//                    }
//                }else{
//                    waitForBattle(battle, unitOfWork);
                }
            }
        } catch (Exception e) {
            unitOfWork.rollbackTransaction();
            throw new RuntimeException(e);
        }
        return null;
    }

    private void startBattle(Battle battle, UnitOfWork unitOfWork) {
        Collection<Card> deckDataA = this.cardRepository.getDeck(battle.getBattle_player_a_id(), unitOfWork);
        Collection<Card> deckDataB = this.cardRepository.getDeck(battle.getBattle_player_b_id(), unitOfWork);
        List<Card> deckPlayerA = new ArrayList<>(deckDataA);
        List<Card> deckPlayerB = new ArrayList<>(deckDataB);

        for (int round = 0; round < 100; ++round) {
            Card cardA = deckPlayerA.get(new Random().nextInt(deckPlayerA.size()));
            Card cardB = deckPlayerB.get(new Random().nextInt(deckPlayerB.size()));

            Card roundWinner = battleRound(cardA, cardB);
            if (roundWinner != null) {
                if (roundWinner.equals(cardA)) {
                    deckPlayerB.remove(cardB);
                    deckPlayerA.add(cardB);
                } else {
                    deckPlayerA.remove(cardA);
                    deckPlayerB.add(cardA);
                }
            }
            else
                System.out.println("DRAW OR NOT IMPLEMENTED");
        }
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

    private Card fight(Card cardA, Card cardB, boolean specialFight) {
        if (!specialFight) {
            if (cardA.getCard_dmg() > cardB.getCard_dmg())
                return cardA;
            if (cardA.getCard_dmg() < cardB.getCard_dmg())
                return cardB;
            else
                return null;
        }

        boolean cardBAdvantage = false;
        if ((cardB.getCard_element().equals("fire") && cardA.getCard_element().equals("normal")) ||
                (cardB.getCard_element().equals("water") && cardA.getCard_element().equals("fire")) ||
                (cardB.getCard_element().equals("normal") && cardA.getCard_element().equals("water")))
        {
            cardBAdvantage = true;
        }
        if (cardBAdvantage) {
            if (cardA.getCard_dmg() / 2 > cardB.getCard_dmg() * 2)
                return cardA;
            if (cardA.getCard_dmg() / 2 < cardB.getCard_dmg() * 2)
                return cardB;
            else
                return null;
        } else {
            if (cardA.getCard_dmg() * 2 > cardB.getCard_dmg() / 2)
                return cardA;
            if (cardA.getCard_dmg() * 2 < cardB.getCard_dmg() / 2)
                return cardB;
            else
                return null;
        }
    }

    private Card monsterFight(Card cardA, Card cardB) {
        if (cardA.getCard_name().contains("Goblin")) {
             if (cardB.getCard_name().contains("Dragon")) {
                 if (cardB.getCard_dmg() > cardA.getCard_dmg())
                     return cardB;
                 else
                    return null;
             }
        } else if (cardA.getCard_name().contains("Dragon")) {
            if (cardB.getCard_name().contains("Goblin")) {
                if (cardA.getCard_dmg() > cardB.getCard_dmg())
                    return cardA;
                else
                    return null;
            } else if (cardB.getCard_name().equals("FireElf")) {
                if (cardB.getCard_dmg() > cardA.getCard_dmg())
                    return cardB;
                else
                    return null;
            }
        } else if (cardA.getCard_name().equals("FireElf")) {
            if (cardB.getCard_name().contains("Dragon")) {
                if (cardA.getCard_dmg() > cardB.getCard_dmg())
                    return cardA;
                else
                    return null;
            }
        } else if (cardA.getCard_name().contains("Wizard")) {
            if (cardB.getCard_name().contains("Ork")) {
                if (cardA.getCard_dmg() > cardB.getCard_dmg())
                    return cardA;
                else
                    return null;
            }
        } else if (cardA.getCard_name().contains("Ork")) {
            if (cardB.getCard_name().contains("Wizard")) {
                if (cardB.getCard_dmg() > cardA.getCard_dmg())
                    return cardB;
                else
                    return null;
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
