CREATE DATABASE ttc;

CREATE user rest_api WITH encrypted password 'tajne';

GRANT USAGE ON SCHEMA public TO rest_api;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO rest_api;
GRANT INSERT ON ALL TABLES IN SCHEMA public TO rest_api;
GRANT ALL ON ALL TABLES IN SCHEMA public TO rest_api;

