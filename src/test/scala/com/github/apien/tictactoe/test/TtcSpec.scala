package com.github.apien.tictactoe.test

import java.time.LocalDateTime

import cats.effect.IO
import com.github.apien.tictactoe.domain.model.{GameId, PlayerId}
import doobie.util.ExecutionContexts
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.language.implicitConversions

trait TtcSpec extends AnyFlatSpec with Matchers {

  implicit val cs = IO.contextShift(ExecutionContexts.synchronous)

  implicit def stringToGameId(value: String): GameId = GameId(value)

  implicit def stringToPlayerId(value: String): PlayerId = PlayerId(value)

  implicit def stringToLocalDateTime(value: String): LocalDateTime = LocalDateTime.parse(value)
}
