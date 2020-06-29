package com.github.apien.tictactoe.api

import cats.effect.IO
import com.github.apien.tictactoe.domain.GameService
import com.github.apien.tictactoe.test.TtcSpec
import io.circe.Json
import io.circe.literal._
import org.http4s.circe._
import org.http4s.implicits._
import org.http4s.{Method, Request, Status}
import org.scalamock.scalatest.MockFactory

class GameApiSpec extends TtcSpec with MockFactory {

  "GameRoutes create game" should "response OK with proper object" in {
    val gameServiceMock = mock[GameService]
    (gameServiceMock.createNew _).expects().returning(IO("game1", "player1"))
    val routes = new GameApi(gameServiceMock)

    val response = routes.addGameRoutes.orNotFound
      .run(Request(method = Method.POST, uri = uri"/api/games"))
      .unsafeRunSync()

    response.status shouldBe Status.Ok
    response.as[Json].unsafeRunSync() shouldBe
      json"""
          {
            "gameId":"game1",
            "playerId":"player1"
          }
          """
  }
}
