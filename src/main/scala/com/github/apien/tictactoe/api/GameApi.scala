package com.github.apien.tictactoe.api

import cats.effect.{ContextShift, IO}
import cats.syntax.either._
import com.github.apien.tictactoe.api.model.GameApiDto
import com.github.apien.tictactoe.domain.GameRepository.PlayerJoinError
import com.github.apien.tictactoe.domain.GameRepository.PlayerJoinError.{GameNoFreeSlot, GameNotExist}
import com.github.apien.tictactoe.domain.GameService
import com.github.apien.tictactoe.domain.model.GameId
import io.circe.generic.semiauto._
import org.http4s.HttpRoutes
import sttp.model.StatusCode
import sttp.tapir.Codec.PlainCodec
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s._

class GameApi(gamesService: GameService)(implicit cs: ContextShift[IO]) {

  val addGame: Endpoint[Unit, Unit, GameApiDto, Nothing] = endpoint.post
    .description(
      "Create a new game. It requires another user to join to start the play.")
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
      .description(
        "Allows to join to the existing game which awaits for second player")
      .in(
        "api" / "games" / path[GameId].description("Game id to join") / "join")
      .out(jsonBody[GameApiDto].description("Game created"))
      .errorOut(
        oneOf[PlayerJoinError](
          statusMapping(
            StatusCode.NotFound,
            jsonBody[GameNotExist].description("Game does not exist")),
          statusMapping(
            StatusCode.BadRequest,
            jsonBody[GameNoFreeSlot].description("Game has not free slot"))
        )
      )

  val joinGameRoutes: HttpRoutes[IO] = joinGame.toRoutes { gameId =>
    gamesService
      .joinPlayer(gameId)
      .map {
        _.map(playerId => GameApiDto(gameId.value, playerId.value))
      }
  }
}
