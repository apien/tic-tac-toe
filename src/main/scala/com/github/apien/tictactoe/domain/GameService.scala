package com.github.apien.tictactoe.domain

import com.github.apien.tictactoe.domain.model.{Game, GameId, PlayerId}
import monix.eval.Task
import java.util.UUID

import cats.syntax.either._
import com.github.apien.tictactoe.domain.GameRepository.PlayerJoinError
import com.github.apien.tictactoe.domain.GameRepository.PlayerJoinError.{GameNoFreeSlot, GameNotExist}
import monix.execution.Scheduler;

class GameService(gameRepository: GameRepository) {

  /**
    * Create a new game.
    *
    * @return Game and player identifiers.
    */
  def createNew: Task[(GameId, PlayerId)] =
    for {
      game <- gameRepository.create(GameId(UUID.randomUUID().toString), PlayerId(UUID.randomUUID().toString))
    } yield (game.id, game.owner)

  def joinPlayer(gameId: GameId)(implicit s: Scheduler): Task[Either[PlayerJoinError, PlayerId]] =
    for {
      validationResult <- validateGame(gameId)
      playerId = PlayerId(UUID.randomUUID().toString)
      result <- validationResult match {
        case Right(game) => gameRepository
          .joinPlayer(game.id, playerId)
          .map {
            _.fold[Either[PlayerJoinError, PlayerId]](GameNotExist().asLeft)(_ => playerId.asRight)
          }
        case Left(error) => Task(error.asLeft[PlayerId])
      }
    } yield result


  private def validateGame(gameId: GameId): Task[Either[PlayerJoinError, Game]] =
    gameRepository
      .findById(gameId)
      .map {
        case Some(game) if game.guest.isEmpty => game.asRight
        case None => GameNotExist().asLeft
        case _ => GameNoFreeSlot().asLeft
      }
}
