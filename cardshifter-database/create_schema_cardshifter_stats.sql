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
	create_date TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
	delete_date TIMESTAMP WITHOUT TIME ZONE NULL DEFAULT NULL,
	last_seen_date TIMESTAMP NULL
);
CREATE TABLE mod 
(
	id SERIAL PRIMARY KEY,
	name TEXT NOT NULL,
	description TEXT NULL,
	owner_player_id INT NOT NULL,
		FOREIGN KEY (owner_player_id) REFERENCES player(id),
	create_date TIMESTAMP WITHOUT TIME ZONE NULL DEFAULT NOW(),
	delete_date TIMESTAMP WITHOUT TIME ZONE NULL DEFAULT NULL
);
CREATE TABLE player_mod 
(
	player_id INT NOT NULL,
		FOREIGN KEY (player_id) REFERENCES player(id),
	mod_id INT NOT NULL,
		FOREIGN KEY (mod_id) REFERENCES mod(id)
);
CREATE TABLE card 
(
	id SERIAL PRIMARY KEY,
	version INT NOT NULL,
	name TEXT NULL,
	description TEXT NULL,
	effect_description TEXT NULL,
	type TEXT NULL,
	attack INT NULL,
	health INT NULL,
	mana_cost INT NULL,
	scrap_cost INT NULL,
	scrap_value INT NULL,
	sickness INT NULL,
	attack_available INT NULL,
	create_date TIMESTAMP WITHOUT TIME ZONE DEFAULT NOW(),
	delete_date TIMESTAMP WITHOUT TIME ZONE DEFAULT NULL
);
CREATE TABLE deck
(
	id SERIAL,
	version INT NOT NULL,
	player_id INT NOT NULL,
		FOREIGN KEY (player_id) REFERENCES player(id),
	mod_id INT NOT NULL,
		FOREIGN KEY (mod_id) REFERENCES mod(id),
	card_id INT NOT NULL,
		FOREIGN KEY (card_id) REFERENCES card(id),
	card_quantity INT NOT NULL
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