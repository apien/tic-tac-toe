package com.github.apien.tictactoe.domain

import com.github.apien.tictactoe.domain.GameEngine.{MoveError, SuccessMove}
import com.github.apien.tictactoe.domain.model._
import cats.syntax.either._

/**
  * It implements of a game logic.
  * @param game A game object.
  * @param previousMoves All moves made to now in context of the game.
  */
class GameEngine(game: Game, previousMoves: List[Move]) {

  import com.github.apien.tictactoe.domain.GameEngine.BoardSize

  /**
    * It validate a user move and verify if move is eligible or maybe finish the game.
    * @param move User game move.
    * @return Result of the operation.
    *         For more details possible operation result please look on the [[MoveError]] or [[SuccessMove]].
    */
  def makeMove(move: Move): Either[MoveError, SuccessMove] = {
    if (game.winner.isDefined)
      MoveError.GameAlreadyFinished(game.winner.get).asLeft
    else if (game.guest.isEmpty)
      MoveError.GameAwaitingForSecondPlayer.asLeft
    else internalMove(move)
  }

  private def internalMove(move: Move): Either[MoveError, SuccessMove] = {
    for {
      _ <- isUserTurn(move.playerId)
      _ <- isFreeSlot(move.coordinate)
      currentMoves = previousMoves :+ move
      stateOfGame = validateStateAfterMove(currentMoves, move)
    } yield stateOfGame
  }

  private def isFreeSlot(coordinate: Coordinate): Either[MoveError, Unit] =
    previousMoves
      .find(_.coordinate == coordinate)
      .fold(().asRight[MoveError])(_ => MoveError.FieldIsNotFree.asLeft[Unit])

  private def isUserTurn(playerId: PlayerId): Either[MoveError, Unit] =
    if (previousMoves.isEmpty || previousMoves.maxBy(_.dateTime).playerId != playerId)
      ().asRight
    else
      MoveError.NotPlayerTurn.asLeft

  private def validateStateAfterMove(moves: List[Move], move: Move): SuccessMove = {
    def isRowFinishGame(row: Row): Boolean = {
      val rowCells = moves.filter(_.coordinate.row == row)
      isWinningCells(rowCells)
    }

    def isColumnFinishGame(col: Column): Boolean = {
      val columnCells = moves.filter(_.coordinate.col == col)
      isWinningCells(columnCells)
    }

    def isDiagonalsFinishGame: Boolean = {
      val leftUpDiagonal = GameEngine.leftTopDiagonalCoordinates
        .flatMap(cord => moves.find(_.coordinate == cord))
      val leftDownDiagonal = GameEngine.leftDownDiagonalCoordinates
        .flatMap(cord => moves.find(_.coordinate == cord))

      isWinningCells(leftUpDiagonal) || isWinningCells(leftDownDiagonal)
    }

    def isWinningCells(filteredMoves: List[Move]): Boolean =
      filteredMoves.size == BoardSize && filteredMoves.map(_.playerId).distinct.size == 1

    if (isColumnFinishGame(move.coordinate.col) || isRowFinishGame(move.coordinate.row) || isDiagonalsFinishGame)
      SuccessMove.GameFinished(move.playerId)
    else
      SuccessMove.GameInProgress

  }

}

object GameEngine {

  /**
    * Size of the board.
    */
  val BoardSize: Int = 3

  /**
    * Coordinates of diagonal from the top left corner.
    */
  val leftTopDiagonalCoordinates: List[Coordinate] = (0 until BoardSize).toList
    .map(i => Coordinate(Row(i), Column(i)))

  /**
    * Coordinates of diagonal from the down left corner.
    */
  val leftDownDiagonalCoordinates: List[Coordinate] = (0 until BoardSize).toList
    .map(i => Coordinate(Row(BoardSize - 1 - i), Column(i)))

  sealed trait SuccessMove

  object SuccessMove {

    /**
      * Given move is valid but it did not finish the game.
      */
    case object GameInProgress extends SuccessMove

    /**
      * Given move is valid and it did finish the game.
      * @param playerId Identifier of the user which finished the game.
      */
    case class GameFinished(playerId: PlayerId) extends SuccessMove
  }

  /**
    * Family of errors when move is not possible because of some reason.
    */
  sealed trait MoveError

  object MoveError {

    /**
      * It is not turn of the user. Other user needs to make move.
      */
    case object NotPlayerTurn extends MoveError

    /**
      * One of the player already selected the field in one of the previous move.
      */
    case object FieldIsNotFree extends MoveError

    /**
      * User can not make any move because game is still awaiting for second player.
      */
    case object GameAwaitingForSecondPlayer extends MoveError

    /**
      * Player is not available to make the move because game has been finished
      * @param playerId Id of user which finished game in previous move.
      */
    case class GameAlreadyFinished(playerId: PlayerId) extends MoveError
  }

}
