package com.github.apien.tictactoe.api

import cats.effect.ContextShift
import cats.syntax.either._
import com.github.apien.tictactoe.api.model.GameApiDto
import com.github.apien.tictactoe.domain.GameService
import monix.eval.Task
import org.http4s.HttpRoutes
import sttp.tapir._
import sttp.tapir.json.circe._
import sttp.tapir.server.http4s._

class GameApi(gamesService: GameService)(implicit cs: ContextShift[Task]) {

  val addGame: Endpoint[Unit, Unit, GameApiDto, Nothing] = endpoint
    .post
    .description("Create a new game. It requires another user to join to start the play.")
    .in("api" / "games")
    .out(jsonBody[GameApiDto])

  val addGameRoutes: HttpRoutes[Task] = addGame.toRoutes { _ =>
    gamesService
      .createNew
      .map { case (gameId, playerId) => GameApiDto(gameId.value, playerId.value).asRight[Unit] }
  }
}