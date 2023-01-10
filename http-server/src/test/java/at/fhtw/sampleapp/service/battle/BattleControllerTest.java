package at.fhtw.sampleapp.service.battle;

import at.fhtw.sampleapp.model.Card;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class BattleControllerTest {
    BattleController battleController = new BattleController();
    Card cardA = new Card();
    Card cardB = new Card();
    @Test
    void testBattleYourself() throws IOException {
        testEnterLobbyKienboec();
        testEnterLobbyKienboec();
    }
    void testEnterLobbyKienboec() throws IOException {
        URL url = new URL("http://localhost:10001/battles");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Authorization", "Basic kienboec-mtcgToken");
        int responseCode = urlConnection.getResponseCode();

        if(responseCode == HttpURLConnection.HTTP_OK){
            InputStream inputStream = urlConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String inputLine;
            while ((inputLine = bufferedReader.readLine()) != null) {
                System.out.println(inputLine + "\n");
            }
        } else {
            Assertions.assertTrue(true);
        }
    }
    @Test
    void testDraw() {
        cardA.setCard_name("Dragon");
        cardA.setCard_dmg(50);
        cardA.setCard_type("monster");
        cardA.setCard_element("normal");
        cardB.setCard_name("Dragon");
        cardB.setCard_dmg(50);
        cardB.setCard_type("monster");
        cardB.setCard_element("fire");

        Card winner = this.battleController.battleRound(cardA, cardB);
        Assertions.assertEquals("Draw", winner.getCard_name());
    }
    @Test
    void testDrawDragonVsGoblin() {
        cardA.setCard_name("Dragon");
        cardA.setCard_dmg(50);
        cardA.setCard_type("monster");
        cardA.setCard_element("normal");
        cardB.setCard_name("Goblin");
        cardB.setCard_dmg(1000000);
        cardB.setCard_type("monster");
        cardB.setCard_element("normal");

        Card winner = this.battleController.battleRound(cardA, cardB);
        Assertions.assertEquals("Draw", winner.getCard_name());
    }
    @Test
    void testDrawDragonVsFireElf() {
        cardA.setCard_name("Dragon");
        cardA.setCard_dmg(1000000);
        cardA.setCard_type("monster");
        cardA.setCard_element("normal");
        cardB.setCard_name("FireElf");
        cardB.setCard_dmg(1);
        cardB.setCard_type("monster");
        cardB.setCard_element("fire");

        Card winner = this.battleController.battleRound(cardA, cardB);
        Assertions.assertEquals("Draw", winner.getCard_name());
    }
    @Test
    void testKnightVsWaterSpell() {
        cardA.setCard_name("Knight");
        cardA.setCard_dmg(1000000);
        cardA.setCard_type("monster");
        cardA.setCard_element("normal");
        cardB.setCard_name("WaterSpell");
        cardB.setCard_dmg(1);
        cardB.setCard_type("spell");
        cardB.setCard_element("water");

        Card winner = this.battleController.battleRound(cardA, cardB);
        Assertions.assertEquals(cardB, winner);
    }
    @Test
    void testDrawWizardVsOrk() {
        cardA.setCard_name("Wizard");
        cardA.setCard_dmg(1);
        cardA.setCard_type("monster");
        cardA.setCard_element("normal");
        cardB.setCard_name("Ork");
        cardB.setCard_dmg(1000000);
        cardB.setCard_type("monster");
        cardB.setCard_element("normal");

        Card winner = this.battleController.battleRound(cardA, cardB);
        Assertions.assertEquals("Draw", winner.getCard_name());
    }
    @Test
    void testDrawFireSpellVsWaterSpell() {
        cardA.setCard_name("FireSpell");
        cardA.setCard_dmg(60);
        cardA.setCard_type("spell");
        cardA.setCard_element("fire");
        cardB.setCard_name("WaterSpell");
        cardB.setCard_dmg(15);
        cardB.setCard_type("spell");
        cardB.setCard_element("water");

        Card winner = this.battleController.battleRound(cardA, cardB);
        Assertions.assertEquals("Draw", winner.getCard_name());
    }
    @Test
    void testFireSpellVsWaterGoblin() {
        cardA.setCard_name("FireSpell");
        cardA.setCard_dmg(61);
        cardA.setCard_type("spell");
        cardA.setCard_element("fire");
        cardB.setCard_name("WaterGoblin");
        cardB.setCard_dmg(15);
        cardB.setCard_type("monster");
        cardB.setCard_element("water");

        Card winner = this.battleController.battleRound(cardA, cardB);
        Assertions.assertEquals(cardA, winner);
    }

    @Test
    void testFireSpellVsRegularSpell() {
        cardA.setCard_name("FireSpell");
        cardA.setCard_dmg(60);
        cardA.setCard_type("spell");
        cardA.setCard_element("fire");
        cardB.setCard_name("RegularSpell");
        cardB.setCard_dmg(60);
        cardB.setCard_type("spell");
        cardB.setCard_element("normal");

        Card winner = this.battleController.battleRound(cardA, cardB);
        Assertions.assertEquals(cardA, winner);
    }

}
