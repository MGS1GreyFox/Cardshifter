START TRANSACTION;

DROP SCHEMA IF EXISTS cardshifter_stats CASCADE;
CREATE SCHEMA cardshifter_stats;
SET SEARCH_PATH TO cardshifter_stats;

CREATE TABLE player 
(
	id SERIAL PRIMARY KEY,
	name TEXT NOT NULL,
	password TEXT NOT NULL, -- this will be made secure once implemented
	email TEXT,
	website TEXT NULL,
	about TEXT NULL,
	create_date TIMESTAMP NOT NULL DEFAULT NOW(),
	delete_date TIMESTAMP NULL DEFAULT NULL,
	last_seen_date TIMESTAMP NULL DEFAULT NULL
);
CREATE UNIQUE INDEX player_name_idx ON player(name);
CREATE TABLE mod 
(
	id SERIAL PRIMARY KEY,
	name TEXT NOT NULL,
	version INT NOT NULL, -- incremented when modified
	description TEXT NULL,
	owner_player_id INT NOT NULL,
		FOREIGN KEY (owner_player_id) REFERENCES player(id),
	create_date TIMESTAMP NOT NULL DEFAULT NOW(),
	delete_date TIMESTAMP NULL DEFAULT NULL
);
CREATE UNIQUE INDEX mod_name_idx ON mod(name);
CREATE TABLE player_mod 
(
	player_id INT NOT NULL,
		FOREIGN KEY (player_id) REFERENCES player(id),
	mod_id INT NOT NULL,
		FOREIGN KEY (mod_id) REFERENCES mod(id)
);
CREATE TABLE card_type
(
	name TEXT PRIMARY KEY
);
INSERT INTO card_type (name) VALUES
	('B0T'),
	('Bio'),
	('Enhancement');
CREATE TABLE card 
(
	id SERIAL PRIMARY KEY,
	version INT NOT NULL, -- incremented when modified
	name TEXT NULL,
	description TEXT NULL,
	effect_description TEXT NULL,
	type TEXT NULL,
		FOREIGN KEY (type) REFERENCES card_type(name),
	attack INT NULL,
	health INT NULL,
	mana_cost INT NULL,
	scrap_cost INT NULL,
	scrap_value INT NULL,
	sickness INT NULL,
	attack_available INT NULL,
	create_date TIMESTAMP NOT NULL DEFAULT NOW(),
	delete_date TIMESTAMP NULL DEFAULT NULL
);
CREATE INDEX card_name_idx ON card(name);
CREATE TABLE deck
(
	id SERIAL PRIMARY KEY,
	player_id INT NOT NULL,
		FOREIGN KEY (player_id) REFERENCES player(id),
	mod_id INT NOT NULL,
		FOREIGN KEY (mod_id) REFERENCES mod(id)
);
CREATE TABLE deck_card
(
	deck_id INT,
		FOREIGN KEY (deck_id) REFERENCES deck(id),
	version INT NOT NULL, -- incremented when modified
	card_id INT NOT NULL,
		FOREIGN KEY (card_id) REFERENCES card(id),
	card_quantity INT NOT NULL
);
CREATE TABLE game_master
(
	game_id SERIAL PRIMARY KEY,
	player1 INT,
		FOREIGN KEY (player1) REFERENCES player(id),
	player2 INT,
		FOREIGN KEY (player2) REFERENCES player(id),
	start_time TIMESTAMP NOT NULL DEFAULT NOW(),
	end_time TIMESTAMP NULL DEFAULT NULL
);
CREATE TABLE game_actions
(
	game_id INT NOT NULL,
		FOREIGN KEY (game_id) REFERENCES game_master(game_id),
	turn INT NOT NULL,
	action_player INT NOT NULL,
		FOREIGN KEY (action_player) REFERENCES player(id),
	action_card INT NULL,
		FOREIGN KEY (action_card) REFERENCES card(id),
	action_attack INT NULL,
	target_player INT NULL,
		FOREIGN KEY (action_player) REFERENCES player(id),
	target_card INT NULL,
		FOREIGN KEY (action_player) REFERENCES player(id),
	action_time TIMESTAMP NOT NULL
);

COMMIT;

SELECT
	tables.table_schema,
	tables.table_name,
	columns.column_name,
	columns.ordinal_position,
	columns.is_nullable,
	columns.data_type
FROM information_schema.tables AS tables
	INNER JOIN information_schema.columns AS columns
		ON tables.table_name = columns.table_name
WHERE 
	tables.table_schema = 'cardshifter_stats'
ORDER BY 
	tables.table_name ASC,
	columns.ordinal_position ASC;