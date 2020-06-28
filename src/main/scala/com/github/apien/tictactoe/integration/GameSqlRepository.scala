package com.github.apien.tictactoe.integration

import com.github.apien.tictactoe.domain.model.Game
import com.github.apien.tictactoe.domain.{GameRepository, model}
import doobie._
import doobie.implicits._

/**
  * Implementation of [[GameRepository]] which persist data in PostgreSQL.
  */
class GameSqlRepository extends GameRepository {
  def create(
      gameId: model.GameId,
      ownerId: model.PlayerId
  ): ConnectionIO[Game] =
    sql"insert into game(id, owner_id) values ($gameId, $ownerId)".update
      .withUniqueGeneratedKeys("id", "owner_id", "guest_id")

  def findById(gameId: model.GameId): ConnectionIO[Option[Game]] =
    sql"select id, owner_id, guest_id from game where id = $gameId"
      .query[Game]
      .option

  def joinPlayer(
      gameId: model.GameId,
      guest: model.PlayerId
  ): ConnectionIO[Option[Game]] =
    sql"update game set guest_id = $guest where id = $gameId".update
      .withGeneratedKeys[Game]("id", "owner_id", "guest_id")
      .head
      .compile
      .last

  def getAll: ConnectionIO[List[Game]] =
    sql"select id, owner_id, guest_id from  game"
      .query[Game]
      .to[List]
}
