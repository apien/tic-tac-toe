package com.github.apien.tictactoe.api.model

import java.time.LocalDateTime

import com.github.apien.tictactoe.domain.model.{Move, PlayerId}
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._

case class MoveApiDto(playerId: PlayerId, coordinate: CoordinateApiDto, dateTime:LocalDateTime)

object MoveApiDto {
  import com.github.apien.tictactoe.api.playerIdDecoder
  import com.github.apien.tictactoe.api.playerIdEncoder
  implicit val gameApiDtoDecoder: Decoder[MoveApiDto] = deriveDecoder[MoveApiDto]
  implicit val gameApiDtoEncoder: Encoder.AsObject[MoveApiDto] = deriveEncoder[MoveApiDto]

  def fromDomain(move: Move): MoveApiDto =
    MoveApiDto(
      move.playerId,
      CoordinateApiDto.fromDomain(move.coordinate),
      move.dateTime
    )
}
