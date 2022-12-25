package at.fhtw.sampleapp.model;

import com.fasterxml.jackson.annotation.JsonAlias;

public class User {

    public User() {

    }
    private int player_id;
    @JsonAlias({"Username"})
    private String player_username;
    @JsonAlias({"Password"})
    private String player_password;

    public User(int player_id, String player_username, String player_password) {
        this.player_id = player_id;
        this.player_username = player_username;
        this.player_password = player_password;
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

}
