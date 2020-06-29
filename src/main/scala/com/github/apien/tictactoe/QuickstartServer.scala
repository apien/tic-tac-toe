package com.github.apien.tictactoe

import cats.effect.{Blocker, ConcurrentEffect, ContextShift, IO, Timer}
import cats.implicits._
import com.github.apien.tictactoe.api.GameApi
import com.github.apien.tictactoe.config.TtcConfig
import com.github.apien.tictactoe.domain.{GameService, MoveRepository}
import com.github.apien.tictactoe.integration.{GameSqlRepository, MoveSqlRepository}
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import fs2.Stream
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.server.blaze.BlazeServerBuilder
import sttp.tapir.docs.openapi._
import sttp.tapir.openapi.OpenAPI
import sttp.tapir.openapi.circe.yaml._
import sttp.tapir.swagger.http4s.SwaggerHttp4s

import scala.concurrent.ExecutionContext.global
object QuickstartServer {

  def stream(
      implicit time: Timer[IO],
      contextShift: ContextShift[IO],
      concurrentEffect: ConcurrentEffect[IO]
  ): Stream[IO, Nothing] = {

    for {
      config <- Stream(TtcConfig.load)
      tran <- Stream(
        Transactor.fromDriverManager[IO](
          "org.postgresql.Driver",
          s"jdbc:postgresql://${config.db.host}:${config.db.port}/${config.db.database}",
          config.db.user,
          config.db.password,
          Blocker.liftExecutionContext(
            ExecutionContexts.synchronous
          )
        )
      )
      gameRepository <- Stream(new GameSqlRepository())
      moveRepository <- Stream(new MoveSqlRepository())
      gameService <- Stream(new GameService(gameRepository, moveRepository, tran))
      gameRouter <- Stream.apply(new GameApi(gameService))
      //
      // generating the documentation in yml; extension methods come from imported packages
      openApiDocs: OpenAPI = List(gameRouter.addGame, gameRouter.joinGame, gameRouter.makeMove)
        .toOpenAPI("The tapir library", "1.0.0")
      openApiYml: String = openApiDocs.toYaml

      // Combine Service Routes into an HttpApp.
      // Can also be done via a Router if you
      // want to extract a segments not checked
      // in the underlying routes.
      cos = Router(
        "/" -> (gameRouter.addGameRoutes <+> gameRouter.joinGameRoutes <+> gameRouter.makeMoveRoutes <+> new SwaggerHttp4s(
          openApiYml
        ).routes[IO])
      ).orNotFound

      // With Middlewares in place
      //                                                             finalHttpApp = Logger.httpApp(true, true)(httpApp)

      exitCode <- BlazeServerBuilder[IO](global)
        .bindHttp(8080, "0.0.0.0")
        .withHttpApp(cos)
        .serve
    } yield exitCode
  }.drain
}
