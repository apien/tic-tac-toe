package com.github.apien.tictactoe.api.model

import com.github.apien.tictactoe.domain.model.{Column, Coordinate, Row}
import io.circe.{Decoder, Encoder}

case class CoordinateApiDto(row: Int, col: Int) {

  def toDomain: Option[Coordinate] =
    for {
      rowDomain <- Row.parse(row)
      columnDomain <- Column.parse(col)
    } yield Coordinate(rowDomain, columnDomain)
}

object CoordinateApiDto {
  import io.circe.generic.semiauto._
  import com.github.apien.tictactoe.api.rowDecoder
  import com.github.apien.tictactoe.api.rowEncoder
  import com.github.apien.tictactoe.api.columnDecoder
  import com.github.apien.tictactoe.api.columnEncoder

  implicit val decoder: Decoder[CoordinateApiDto] = deriveDecoder[CoordinateApiDto]
  implicit val encoder: Encoder.AsObject[CoordinateApiDto] = deriveEncoder[CoordinateApiDto]

  def fromDomain(domain: Coordinate): CoordinateApiDto = CoordinateApiDto(domain.row.value, domain.col.value)

}
