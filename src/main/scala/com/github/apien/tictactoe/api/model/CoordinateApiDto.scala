package com.github.apien.tictactoe.api.model

import com.github.apien.tictactoe.domain.model.{Column, Coordinate, Row}

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

  implicit val decoder = deriveDecoder[CoordinateApiDto]
  implicit val encoder = deriveEncoder[CoordinateApiDto]

}
