package com.github.apien.tictactoe.domain
import cats.syntax.option._
package object model {

  case class GameId(value: String) extends AnyVal

  case class PlayerId(value: String) extends AnyVal

  case class Column(value: Int) extends AnyVal

  object Column {
    def parse(raw: Int): Option[Column] = {
      if (raw >= 0 && raw <= 2)
        Column(raw).some
      else None
    }
  }

  case class Row(value: Int) extends AnyVal

  object Row {
    def parse(raw: Int): Option[Row] = {
      if (raw >= 0 && raw <= 2)
        Row(raw).some
      else None
    }
  }

}
