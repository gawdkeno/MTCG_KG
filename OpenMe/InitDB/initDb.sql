DROP TABLE IF EXISTS player, package, card, trading, battle, battle_round ;

CREATE TABLE IF NOT EXISTS player (
    player_id               SERIAL PRIMARY KEY,
    player_username         VARCHAR(255) NOT NULL,
    player_password         TEXT NOT NULL,
    player_coins            INT NOT NULL DEFAULT 20,
    player_token            VARCHAR(255),
    player_bio              VARCHAR(255) DEFAULT 'No bio yet...',
    player_image            VARCHAR(255) DEFAULT 'No image yet...',
    player_name             VARCHAR(255) DEFAULT 'Firstname, Lastname',
    player_total_battles    INT DEFAULT 0,
    player_wins             INT DEFAULT 0,
    player_losses           INT DEFAULT 0,
    player_elo              INT DEFAULT 500
);

CREATE TABLE IF NOT EXISTS package(
    package_id      SERIAL PRIMARY KEY,
    package_name    VARCHAR(255),
    package_price   INT
);

CREATE TABLE IF NOT EXISTS card(
    card_id             SERIAL PRIMARY KEY,
    card_code_id        VARCHAR(255),
    card_name           VARCHAR(255),
    card_dmg            INT,
    card_element        VARCHAR(10),
    card_type           VARCHAR(10),
    card_in_deck        BOOLEAN DEFAULT FALSE,
    card_player_id      INT,
    CONSTRAINT      fk_player
        FOREIGN KEY (card_player_id)
            REFERENCES player(player_id),
    card_package_id     INT,
    CONSTRAINT      fk_package
        FOREIGN KEY (card_package_id)
            REFERENCES package(package_id)
    ON DELETE SET NULL
);

CREATE TABLE IF NOT EXISTS trading (
    trading_id          SERIAL PRIMARY KEY,
    trading_card_a      INT,
    CONSTRAINT fk_card_a
        FOREIGN KEY (trading_card_a)
            REFERENCES card(card_id),
    trading_card_b      INT,
    CONSTRAINT fk_cardB
        FOREIGN KEY (trading_card_b)
            REFERENCES card(card_id),
    trading_coins       INT,
    trading_accepted    BOOLEAN
);

CREATE TABLE IF NOT EXISTS battle (
    battle_id           SERIAL PRIMARY KEY,
    battle_player_a_id  INT DEFAULT NULL,
    CONSTRAINT fk_player_a
        FOREIGN KEY (battle_player_a_id)
            REFERENCES player(player_id),
    battle_player_b_id  INT DEFAULT NULL,
    CONSTRAINT fk_player_b
        FOREIGN KEY (battle_player_b_id)
            REFERENCES player(player_id),
    battle_finished     BOOLEAN DEFAULT FALSE,
    battle_winner_id    INT DEFAULT NULL,
    CONSTRAINT fk_player
        FOREIGN KEY (battle_winner_id)
            REFERENCES player(player_id)
);

CREATE TABLE IF NOT EXISTS battle_round (
    battle_round_id             SERIAL PRIMARY KEY,
    battle_round_battle_id      INT,
    CONSTRAINT fk_battle
        FOREIGN KEY (battle_round_battle_id)
            REFERENCES battle(battle_id),
    battle_round_card_a_id         INT,
    CONSTRAINT fk_card_a
        FOREIGN KEY (battle_round_card_a_id)
            REFERENCES card(card_id),
    battle_round_card_b_id         INT,
    CONSTRAINT fk_card_b
        FOREIGN KEY (battle_round_card_b_id)
            REFERENCES card(card_id),
    battle_round_winner_card_id    INT DEFAULT NULL
);