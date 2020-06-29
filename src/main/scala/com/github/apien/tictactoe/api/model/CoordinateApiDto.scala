package com.github.apien.tictactoe.api.model

import com.github.apien.tictactoe.domain.model.{Column, Coordinate, Row}

case class CoordinateApiDto(row: Int, col: Int) {

  def toDomain: Coordinate = Coordinate(Row(row), Column(col))
}

object CoordinateApiDto {
  import io.circe.generic.semiauto._

  implicit val decoder = deriveDecoder[CoordinateApiDto]
  implicit val encoder = deriveEncoder[CoordinateApiDto]
}
