START TRANSACTION;

DROP SCHEMA IF EXISTS cardshifter_stats CASCADE;
CREATE SCHEMA cardshifter_stats;
SET SEARCH_PATH TO cardshifter_stats;

CREATE TABLE player 
(
	id SERIAL PRIMARY KEY UNIQUE,
	name TEXT NOT NULL,
	password TEXT NOT NULL, -- this will be made secure once implemented
	email TEXT,
	website TEXT NULL,
	about TEXT NULL,
	create_time TIMESTAMP NOT NULL DEFAULT NOW(),
	delete_time TIMESTAMP NULL DEFAULT NULL,
	last_seen_time TIMESTAMP NULL DEFAULT NULL
);
CREATE UNIQUE INDEX player_name_idx ON player(name);
CREATE TABLE mod 
(
	id SERIAL PRIMARY KEY UNIQUE,
	name TEXT NOT NULL,
	version INT NOT NULL, -- incremented when modified
	description TEXT NULL,
	owner_player_id INT NOT NULL,
		FOREIGN KEY (owner_player_id) REFERENCES player(id),
	create_time TIMESTAMP NOT NULL DEFAULT NOW(),
	delete_time TIMESTAMP NULL DEFAULT NULL
);
CREATE UNIQUE INDEX mod_name_idx ON mod(name);
CREATE TABLE player_mod 
(
	player_id INT NOT NULL,
		FOREIGN KEY (player_id) REFERENCES player(id),
	mod_id INT NOT NULL,
		FOREIGN KEY (mod_id) REFERENCES mod(id)
);
CREATE TABLE card 
(
	id SERIAL PRIMARY KEY UNIQUE,
	version INT NOT NULL, -- incremented when modified
	name TEXT NULL,
	flavor_text TEXT NULL,
	effect_description TEXT NULL,
	type TEXT NULL,
--		FOREIGN KEY (type) REFERENCES card_type(name),
	attack INT NULL,
	health INT NULL,
--	mana_cost INT NULL,
--	scrap_cost INT NULL,
--	scrap_value INT NULL,
	sickness INT NULL,
	attack_available INT NULL,
	create_time TIMESTAMP NOT NULL DEFAULT NOW(),
	delete_time TIMESTAMP NULL DEFAULT NULL
);
CREATE INDEX card_name_idx ON card(name);
CREATE TABLE deck
(
	id SERIAL PRIMARY KEY UNIQUE,
	player_id INT NOT NULL,
		FOREIGN KEY (player_id) REFERENCES player(id),
	mod_id INT NOT NULL,
		FOREIGN KEY (mod_id) REFERENCES mod(id),
	name TEXT NOT NULL
);
CREATE TABLE deck_card
(
	deck_id INT,
		FOREIGN KEY (deck_id) REFERENCES deck(id),
	version INT NOT NULL, 	-- incremented when modified
	card_id INT NOT NULL,
		FOREIGN KEY (card_id) REFERENCES card(id),
	card_quantity INT NOT NULL
);
CREATE TABLE game
(
	id SERIAL UNIQUE,
	mod_id INT NOT NULL,
		FOREIGN KEY (mod_id) REFERENCES mod(id),
	start_time TIMESTAMP NOT NULL
);
CREATE TABLE game_player
(
	game_id INT NOT NULL,
		FOREIGN KEY (game_id) REFERENCES game(id),
	player_id INT NOT NULL,
		FOREIGN KEY (player_id) REFERENCES player(id),
	player_deck INT NOT NULL,
		FOREIGN KEY (player_deck) REFERENCES deck(id),
	game_rank INT NOT NULL
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