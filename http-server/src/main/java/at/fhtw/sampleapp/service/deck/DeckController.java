package at.fhtw.sampleapp.service.deck;

import at.fhtw.httpserver.http.ContentType;
import at.fhtw.httpserver.http.HttpStatus;
import at.fhtw.httpserver.server.Request;
import at.fhtw.httpserver.server.Response;
import at.fhtw.sampleapp.controller.Controller;
import at.fhtw.sampleapp.dal.UnitOfWork;
import at.fhtw.sampleapp.dal.repository.CardRepository;
import at.fhtw.sampleapp.dal.repository.UserRepository;
import at.fhtw.sampleapp.model.Card;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DeckController extends Controller {
    private final CardRepository cardRepository;
    private final UserRepository userRepository;

    public DeckController() {
        this.cardRepository = new CardRepository();
        this.userRepository = new UserRepository();
    }
    public Response showDeck(Request request) {
        UnitOfWork unitOfWork = new UnitOfWork();
        String currentToken = request.getCurrentToken();
        if (currentToken == null) {
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.JSON,
                    "{ \"message\": \"Failed, no token was given\" }"
            );
        }
        try (unitOfWork){
            int player_id = this.userRepository.getPlayerId(currentToken, unitOfWork);
            Collection<Card> deckData = this.cardRepository.getDeck(player_id, unitOfWork);
            if (deckData == null) {
                unitOfWork.rollbackTransaction();
                return new Response(
                        HttpStatus.CONFLICT,
                        ContentType.JSON,
                        "{ \"message\" : \"You have no deck configured\" }"
                );
            }
            String cardDataJSON = this.getObjectMapper().writeValueAsString(deckData);

            unitOfWork.commitTransaction();
            if (request.getParams() != null) {
                return new Response(
                        HttpStatus.OK,
                        ContentType.PLAIN_TEXT,
                        (cardDataJSON + "\n")
                );
            }
            return new Response(
                    HttpStatus.OK,
                    ContentType.JSON,
                    (cardDataJSON + "\n")
            );
        } catch (Exception e) {
            e.printStackTrace();

            unitOfWork.rollbackTransaction();
            return new Response(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    ContentType.JSON,
                    "{ \"message\" : \"Internal Server Error\" }"
            );
        }
    }
    public Response configureDeck(Request request) {
        UnitOfWork unitOfWork = new UnitOfWork();
        String currentToken = request.getCurrentToken();
        if (currentToken == null) {
            return new Response(
                    HttpStatus.UNAUTHORIZED,
                    ContentType.JSON,
                    "{ \"message\": \"Failed, no token was given\" }"
            );
        }
        try {
            List<String> cardCodeIds = trimPayload(request.getBody());
            if(cardCodeIds.size() != 4){
                unitOfWork.rollbackTransaction();
                return new Response(

                        HttpStatus.BAD_REQUEST,
                        ContentType.JSON,
                        "{ \"message\" : \"Failed, You need 4 cards in your deck\" }"
                );
            }
            int playerId = this.userRepository.getPlayerId(currentToken, unitOfWork);
            HttpStatus httpStatus = this.cardRepository.putCardsInDeck(playerId, cardCodeIds, unitOfWork);
            if (httpStatus == HttpStatus.OK) {
                unitOfWork.commitTransaction();
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        "{ \"message\": \"Success, cards added to deck\" }"
                );
            }
            unitOfWork.rollbackTransaction();
            return new Response(
                    HttpStatus.CONFLICT,
                    ContentType.JSON,
                    "{ \"message\": \"Failed, those cards are already in your deck, or you didn't select enough\" }"
            );
        } catch (Exception e){
            e.printStackTrace();
                unitOfWork.rollbackTransaction();
                return new Response(
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        ContentType.JSON,
                        "{ \"message\" : \"Failed, something went wrong\" }"
                );
            }
        unitOfWork.rollbackTransaction();
        return new Response(
                HttpStatus.CONFLICT,
                ContentType.JSON,
                "{ \"message\" : \"Failed, no cards found with those ID's\" }"
        );
    }


    public List<String> trimPayload(String body) {
        String newBody = body.replace("[", "")
                .replace("]", "")
                .replaceAll("\"", "")
                .trim();

        String[] ids = newBody.split(",");
        List<String> idsClean = new ArrayList<>();
        for(String id : ids){
            idsClean.add(id.trim());
        }
        return idsClean;
    }
}
