package com.github.apien.tictactoe

import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import monix.eval.TaskApp

object Main extends TaskApp {
  def run(args: List[String]) =
    QuickstartServer.stream.compile.drain.as(ExitCode.Success)
}