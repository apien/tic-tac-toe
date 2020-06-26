package com.github.apien.tictactoe.api.model

import io.circe.generic.semiauto._

case class GameApiDto(gameId: String, playerId: String)

object GameApiDto {
  implicit val gameApiDtoDecoder = deriveDecoder[GameApiDto]
  implicit val gameApiDtoEncoder = deriveEncoder[GameApiDto]
}