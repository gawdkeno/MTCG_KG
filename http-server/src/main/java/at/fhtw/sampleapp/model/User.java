package at.fhtw.sampleapp.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

public class User {
    private int player_id;
    @JsonAlias({"Username"})
    private String player_username;
    @JsonAlias({"Password"})
    private String player_password;
    private int player_coins;
    @JsonAlias({"Bio"})
    private String player_bio;
    @JsonAlias({"Image"})
    private String player_image;
    @JsonAlias({"Name"})
    private String player_name;
    private String player_token;

    public User() {

    }
    public User(String player_username, int player_coins, String player_bio, String player_image, String player_name) {
        this.player_username = player_username;
        this.player_coins = player_coins;
        this.player_bio = player_bio;
        this.player_image = player_image;
        this.player_name = player_name;
}

    public void setPlayer_id(int player_id) {
        this.player_id = player_id;
    }

    public String getPlayer_username() {
        return player_username;
    }

    public void setPlayer_username(String player_username) {
        this.player_username = player_username;
    }

    public String getPlayer_password() {
        return player_password;
    }

    public void setPlayer_password(String player_password) {
        this.player_password = player_password;
    }

    public User(String username, String password) {
        this.player_username = username;
        this.player_password = password;
    }

    public int getPlayer_id() {
        return player_id;
    }

//    public void setPlayer_id(int player_id) {
//        this.player_id = player_id;
//    }

    public int getPlayer_coins() {
        return player_coins;
    }

    public void setPlayer_coins(int player_coins) {
        this.player_coins = player_coins;
    }

    public String getPlayer_bio() {
        return player_bio;
    }

    public void setPlayer_bio(String player_bio) {
        this.player_bio = player_bio;
    }

    public String getPlayer_image() {
        return player_image;
    }

    public void setPlayer_image(String player_image) {
        this.player_image = player_image;
    }

    public String getPlayer_name() {
        return player_name;
    }

    public void setPlayer_name(String player_name) {
        this.player_name = player_name;
    }

    public String getPlayer_token() {
        return player_token;
    }

    public void setPlayer_token(String player_Token) {
        this.player_token = player_Token;
    }
}
