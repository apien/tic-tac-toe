package com.github.apien.tictactoe.domain

import com.github.apien.tictactoe.domain.model.{Game, GameId, PlayerId}
import monix.eval.Task;
import java.util.UUID;

class GameService(gameRepository: GameRepository) {

  /**
    * Create a new game.
    *
    * @return Game and player identifiers.
    */
  def createNew: Task[(GameId, PlayerId)] =
    for {
      game <- gameRepository.create(GameId(UUID.randomUUID().toString), PlayerId(UUID.randomUUID().toString))
    } yield (game.id, game.owner)
}
