package at.fhtw.sampleapp.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public class Card {
    private int card_id;
    @JsonAlias({"Id"})
    private String card_code_id;
    @JsonAlias({"Name"})
    private String card_name;
    @JsonAlias({"Damage"})
    private int card_dmg;
    private String card_element;
    private String card_type;
    private Boolean card_in_deck;
    private Boolean card_is_locked;
    private int card_belongs_user;

    public Card(String card_code_id, String card_name, int card_dmg, String card_element, String card_type, Boolean card_in_deck, Boolean card_is_locked, int card_belongs_user) {
        this.card_code_id = card_code_id;
        this.card_name = card_name;
        this.card_dmg = card_dmg;
        this.card_element = card_element;
        this.card_type = card_type;
        this.card_in_deck =card_in_deck ;
        this.card_is_locked = card_is_locked;
        this.card_belongs_user = card_belongs_user;
    }
    //needed default-Constructor
    public Card() {

    }

    public int getCard_id() {
        return card_id;
    }

    public void setCard_id(int card_id) {
        this.card_id = card_id;
    }

    public String getCard_code_id() {
        return card_code_id;
    }

    public void setCard_code_id(String card_code_id) {
        this.card_code_id = card_code_id;
    }

    public String getCard_name() {
        return card_name;
    }

    public void setCard_name(String card_name) {
        this.card_name = card_name;
    }

    public int getCard_dmg() {
        return card_dmg;
    }

    public void setCard_dmg(int card_dmg) {
        this.card_dmg = card_dmg;
    }

    public String getCard_element() {
        return card_element;
    }

    public void setCard_element(String card_element) {
        this.card_element = card_element;
    }

    public String getCard_type() {
        return card_type;
    }

    public void setCard_type(String card_type) {
        this.card_type = card_type;
    }

    public Boolean getCard_in_deck() {
        return card_in_deck;
    }

    public void setCard_in_deck(Boolean card_in_deck) {
        this.card_in_deck = card_in_deck;
    }

    public Boolean getCard_is_locked() {
        return card_is_locked;
    }

    public void setCard_is_locked(Boolean card_is_locked) {
        this.card_is_locked = card_is_locked;
    }

    public int getCard_belongs_user() {
        return card_belongs_user;
    }

    public void setCard_belongs_user(int card_belongs_user) {
        this.card_belongs_user = card_belongs_user;
    }

    public int getCard_in_package() {
        return card_in_package;
    }

    public void setCard_in_package(int card_in_package) {
        this.card_in_package = card_in_package;
    }

    private int card_in_package;
}
