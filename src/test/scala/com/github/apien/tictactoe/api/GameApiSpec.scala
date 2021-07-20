package com.github.apien.tictactoe.api

import cats.syntax.either._
import cats.effect.IO
import com.github.apien.tictactoe.domain.GameEngine.{MoveError, SuccessMove}
import com.github.apien.tictactoe.domain.GameService
import com.github.apien.tictactoe.domain.model.{Column, Coordinate, Game, GameId, Move, PlayerId, Row}
import com.github.apien.tictactoe.test.TtcSpec
import io.circe.Json
import io.circe.literal._
import org.http4s.circe._
import org.http4s.implicits._
import org.http4s._
import org.scalamock.scalatest.MockFactory
import cats.syntax.option._

class GameApiSpec extends TtcSpec with MockFactory {

  "GameApi create game" should "response OK with proper object" in {
    val gameServiceMock = mock[GameService]
    (() => gameServiceMock.createNew).expects().returning(IO("game1", "player1"))
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

  "GameApi make move" should "response 400 when row is above 2" in {
    val routes = new GameApi(mock[GameService])

    val response = routes.makeMoveRoutes.orNotFound
      .run(
        Request(
          method = Method.PUT,
          uri = uri"/api/games/g1",
          headers = Headers.of(Header("X-PLAYER-ID", "p1"))
        ).withEntity(json"""{"row":3, "col": 1}""")
      )
      .unsafeRunSync()

    response.status shouldBe Status.BadRequest
  }

  it should "response 400 when row is below 0" in {
    val routes = new GameApi(mock[GameService])

    val response = routes.makeMoveRoutes.orNotFound
      .run(
        Request(
          method = Method.PUT,
          uri = uri"/api/games/g1",
          headers = Headers.of(Header("X-PLAYER-ID", "p1"))
        ).withEntity(json"""{"row":-2, "col": 1}""")
      )
      .unsafeRunSync()

    response.status shouldBe Status.BadRequest
  }

  it should "response 400 when col is below 0" in {
    val routes = new GameApi(mock[GameService])

    val response = routes.makeMoveRoutes.orNotFound
      .run(
        Request(
          method = Method.PUT,
          uri = uri"/api/games/g1",
          headers = Headers.of(Header("X-PLAYER-ID", "p1"))
        ).withEntity(json"""{"row":2, "col": -56}""")
      )
      .unsafeRunSync()

    response.status shouldBe Status.BadRequest
  }

  it should "response 400 when col is above 2" in {
    val routes = new GameApi(mock[GameService])

    val response = routes.makeMoveRoutes.orNotFound
      .run(
        Request(
          method = Method.PUT,
          uri = uri"/api/games/g1",
          headers = Headers.of(Header("X-PLAYER-ID", "p1"))
        ).withEntity(json"""{"row":1, "col": 6}""")
      )
      .unsafeRunSync()

    response.status shouldBe Status.BadRequest
  }

  it should "call service when row and col is in range <0,2>" in {
    val gameServiceMock = mock[GameService]
    (gameServiceMock.move _)
      .expects(GameId("g1"), PlayerId("p1"), Coordinate(Row(1), Column(2)))
      .returning(IO(SuccessMove.GameInProgress.asRight))
    val routes = new GameApi(gameServiceMock)

    val response = routes.makeMoveRoutes.orNotFound
      .run(
        Request(
          method = Method.PUT,
          uri = uri"/api/games/g1",
          headers = Headers.of(Header("X-PLAYER-ID", "p1"))
        ).withEntity(json"""{"row":1, "col": 2}""")
      )
      .unsafeRunSync()

    response.status shouldBe Status.Ok
  }

  it should "response 200 when game has been finished" in {
    val gameServiceMock = mock[GameService]
    (gameServiceMock.move _)
      .expects(*, *, *)
      .returning(IO(SuccessMove.GameFinished.asRight))
    val routes = new GameApi(gameServiceMock)

    val response = routes.makeMoveRoutes.orNotFound
      .run(
        Request(
          method = Method.PUT,
          uri = uri"/api/games/g1",
          headers = Headers.of(Header("X-PLAYER-ID", "p1"))
        ).withEntity(json"""{"row":1, "col": 2}""")
      )
      .unsafeRunSync()

    response.status shouldBe Status.Ok
  }

  it should "response 400 with error when game is awaiting for another player" in {
    val gameServiceMock = mock[GameService]
    (gameServiceMock.move _)
      .expects(*, *, *)
      .returning(IO(MoveError.GameAwaitingForSecondPlayer.asLeft))
    val routes = new GameApi(gameServiceMock)

    val response = routes.makeMoveRoutes.orNotFound
      .run(
        Request(
          method = Method.PUT,
          uri = uri"/api/games/g1",
          headers = Headers.of(Header("X-PLAYER-ID", "p1"))
        ).withEntity(json"""{"row":1, "col": 2}""")
      )
      .unsafeRunSync()

    response.status shouldBe Status.Conflict
    response.as[Json].unsafeRunSync() shouldBe
      json"""
          {
           "error" : "GameAwaitingForSecondPlayer"
          }
          """
  }

  "GameApi.getDetails" should "response 200 with proper object" in {
    val gameServiceMock = mock[GameService]
    (gameServiceMock.getGame _)
      .expects(GameId("g1"))
      .returning(
        IO(
          (
            Game(
              GameId("g1"),
              PlayerId("owner1"),
              PlayerId("guest1").some,
              PlayerId("owner1").some
            ),
            List(
              Move(
                "p1",
                Coordinate(Row(1), Column(2)),
                "2020-06-30T14:30:00.000"
              ),
              Move(
                "p2",
                Coordinate(Row(0), Column(1)),
                "2020-04-30T14:30:00.000"
              )
            )
          ).some
        )
      )
    val routes = new GameApi(gameServiceMock)

    val response = routes.getGameRoutes.orNotFound
      .run(Request(method = Method.GET, uri = uri"/api/games/g1"))
      .unsafeRunSync()

    response.status shouldBe Status.Ok
    response.as[Json].unsafeRunSync() shouldBe
      json"""
          {
            "gameId" : "g1",
            "ownerId" : "owner1",
            "guestId" : "guest1",
            "winnerId" : "owner1",
            "moves" : [
              {
                "playerId" : "p1",
                "coordinate" : {
                  "row" : 1,
                  "col" : 2
                },
                "dateTime" : "2020-06-30T14:30:00"
              },
              {
                "playerId" : "p2",
                "coordinate" : {
                  "row" : 0,
                  "col" : 1
                },
                "dateTime" : "2020-04-30T14:30:00"
              }
            ]
          }
          """
  }

  it should "response 200 with minimal object" in {
    val gameServiceMock = mock[GameService]
    (gameServiceMock.getGame _)
      .expects(GameId("g1"))
      .returning(
        IO(
          (
            Game(GameId("g1"), PlayerId("owner1"), None, None),
            Nil
          ).some
        )
      )
    val routes = new GameApi(gameServiceMock)

    val response = routes.getGameRoutes.orNotFound
      .run(Request(method = Method.GET, uri = uri"/api/games/g1"))
      .unsafeRunSync()

    response.status shouldBe Status.Ok
    response.as[Json].unsafeRunSync() shouldBe
      json"""
          {
            "gameId" : "g1",
            "ownerId" : "owner1",
            "guestId" : null,
            "winnerId" : null,
            "moves" : []
          }
          """
  }

  it should "response 404 when no such game" in {
    val gameServiceMock = mock[GameService]
    (gameServiceMock.getGame _)
      .expects(GameId("g1"))
      .returning(IO(None))
    val routes = new GameApi(gameServiceMock)

    val response = routes.getGameRoutes.orNotFound
      .run(Request(method = Method.GET, uri = uri"/api/games/g1"))
      .unsafeRunSync()

    response.status shouldBe Status.NotFound
  }

}
