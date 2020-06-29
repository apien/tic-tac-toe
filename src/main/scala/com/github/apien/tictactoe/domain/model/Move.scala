package com.github.apien.tictactoe.domain.model

import java.time.LocalDateTime

/**
  * It represents single move of a user on board.
  * @param playerId User identifier which make a move.
  * @param coordinate Coordinate on a board.
  * @param dateTime Date time when
  */
case class Move(
    playerId: PlayerId,
    coordinate: Coordinate,
    dateTime: LocalDateTime
)
