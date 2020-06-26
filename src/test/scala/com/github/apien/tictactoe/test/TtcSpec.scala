package com.github.apien.tictactoe.test

import com.github.apien.tictactoe.domain.model.{GameId, PlayerId}
import monix.execution.Scheduler
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import scala.language.implicitConversions

trait TtcSpec extends AnyFlatSpec with Matchers {

  protected implicit val scheduler: Scheduler = Scheduler.global

  implicit def stringToGameId(value: String): GameId = GameId(value)

  implicit def stringToPlayerId(value: String): PlayerId = PlayerId(value)
}
