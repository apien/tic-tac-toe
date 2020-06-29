package com.github.apien.tictactoe

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] =
    QuickstartServer.stream.compile.drain.as(ExitCode.Success)
}
