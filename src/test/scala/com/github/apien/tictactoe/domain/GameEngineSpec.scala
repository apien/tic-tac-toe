package com.github.apien.tictactoe.domain

import cats.syntax.either._
import cats.syntax.option._
import com.github.apien.tictactoe.domain.GameEngine.{MoveError, SuccessMove}
import com.github.apien.tictactoe.domain.model._
import com.github.apien.tictactoe.test.TtcSpec

class GameEngineSpec extends TtcSpec {

  private val player1 = PlayerId("p1")
  private val player2 = PlayerId("p2")

  "GameService" should "return error when gam is awaiting for second player" in {
    val game = Game(GameId("g1"), player1, None, None)
    val engine = new GameEngine(game, Nil)

    engine.makeMove(Move(player1, Coordinate(Row(0), Column(0)), "2020-06-30T14:30:15.312")) shouldBe
      MoveError.GameAwaitingForSecondPlayer.asLeft
  }

  it should "return error when given field is already taken" in {
    val game = Game(GameId("g1"), player1, player2.some, None)
    val engine = new GameEngine(
      game,
      List(Move(player1, Coordinate(Row(0), Column(0)), "2020-06-30T14:30:15.312"))
    )

    engine.makeMove(Move(player2, Coordinate(Row(0), Column(0)), "2020-06-30T15:30:15.312")) shouldBe
      MoveError.FieldIsNotFree.asLeft
  }

  it should "return error when it another player turn" in {
    val game = Game(GameId("g1"), player1, player2.some, None)
    val engine = new GameEngine(
      game,
      List(Move(player1, Coordinate(Row(0), Column(0)), "2020-06-30T14:30:15.312"))
    )

    engine.makeMove(Move(player1, Coordinate(Row(1), Column(1)), "2020-06-30T15:30:15.312")) shouldBe
      MoveError.NotPlayerTurn.asLeft
  }

  it should "return error when game is already finished" in {
    val game = Game(GameId("g1"), player1, player2.some, player1.some)
    val engine = new GameEngine(
      game,
      List(Move(player1, Coordinate(Row(0), Column(0)), "2020-06-30T14:30:15.312"))
    )

    engine.makeMove(Move(player2, Coordinate(Row(1), Column(1)), "2020-06-30T15:30:15.312")) shouldBe
      MoveError.GameAlreadyFinished.asLeft
  }

  it should "return error when more free fields left" in {
    val game = Game(GameId("g1"), player1, player2.some, None)
    val engine = new GameEngine(
      game,
      List(
        Move(player1, Coordinate(Row(0), Column(0)), "2020-06-30T14:30:15.312"),
        Move(player2, Coordinate(Row(1), Column(0)), "2020-06-30T14:31:15.312"),
        Move(player1, Coordinate(Row(0), Column(1)), "2020-06-30T14:32:15.312"),
        Move(player2, Coordinate(Row(1), Column(1)), "2020-06-30T14:33:15.312"),
        Move(player1, Coordinate(Row(0), Column(2)), "2020-06-30T14:34:15.312"),
        Move(player2, Coordinate(Row(1), Column(2)), "2020-06-30T14:35:15.312"),
        Move(player1, Coordinate(Row(2), Column(0)), "2020-06-30T14:36:15.312"),
        Move(player2, Coordinate(Row(2), Column(1)), "2020-06-30T14:37:15.312"),
        Move(player1, Coordinate(Row(2), Column(2)), "2020-06-30T14:38:15.312"),
      )
    )

    engine.makeMove(Move(player2, Coordinate(Row(1), Column(1)), "2020-06-30T15:30:15.312")) shouldBe
      MoveError.NoMoreMoves.asLeft
  }

  it should "return information about finished game (there is a winner) when game finished and not more moves" in {
    val game = Game(GameId("g1"), player1, player2.some, player1.some)
    val engine = new GameEngine(
      game,
      List(
        Move(player1, Coordinate(Row(0), Column(0)), "2020-06-30T14:30:15.312"),
        Move(player2, Coordinate(Row(1), Column(0)), "2020-06-30T14:31:15.312"),
        Move(player1, Coordinate(Row(0), Column(1)), "2020-06-30T14:32:15.312"),
        Move(player2, Coordinate(Row(1), Column(1)), "2020-06-30T14:33:15.312"),
        Move(player1, Coordinate(Row(0), Column(2)), "2020-06-30T14:34:15.312"),
        Move(player2, Coordinate(Row(1), Column(2)), "2020-06-30T14:35:15.312"),
        Move(player1, Coordinate(Row(2), Column(0)), "2020-06-30T14:36:15.312"),
        Move(player2, Coordinate(Row(2), Column(1)), "2020-06-30T14:37:15.312"),
        Move(player1, Coordinate(Row(2), Column(2)), "2020-06-30T14:38:15.312"),
      )
    )

    engine.makeMove(Move(player2, Coordinate(Row(1), Column(1)), "2020-06-30T15:30:15.312")) shouldBe
      MoveError.GameAlreadyFinished.asLeft
  }

  it should "return status when move has been made" in {
    val game = Game(GameId("g1"), player1, player2.some, None)
    val engine = new GameEngine(
      game,
      List(Move(player1, Coordinate(Row(0), Column(0)), "2020-06-30T14:30:15.312"))
    )

    engine.makeMove(Move(player2, Coordinate(Row(1), Column(1)), "2020-06-30T15:30:15.312")) shouldBe
      SuccessMove.GameInProgress.asRight
  }

  it should "return status when move finished the game (in given column)" in {
    val game = Game(GameId("g1"), player1, player2.some, None)
    val engine = new GameEngine(
      game,
      List(
        Move(player1, Coordinate(Row(0), Column(0)), "2020-06-30T14:30:00.000"),
        Move(player2, Coordinate(Row(0), Column(2)), "2020-06-30T14:31:00.000"),
        Move(player1, Coordinate(Row(1), Column(0)), "2020-06-30T14:32:00.000"),
        Move(player2, Coordinate(Row(1), Column(2)), "2020-06-30T14:33:00.000")
      )
    )

    engine.makeMove(Move(player1, Coordinate(Row(2), Column(0)), "2020-06-30T14:34:00.000")) shouldBe
      SuccessMove.GameFinished.asRight
  }

  it should "return status when move finished the game (in given row)" in {
    val game = Game(GameId("g1"), player1, player2.some, None)
    val engine = new GameEngine(
      game,
      List(
        Move(player1, Coordinate(Row(0), Column(0)), "2020-06-30T14:30:00.000"),
        Move(player2, Coordinate(Row(2), Column(0)), "2020-06-30T14:31:00.000"),
        Move(player1, Coordinate(Row(0), Column(1)), "2020-06-30T14:32:00.000"),
        Move(player2, Coordinate(Row(2), Column(1)), "2020-06-30T14:33:00.000")
      )
    )

    engine.makeMove(Move(player1, Coordinate(Row(0), Column(2)), "2020-06-30T14:34:00.000")) shouldBe
      SuccessMove.GameFinished.asRight
  }

  it should "return status when move finished the game (diagonal from left down cornet to top right)" in {
    val game = Game(GameId("g1"), player1, player2.some, None)
    val engine = new GameEngine(
      game,
      List(
        Move(player1, Coordinate(Row(2), Column(0)), "2020-06-30T14:30:00.000"),
        Move(player2, Coordinate(Row(0), Column(0)), "2020-06-30T14:31:00.000"),
        Move(player1, Coordinate(Row(1), Column(1)), "2020-06-30T14:32:00.000"),
        Move(player2, Coordinate(Row(1), Column(0)), "2020-06-30T14:33:00.000")
      )
    )

    engine.makeMove(Move(player1, Coordinate(Row(0), Column(2)), "2020-06-30T14:34:00.000")) shouldBe
      SuccessMove.GameFinished.asRight
  }

  it should "return status when move finished the game (diagonal from left top cornet to down right)" in {
    val game = Game(GameId("g1"), player1, player2.some, None)
    val engine = new GameEngine(
      game,
      List(
        Move(player1, Coordinate(Row(0), Column(0)), "2020-06-30T14:30:00.000"),
        Move(player2, Coordinate(Row(0), Column(2)), "2020-06-30T14:31:00.000"),
        Move(player1, Coordinate(Row(1), Column(1)), "2020-06-30T14:32:00.000"),
        Move(player2, Coordinate(Row(1), Column(0)), "2020-06-30T14:33:00.000")
      )
    )

    engine.makeMove(Move(player1, Coordinate(Row(2), Column(2)), "2020-06-30T14:34:00.000")) shouldBe
      SuccessMove.GameFinished.asRight
  }

  "GameEngine.leftDownDiagonalCoordinates" should "properly determine coordinates of top left diagonal" in {
    GameEngine.leftDownDiagonalCoordinates shouldBe List(
      Coordinate(Row(2), Column(0)),
      Coordinate(Row(1), Column(1)),
      Coordinate(Row(0), Column(2))
    )
  }

  "GameEngine.leftTopDiagonalCoordinates" should "properly determine coordinates of top left diagonal" in {
    GameEngine.leftTopDiagonalCoordinates shouldBe List(
      Coordinate(Row(0), Column(0)),
      Coordinate(Row(1), Column(1)),
      Coordinate(Row(2), Column(2))
    )
  }

}
