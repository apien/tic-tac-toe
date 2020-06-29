package com.github.apien.tictactoe.domain

package object model {

  case class GameId(value: String) extends AnyVal

  case class PlayerId(value: String) extends AnyVal

  case class Column(value : Int) extends AnyVal

  case class Row(value: Int) extends AnyVal

}
