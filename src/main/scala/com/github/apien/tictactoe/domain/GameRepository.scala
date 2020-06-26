package com.github.apien.tictactoe.domain

import com.github.apien.tictactoe.domain.model.{Game, GameId, PlayerId}
import monix.eval.Task

trait GameRepository {

  /**
    * It creates new game board. It requires to join a second user to continue game.
    *
    * @param gameId  Game id to assign to the new game.
    * @param ownerId Id of owner of the game.
    * @return -.
    */
  def create(gameId: GameId, ownerId: PlayerId): Task[Game]

  /**
    * Get all available games.
    *
    * @return -.
    */
  def getAll: Task[List[Game]]
}
