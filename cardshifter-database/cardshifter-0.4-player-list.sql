-- SQL insert query
START TRANSACTION; SET SEARCH_PATH TO cardshifter_stats; DELETE FROM player;
INSERT INTO player(name,password,email,website,about,create_date,delete_date,last_seen_date) VALUES
('Zomis','*****',null,null,null,null,null,null),
('bazola','*****',null,null,null,null,null,null),
('skiwi','*****',null,null,null,null,null,null),
('Phrancis','*****',null,null,null,null,null,null),
('GreyFox','*****',null,null,null,null,null,null),
('Jay1148','*****',null,null,null,null,null,null);
COMMIT;