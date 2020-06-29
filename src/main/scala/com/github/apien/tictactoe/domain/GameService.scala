package com.github.apien.tictactoe.domain

import java.util.UUID

import cats.effect.IO
import cats.syntax.applicative._
import cats.syntax.either._
import com.github.apien.tictactoe.domain.GameRepository.PlayerJoinError
import com.github.apien.tictactoe.domain.GameRepository.PlayerJoinError.{GameNoFreeSlot, GameNotExist}
import com.github.apien.tictactoe.domain.model.{Game, GameId, PlayerId}
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor
class GameService(gameRepository: GameRepository, xa: Transactor[IO]) {

  /**
    * Create a new game.
    *
    * @return Game and player identifiers.
    */
  def createNew: IO[(GameId, PlayerId)] =
    for {
      game <- gameRepository
        .create(
          GameId(UUID.randomUUID().toString),
          PlayerId(UUID.randomUUID().toString)
        )
        .transact(xa)
    } yield (game.id, game.owner)

  def joinPlayer(gameId: GameId): IO[Either[PlayerJoinError, PlayerId]] = {
    val op = for {
      validationResult <- validateGame(gameId)
      playerId = PlayerId(UUID.randomUUID().toString)
      result <- validationResult match {
        case Right(game) =>
          gameRepository
            .joinPlayer(game.id, playerId)
            .map {
              _.fold[Either[PlayerJoinError, PlayerId]](GameNotExist().asLeft)(
                _ => playerId.asRight
              )
            }
        case Left(error) => error.asLeft[PlayerId].pure[ConnectionIO]
      }
    } yield result
    op.transact(xa)
  }

  private def validateGame(
      gameId: GameId
  ): ConnectionIO[Either[PlayerJoinError, Game]] =
    gameRepository
      .findById(gameId)
      .map {
        case Some(game) if game.guest.isEmpty => game.asRight
        case None                             => GameNotExist().asLeft
        case _                                => GameNoFreeSlot().asLeft
      }
}
