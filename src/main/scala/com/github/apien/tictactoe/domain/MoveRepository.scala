package com.github.apien.tictactoe.domain

import com.github.apien.tictactoe.domain.model.{GameId, Move}
import doobie.free.connection.ConnectionIO

trait MoveRepository {

  /**
    * Get all moves for the given game.
    * @param gameId Id of demanded game.
    * @return -.
    */
  def getAllForGame(gameId: GameId): ConnectionIO[List[Move]]

  /**
    * Persist a new move.
    * @param move Move to persist.
    * @return Persisted move.
    */
  def insert(move: Move): ConnectionIO[Move]
}
