package com.github.apien.tictactoe.integration

import cats.syntax.option._
import com.github.apien.tictactoe.domain.GameRepository
import com.github.apien.tictactoe.domain.model.{Game, GameId, PlayerId}
import monix.eval.Task

import scala.collection.mutable

class GameInMemoryRepository(games: mutable.Map[GameId, Game]) extends GameRepository {


  override def create(gameId: GameId, ownerId: PlayerId): Task[Game] = Task {
    val game = Game(gameId, ownerId, None)
    games += game.id -> game
    game
  }

  override def getAll: Task[List[Game]] = Task(games.values.toList)
}