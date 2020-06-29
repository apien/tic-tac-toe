package com.github.apien.tictactoe.api

import cats.effect.{ContextShift, IO}
import cats.syntax.either._
import com.github.apien.tictactoe.api.model.{CoordinateApiDto, GameApiDto, MoveErrorApiDto}
import com.github.apien.tictactoe.domain.GameEngine.{MoveError, SuccessMove}
import com.github.apien.tictactoe.domain.GameRepository.PlayerJoinError
import com.github.apien.tictactoe.domain.GameRepository.PlayerJoinError.{GameNoFreeSlot, GameNotExist}
import com.github.apien.tictactoe.domain.GameService
import com.github.apien.tictactoe.domain.model.{GameId, PlayerId}
import org.http4s.HttpRoutes
import sttp.model.StatusCode
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s._

class GameApi(gamesService: GameService)(implicit cs: ContextShift[IO]) {

  val addGame: Endpoint[Unit, Unit, GameApiDto, Nothing] = endpoint.post
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

  val joinGame: Endpoint[GameId, PlayerJoinError, GameApiDto, Nothing] =
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

  val makeMove: Endpoint[(GameId, PlayerId, CoordinateApiDto), Unit, Unit, Nothing] =
    endpoint.put
      .in("api" / "games" / path[GameId].description("Game id to join"))
      .in(header[PlayerId]("X-PLAYER-ID").description("Identifier of player received during game creation or joining to the game"))
      .in(jsonBody[CoordinateApiDto])
      //      .errorOut(
      //        oneOf[MoveError](
      //          statusMapping(StatusCode.Conflict, jsonBody[MoveError.NotPlayerTurn].description("It is turn of second player"))
      ////          statusMapping(StatusCode.Conflict, jsonBody[MoveError.NotPlayerTurn].description("It is turn of second player"))
      ////          statusMapping(StatusCode.BadRequest, jsonBody[MoveError.GameDoesNotExist].description("Game has not free slot"))
      //        )
      //      )
//      .errorOutput(
//        oneOf[MoveErrorApiDto](
//          statusMapping(StatusCode.Conflict, jsonBody[MoveErrorApiDto].description("It is turn of second player"))
//        )
//      )

  val makeMoveRoutes = makeMove.toRoutes {
    case (gameId, playerId, coordinateApiDto) =>
      gamesService
        .move(gameId, playerId, coordinateApiDto.toDomain)
        .map {
//          case Left(error) => MoveErrorApiDto(error).asLeft
          case Left(error) => ().asLeft
          case Right(s)    => ().asRight
        }
  }
}
