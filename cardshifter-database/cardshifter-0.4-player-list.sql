-- SQL insert query
START TRANSACTION; SET SEARCH_PATH TO cardshifter_stats; 
DROP TABLE IF EXISTS player CASCADE;
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
INSERT INTO player(name,password,email,website,about,delete_time,last_seen_time) VALUES
('Zomis','*****',null,null,null,null,null),
('bazola','*****',null,null,null,null,null),
('skiwi','*****',null,null,null,null,null),
('Phrancis','*****',null,null,null,null,null),
('GreyFox','*****',null,null,null,null,null),
('Jay1148','*****',null,null,null,null,null); 
COMMIT;

SELECT * FROM player;