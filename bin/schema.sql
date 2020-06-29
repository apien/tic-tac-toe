CREATE DATABASE ttc;

CREATE user rest_api WITH encrypted password 'tajne';

GRANT USAGE ON SCHEMA public TO rest_api;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO rest_api;
GRANT INSERT ON ALL TABLES IN SCHEMA public TO rest_api;
GRANT ALL ON ALL TABLES IN SCHEMA public TO rest_api;

CREATE TABLE IF NOT EXISTS game (
	id       TEXT PRIMARY KEY,
	owner_id TEXT NOT NULL,
	guest_id TEXT,
	winner_id TEXT
);


CREATE TABLE IF NOT EXISTS move (
	row       int       NOT NULL,
	col       int       NOT NULL,
	player_id TEXT      NOT NULL,
	date_time TIMESTAMP WITHOUT TIME ZONE NOT NULL
);