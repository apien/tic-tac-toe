package com.github.apien.tictactoe.integration

import cats.syntax.applicative._
import cats.syntax.option._
import com.github.apien.tictactoe.domain.GameRepository
import com.github.apien.tictactoe.domain.model.{Game, GameId, PlayerId}
import doobie.ConnectionIO
import doobie.implicits._

import scala.collection.mutable

class GameInMemoryRepository(games: mutable.Map[GameId, Game])
    extends GameRepository {

  def create(gameId: GameId, ownerId: PlayerId): ConnectionIO[Game] = {
    val game = Game(gameId, ownerId, None)
    games += game.id -> game
    game
  }.pure[ConnectionIO]

  def joinPlayer(gameId: GameId, guest: PlayerId): ConnectionIO[Option[Game]] = {
    games
      .get(gameId)
      .map(game => game.copy(guest = guest.some))
      .map { updatedGame =>
        games.update(gameId, updatedGame)
        updatedGame
      }
  }.pure[ConnectionIO]

  def getAll: ConnectionIO[List[Game]] = games.values.toList.pure[ConnectionIO]

  def findById(gameId: GameId): ConnectionIO[Option[Game]] =
    games.get(gameId).pure[ConnectionIO]
}
