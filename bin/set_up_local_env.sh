#!/usr/bin/env bash

echo "# Please wait a minute to set up a docker with Postgres"
docker-compose up -d
sleep 30
echo "# Docker set up"

cd ../

echo "# Apply schema migrations"
sbt -Dflyway.url=jdbc:postgresql://localhost:15432/postgres \
    -Dflyway.user=postgres \
    -Dflyway.password=tajne \
    flywayMigrate
