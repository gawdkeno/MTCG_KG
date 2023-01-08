package at.fhtw.sampleapp.service.pckg;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.dal.repository.PackageRepository;
import at.fhtw.sampleapp.dal.repository.UserRepository;

public class TransactionController {

    private final PackageRepository packagerepository;
    private final UserRepository userRepository;

    public TransactionController() {
        this.packagerepository = new PackageRepository();
        this.userRepository = new UserRepository();
    }
    public Response buyPackage(Request request) {
        String currentToken = request.getCurrentToken();
        if (currentToken == null) {
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.JSON,
                    "{ \"message\": \"Failed, no token was given\" }"
            );
        }
        UnitOfWork unitOfWork = new UnitOfWork();

        int player_id = this.userRepository.getPlayerId(currentToken, unitOfWork);
        int selectedPackage_id = this.packagerepository.selectPackage(unitOfWork);
        // if there are no packs
        if (selectedPackage_id < 0) {
            unitOfWork.rollbackTransaction();
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"message\":\"Failed, no packs to buy\" }"
            );
        }
        int playerCoins = this.packagerepository.checkCoins(currentToken, unitOfWork);
        // if player can't buy any packs
        if (playerCoins < 5) {
            unitOfWork.rollbackTransaction();
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"message\":\"Failed, you're broke G\" }"
            );
        }
        HttpStatus httpStatus = this.packagerepository.addCardsToPlayer(player_id, selectedPackage_id, unitOfWork);
        if (httpStatus != HttpStatus.OK) {
            unitOfWork.rollbackTransaction();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\":\"Failed, couldn't add cards to player\" }"
            );
        }
        httpStatus = this.packagerepository.reducePlayerCoins(playerCoins, player_id, unitOfWork);
        if (httpStatus != HttpStatus.OK) {
            unitOfWork.rollbackTransaction();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\":\"Failed, couldn't reduce coins\" }"
            );
        }
        httpStatus = this.packagerepository.deletePackage(selectedPackage_id, unitOfWork);
        if (httpStatus != HttpStatus.OK) {
            unitOfWork.rollbackTransaction();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\":\"Failed, couldn't delete package\" }"
            );
        }
        unitOfWork.commitTransaction();
        return new Response(
                HttpStatus.OK,
                ContentType.JSON,
                "{ \"message\":\"Success, you bought a Booster-Pack\" }"
        );
    }
}
