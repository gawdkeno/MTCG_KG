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

//        for (int round = 0; round < 100; ++round) {
            Card cardA = deckPlayerA.get(new Random().nextInt(deckPlayerA.size()));
            Card cardB = deckPlayerB.get(new Random().nextInt(deckPlayerB.size()));



            Card roundWinner = battleRound(cardA, cardB);
            if (roundWinner != null) {
                System.out.println(roundWinner.getCard_name());
                System.out.println(roundWinner.getCard_dmg());
            }
            else
                System.out.println("DRAW OR NOT IMPLEMENTED");
//        }
    }

    private Card battleRound(Card cardA, Card cardB) {
        System.out.println(cardA.getCard_name() + "\n" + cardB.getCard_name() + "\n");
        System.out.println(cardA.getCard_type() + "\n" + cardB.getCard_type() + "\n");
        if ((cardA.getCard_type().equals("monster") && cardB.getCard_type().equals("monster")) ||
            (cardA.getCard_name().equals(cardB.getCard_name()))) {
            return normalFight(cardA, cardB);
        }
        return null;
    }

    private Card normalFight(Card cardA, Card cardB) {
        if (cardA.getCard_dmg() > cardB.getCard_dmg())
            return cardA;
        if (cardA.getCard_dmg() < cardB.getCard_dmg())
            return cardB;
        else
            return null;
    }
}
