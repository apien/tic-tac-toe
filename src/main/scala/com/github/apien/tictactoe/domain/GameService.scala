package com.github.apien.tictactoe.domain

import java.time.LocalDateTime
import java.util.UUID

import cats.effect.IO
import cats.syntax.applicative._
import cats.syntax.either._
import com.github.apien.tictactoe.domain.GameEngine.MoveError.GameDoesNotExist
import com.github.apien.tictactoe.domain.GameEngine.{MoveError, SuccessMove}
import com.github.apien.tictactoe.domain.GameRepository.PlayerJoinError
import com.github.apien.tictactoe.domain.GameRepository.PlayerJoinError.{GameNoFreeSlot, GameNotExist}
import com.github.apien.tictactoe.domain.model._
import doobie._
import doobie.implicits._
import doobie.util.transactor.Transactor
class GameService(gameRepository: GameRepository, moveRepository: MoveRepository, xa: Transactor[IO]) {

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
      validationResult <- validateGameNewPlayer(gameId)
      playerId = PlayerId(UUID.randomUUID().toString)
      result <- validationResult match {
        case Right(game) =>
          gameRepository
            .joinPlayer(game.id, playerId)
            .map {
              _.fold[Either[PlayerJoinError, PlayerId]](GameNotExist().asLeft)(_ => playerId.asRight)
            }
        case Left(error) => error.asLeft[PlayerId].pure[ConnectionIO]
      }
    } yield result
    op.transact(xa)
  }

  /**
    * Player make a move.
    * @param gameId Identifier of the game.
    * @param playerId Identifier of the player.
    * @param coordinate Cordinate of the user's move.
    * @return Result of the move.
    */
  def move(gameId: GameId, playerId: PlayerId, coordinate: Coordinate): IO[Either[MoveError, SuccessMove]] = {

    val t1 = gameRepository
      .findByIdAndPlayerId(gameId, playerId)
      .flatMap {
        case None =>
          (GameDoesNotExist(): MoveError).asLeft[SuccessMove].pure[ConnectionIO]
        case Some(game) =>
          for {
            gameMoves <- moveRepository.getAllForGame(gameId)
            engine = new GameEngine(game, gameMoves)
            move = Move(playerId, coordinate, LocalDateTime.now())
            moveResult = engine.makeMove(move)
            result <- moveResult match {
              case Left(error)      => error.asLeft[SuccessMove].pure[ConnectionIO]
              case Right(gameState) => handleSuccessMove(gameId, move, gameState).map(_ => gameState.asRight[MoveError])
            }
          } yield result
      }

    t1.transact(xa)

  }

  /**
    * Handle successfully made move so it is going insert it to db.
    *
    * In case when game is finished then also persist winner of the game.
    * @param gameId Game identifier.
    * @param move Move which has been validated and is ok.
    * @param result Result of the game.
    * @return -.
    */
  private def handleSuccessMove(gameId: GameId, move: Move, result: SuccessMove): ConnectionIO[Unit] =
    for {
      _ <- moveRepository.insert(move)
      _ <- result match {
        case SuccessMove.GameFinished(playerId) => gameRepository.setWinner(gameId, playerId)
        case SuccessMove.GameInProgress()         => ().pure[ConnectionIO]
      }
    } yield ()

  /**
    * Check if user can join to the game.
    * @param gameId Game id.
    * @return Right if another user can join to the game.
    */
  private def validateGameNewPlayer(gameId: GameId): ConnectionIO[Either[PlayerJoinError, Game]] =
    gameRepository
      .findById(gameId)
      .map {
        case Some(game) if game.guest.isEmpty => game.asRight
        case None                             => GameNotExist().asLeft
        case _                                => GameNoFreeSlot().asLeft
      }
}
