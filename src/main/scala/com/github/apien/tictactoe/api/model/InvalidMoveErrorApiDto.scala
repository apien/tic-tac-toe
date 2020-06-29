package com.github.apien.tictactoe.api.model

import com.github.apien.tictactoe.domain.GameEngine.MoveError
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._

sealed trait MoveErrorApiDto
case class InvalidInputData() extends MoveErrorApiDto
case class InvalidMoveErrorApiDto(error: MoveError) extends MoveErrorApiDto

object InvalidMoveErrorApiDto {
  import com.github.apien.tictactoe.api.moveErrorEncoder
  import com.github.apien.tictactoe.api.moveErrorDecoder
  implicit lazy val decoder: Decoder[InvalidMoveErrorApiDto] = deriveDecoder[InvalidMoveErrorApiDto]
  implicit lazy val encoder: Encoder.AsObject[InvalidMoveErrorApiDto] = deriveEncoder[InvalidMoveErrorApiDto]
}

object InvalidInputData{
  implicit lazy val decoder: Decoder[InvalidInputData] = deriveDecoder[InvalidInputData]
  implicit lazy val encoder: Encoder.AsObject[InvalidInputData] = deriveEncoder[InvalidInputData]
}
