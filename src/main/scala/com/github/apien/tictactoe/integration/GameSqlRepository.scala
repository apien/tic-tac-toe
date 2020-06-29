package com.github.apien.tictactoe.integration

import com.github.apien.tictactoe.domain.model.{Game, GameId, PlayerId}
import com.github.apien.tictactoe.domain.{GameRepository, model}
import com.github.apien.tictactoe.integration.GameSqlRepository.GameQueries
import doobie._
import doobie.implicits._

/**
  * Implementation of [[GameRepository]] which persist data in PostgreSQL.
  */
class GameSqlRepository extends GameRepository {
  override def create(
      gameId: model.GameId,
      ownerId: model.PlayerId
  ): ConnectionIO[Game] =
    GameQueries
      .insert(gameId, ownerId)
      .withUniqueGeneratedKeys("id", "owner_id", "guest_id", "winner_id")

  override def findById(gameId: model.GameId): ConnectionIO[Option[Game]] = GameQueries.findById(gameId).option

  override def joinPlayer(
      gameId: model.GameId,
      guest: model.PlayerId
  ): ConnectionIO[Option[Game]] =
    GameQueries
      .joinPlayer(gameId, guest)
      .withGeneratedKeys[Game]("id", "owner_id", "guest_id", "winner_id")
      .head
      .compile
      .last

  override def getAll: ConnectionIO[List[Game]] = GameQueries.getAll.to[List]

  override def findByIdAndPlayerId(gameId: GameId, playerId: PlayerId): ConnectionIO[Option[Game]] =
    GameQueries
      .findByIdAndOwnerOrPlayer(gameId, playerId)
      .option

  override def setWinner(gameId: GameId, playerId: PlayerId): ConnectionIO[Option[Game]] =
    GameQueries
      .setWinner(gameId, playerId)
      .withGeneratedKeys[Game]("id", "owner_id", "guest_id", "winner_id")
      .head
      .compile
      .last
}

object GameSqlRepository {

  object GameQueries {

    val getAll: Query0[Game] = sql"select id, owner_id, guest_id, winner_id from  game".query[Game]

    def insert(gameId: GameId, ownerId: PlayerId): Update0 = sql"insert into game(id, owner_id) values ($gameId, $ownerId)".update

    def findById(gameId: GameId): Query0[Game] =
      sql"select id, owner_id, guest_id, winner_id from game where id = $gameId"
        .query[Game]

    def findByIdAndOwnerOrPlayer(gameId: GameId, playerId: PlayerId) =
      sql"""
           |select id, owner_id, guest_id, winner_id 
           |from game
           |where id = $gameId and (owner_id = $playerId or guest_id = $playerId)
           |""".stripMargin
        .query[Game]

    def joinPlayer(gameId: GameId, guestId: PlayerId): Update0 = sql"update game set guest_id = $guestId where id = $gameId".update

    def setWinner(gameId: GameId, playerId: PlayerId): Update0 =
      sql"""
           |update game 
           |set winner_id = $playerId
           |where id = $gameId
           |""".stripMargin.update
  }
}
