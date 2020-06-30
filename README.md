# tic-tac-toe
It provides simple TIC TAC TOE game through rest api.

An application allows to create a new game and join to previously created game.
The application does not provide authentication or authentication mechanism. Some security provides random token which represents
player id. User receives token aka player id when he creates or joins to the game.  
 

## Getting started

* Scala SBT [here](https://www.scala-sbt.org/)
* Java 1.9
* Docker version 19.03.12

## Run 

### Set up docker container with PostgreSQL and apply migrations

1. Go to `/bin` directory.
2. Execute `set_up_local_env.sh` script which set up a docker container with PostgreSQL and apply schema migrations.

### Run application

Directory `/bin` contains the bash script `run.sh` to run a project with development configuration. It runs application on port 8080. 


## Swagger
Application provides documentation of the Rest Api as Swagger. You can find it under the url `/docs` i.e: `localhost:8080/docs`.

## Application configuration

|env variable|system property|description|default value|example value|
|---|---|---|---|---|
| DB_HOST | db.host | Host to the database where works PostgreSQL. | - | localhost |
| DB_PORT | db.port | Port to PostgreSQL | - | 5432 |
| DB_USER | db.user | User name to PostgreSQL | - | john.doe |
| DB_PASSWORD | db.password | PostgreSQL user's name | - | tajne |
| DB_DATABASE | db.database | PostgreSQL database name | - | ttc |



## TODO
- Introduce tagged types and new type to represent configuration and domain objects
- Make constrains on the database
- Write back tests for the service logic
- Set up a CI pipeline
- Use Chimney to convert between domain api dto objects
