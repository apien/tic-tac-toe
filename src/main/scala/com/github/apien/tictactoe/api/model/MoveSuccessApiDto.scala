package com.github.apien.tictactoe.api.model

import com.github.apien.tictactoe.domain.GameEngine.SuccessMove
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}

case class MoveSuccessApiDto(status: SuccessMove)

object MoveSuccessApiDto{
  import com.github.apien.tictactoe.api.moveSuccessDecoder
  import com.github.apien.tictactoe.api.moveSuccessEncoder
  implicit lazy val decoder: Decoder[MoveSuccessApiDto] = deriveDecoder[MoveSuccessApiDto]
  implicit lazy val encoder: Encoder.AsObject[MoveSuccessApiDto] = deriveEncoder[MoveSuccessApiDto]
}


