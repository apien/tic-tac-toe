package com.github.apien.tictactoe.api

import cats.effect.{ContextShift, IO}
import cats.implicits._
import com.github.apien.tictactoe.api.model._
import com.github.apien.tictactoe.domain.GameRepository.PlayerJoinError
import com.github.apien.tictactoe.domain.GameRepository.PlayerJoinError.{GameNoFreeSlot, GameNotExist}
import com.github.apien.tictactoe.domain.GameService
import com.github.apien.tictactoe.domain.model.{GameId, PlayerId}
import org.http4s.HttpRoutes
import sttp.model.StatusCode
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s._
import sttp.tapir.{statusMapping, _}
class GameApi(gamesService: GameService)(implicit cs: ContextShift[IO]) {

  private val addGame: Endpoint[Unit, Unit, GameApiDto, Nothing] = endpoint.post
    .description("Create a new game. It requires another user to join to start the play.")
    .in("api" / "games")
    .out(jsonBody[GameApiDto])

  val addGameRoutes: HttpRoutes[IO] = addGame.toRoutes { _ =>
    gamesService.createNew
      .map {
        case (gameId, playerId) =>
          GameApiDto(gameId.value, playerId.value).asRight[Unit]
      }
  }

  private val joinGame: Endpoint[GameId, PlayerJoinError, GameApiDto, Nothing] =
    endpoint.put
      .description("Allows to join to the existing game which awaits for second player")
      .in("api" / "games" / path[GameId].description("Game id to join") / "join")
      .out(jsonBody[GameApiDto].description("Game created"))
      .errorOut(
        oneOf[PlayerJoinError](
          statusMapping(StatusCode.NotFound, jsonBody[GameNotExist].description("Game does not exist")),
          statusMapping(StatusCode.BadRequest, jsonBody[GameNoFreeSlot].description("Game has not free slot"))
        )
      )

  val joinGameRoutes: HttpRoutes[IO] = joinGame.toRoutes { gameId =>
    gamesService
      .joinPlayer(gameId)
      .map {
        _.map(playerId => GameApiDto(gameId.value, playerId.value))
      }
  }

  private val makeMove: Endpoint[(GameId, PlayerId, CoordinateApiDto), MoveErrorApiDto, MoveSuccessApiDto, Nothing] = {
    endpoint.put
      .in("api" / "games" / path[GameId].description("It allows to make a move."))
      .in(header[PlayerId]("X-PLAYER-ID").description("Identifier of player received during game creation or joining to the game"))
      .in(jsonBody[CoordinateApiDto])
      .errorOut(
        oneOf[MoveErrorApiDto](
          statusMapping(
            StatusCode.Conflict,
            jsonBody[InvalidMoveErrorApiDto].description("Move can not make. For details please look on response.")
          ),
          statusMapping(
            StatusCode.BadRequest,
            jsonBody[InvalidInputData].description("Invalid input data. Coordinate must belong to the range <0,2>")
          )
        )
      )
      .out(
        oneOf[MoveSuccessApiDto](
          statusMapping(StatusCode.Ok, jsonBody[MoveSuccessApiDto].description("Move has been made."))
        )
      )
  }

  val makeMoveRoutes: HttpRoutes[IO] = makeMove.toRoutes {
    case (gameId, playerId, coordinateApiDto) =>
      coordinateApiDto.toDomain match {
        case None => IO(InvalidInputData().asLeft[MoveSuccessApiDto])
        case Some(coordinate) =>
          gamesService
            .move(gameId, playerId, coordinate)
            .map {
              case Left(error)   => InvalidMoveErrorApiDto(error).asLeft
              case Right(status) => MoveSuccessApiDto(status).asRight
            }
      }
  }

  private val getGame: Endpoint[GameId, GameNotExist, GameDetailsApiDto, Nothing] =
    endpoint.get
      .description("Get details of the game")
      .in("api" / "games" / path[GameId].description("Game id"))
      .errorOut(
        oneOf[GameNotExist] {
          statusMapping(
            StatusCode.NotFound,
            jsonBody[GameNotExist].description("Game does not exist")
          )
        }
      )
      .out(jsonBody[GameDetailsApiDto].description("Details of the game"))

  val getGameRoutes: HttpRoutes[IO] = getGame.toRoutes { gameId =>
    gamesService
      .getGame(gameId)
      .map { cos =>
        cos.fold(GameNotExist().asLeft[GameDetailsApiDto]) {
          case (game, moves) =>
            GameDetailsApiDto.fromDomain(game, moves).asRight[GameNotExist]
        }
      }
  }

  lazy val routes = addGameRoutes <+> joinGameRoutes <+> makeMoveRoutes <+> getGameRoutes
  lazy val descriptions = List(addGame, joinGame, makeMove, getGame)
}
