package com.github.apien.tictactoe.domain.model

case class Game(id: GameId, owner: PlayerId, guest: Option[PlayerId])
