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
    * Find [[Game]] by id.
    *
    * @param gameId Id of demanded game.
    * @return Found game.
    */
  def findById(gameId: GameId): Task[Option[Game]];

  /**
    * Allows for guest to join to the existing game.
    *
    * @param gameId Game id.
    * @param guest  Id of the guest id.
    * @return
    */
  def joinPlayer(gameId: GameId, guest: PlayerId): Task[Option[Game]]

  /**
    * Get all available games.
    *
    * @return -.
    */
  def getAll: Task[List[Game]]
}

object GameRepository {

  sealed trait PlayerJoinError

  object PlayerJoinError {

    case class GameNotExist() extends PlayerJoinError

    case class GameNoFreeSlot() extends PlayerJoinError

  }

}
