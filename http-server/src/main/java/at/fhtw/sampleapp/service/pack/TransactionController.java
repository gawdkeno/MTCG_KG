package at.fhtw.sampleapp.service.pack;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.dal.repository.PackageRepository;
import at.fhtw.sampleapp.model.Pckg;
import at.fhtw.sampleapp.model.User;

public class TransactionController {

    private final PackageRepository packagerepository;

    public TransactionController() {
        this.packagerepository = new PackageRepository();
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
        // TODO: see if this is really necessary or I can just work with my locally created
        //  variables so f.e. just selectedPackage_id instead of pckg.setPackage_id(selectedPackage_id)
        User user = new User();
        Pckg pckg  = new Pckg();

        int player_id = this.packagerepository.buyerId(currentToken, unitOfWork);
        int selectedPackage_id = this.packagerepository.selectPackage(pckg, unitOfWork);
        // if there are no packs
        if (selectedPackage_id < 0) {
            unitOfWork.rollbackTransaction();
            return new Response(
                    HttpStatus.BAD_REQUEST,
                    ContentType.JSON,
                    "{ \"message\":\"Failed, no packs to buy\" }"
            );
        }
        pckg.setPackage_id(selectedPackage_id);
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
        user.setPlayer_coins(playerCoins);

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
        httpStatus = this.packagerepository.deletePackage(pckg.getPackage_id(), unitOfWork);
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
