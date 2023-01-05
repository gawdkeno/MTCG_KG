package at.fhtw.sampleapp.service.battle;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.controller.Controller;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.dal.repository.BattleRepository;
import at.fhtw.sampleapp.dal.repository.UserRepository;
import at.fhtw.sampleapp.model.Battle;

public class BattleController extends Controller {
    private final UserRepository userRepository;
    private final BattleRepository battleRepository;

    static final Object lock = new Object();

    public BattleController() {
        this.userRepository = new UserRepository();
        this.battleRepository = new BattleRepository();
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
//                if(battle.getBattle_player_a_id() != -1 && battle.getBattle_player_b_id() != -1) {      //check if lobby is full
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
//                }
            }
        } catch (Exception e) {
            unitOfWork.rollbackTransaction();
            throw new RuntimeException(e);
        }
        return null;
    }
}
