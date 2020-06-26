package com.github.apien.tictactoe

import cats.effect.ExitCode
import cats.implicits._
import monix.eval.TaskApp

object Main extends TaskApp {
  implicit val myScheduler = scheduler

  def run(args: List[String]) =
    QuickstartServer.stream.compile.drain.as(ExitCode.Success)
}