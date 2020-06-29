package com.github.apien.tictactoe.api.model

import com.github.apien.tictactoe.domain.GameEngine.MoveError
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._
case class MoveErrorApiDto(error: MoveError)

object MoveErrorApiDto {
  import com.github.apien.tictactoe.api.moveErrorEncoder
  import com.github.apien.tictactoe.api.moveErrorDecoder
  implicit lazy val decoder: Decoder[MoveErrorApiDto] = deriveDecoder[MoveErrorApiDto]
  implicit lazy val encoder: Encoder.AsObject[MoveErrorApiDto] = deriveEncoder[MoveErrorApiDto]
}
