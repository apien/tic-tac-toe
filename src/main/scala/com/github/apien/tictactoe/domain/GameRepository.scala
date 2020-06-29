package com.github.apien.tictactoe.domain

import com.github.apien.tictactoe.domain.model.{Game, GameId, PlayerId}
import doobie.ConnectionIO

trait GameRepository {

  /**
    * It creates new game board. It requires to join a second user to continue game.
    *
    * @param gameId  Game id to assign to the new game.
    * @param ownerId Id of owner of the game.
    * @return -.
    */
  def create(gameId: GameId, ownerId: PlayerId): ConnectionIO[Game]

  /**
    * Find [[Game]] by id.
    *
    * @param gameId Id of demanded game.
    * @return Found game.
    */
  def findById(gameId: GameId): ConnectionIO[Option[Game]]

  /**
    * Find [[Game]] by id and owner or guest id.
    *
    * @param gameId Id of demanded game.
    * @param playerId Id of the player assinged to the game.
    * @return Found game.
    */
  def findByIdAndPlayerId(gameId: GameId, playerId: PlayerId): ConnectionIO[Option[Game]]

  /**
    * Allows for guest to join to the existing game.
    *
    * @param gameId Game id.
    * @param guest  Id of the guest id.
    * @return
    */
  def joinPlayer(gameId: GameId, guest: PlayerId): ConnectionIO[Option[Game]]

  def setWinner(game:GameId, playerId: PlayerId) : ConnectionIO[Option[Game]]

  /**
    * Get all available games.
    *
    * @return -.
    */
  def getAll: ConnectionIO[List[Game]]
}

object GameRepository {

  sealed trait PlayerJoinError

  object PlayerJoinError {

    case class GameNotExist() extends PlayerJoinError

    case class GameNoFreeSlot() extends PlayerJoinError

  }

}
