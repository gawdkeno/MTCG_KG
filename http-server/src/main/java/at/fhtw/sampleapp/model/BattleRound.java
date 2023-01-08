package at.fhtw.sampleapp.model;

public class BattleRound {
    private String cardAName;
    private int cardADmg;
    private String cardBName;
    private int cardBDmg;
    private String winnerCardName;
    private int winnerCardDmg;

    public BattleRound(String cardAName, int cardADmg, String cardBName, int cardBDmg, String winnerCardName, int winnerCardDmg) {
        this.cardAName = cardAName;
        this.cardADmg = cardADmg;
        this.cardBName = cardBName;
        this.cardBDmg = cardBDmg;
        this.winnerCardName = winnerCardName;
        this.winnerCardDmg = winnerCardDmg;
    }

    public String getCardAName() {
        return cardAName;
    }

    public void setCardAName(String cardAName) {
        this.cardAName = cardAName;
    }

    public int getCardADmg() {
        return cardADmg;
    }

    public void setCardADmg(int cardADmg) {
        this.cardADmg = cardADmg;
    }

    public String getCardBName() {
        return cardBName;
    }

    public void setCardBName(String cardBName) {
        this.cardBName = cardBName;
    }

    public int getCardBDmg() {
        return cardBDmg;
    }

    public void setCardBDmg(int cardBDmg) {
        this.cardBDmg = cardBDmg;
    }

    public String getWinnerCardName() {
        return winnerCardName;
    }

    public void setWinnerCardName(String winnerCardName) {
        this.winnerCardName = winnerCardName;
    }

    public int getWinnerCardDmg() {
        return winnerCardDmg;
    }

    public void setWinnerCardDmg(int winnerCardDmg) {
        this.winnerCardDmg = winnerCardDmg;
    }
}
