package at.fhtw.sampleapp.model;

public class Battle {
    private int battle_id;
    private int battle_player_a_id = -1;
    private int battle_player_b_id = -1;
    private boolean battle_finished;
    private int battle_winner_id;

    public Battle() {

    }

    public int getBattle_id() {
        return battle_id;
    }

    public void setBattle_id(int battle_id) {
        this.battle_id = battle_id;
    }

    public int getBattle_player_a_id() {
        return battle_player_a_id;
    }

    public void setBattle_player_a_id(int battle_player_a_id) {
        this.battle_player_a_id = battle_player_a_id;
    }

    public int getBattle_player_b_id() {
        return battle_player_b_id;
    }

    public void setBattle_player_b_id(int battle_player_b_id) {
        this.battle_player_b_id = battle_player_b_id;
    }

    public boolean isBattle_finished() {
        return battle_finished;
    }

    public void setBattle_finished(boolean battle_finished) {
        this.battle_finished = battle_finished;
    }

    public int getBattle_winner_id() {
        return battle_winner_id;
    }

    public void setBattle_winner_id(int battle_winner_id) {
        this.battle_winner_id = battle_winner_id;
    }
}
