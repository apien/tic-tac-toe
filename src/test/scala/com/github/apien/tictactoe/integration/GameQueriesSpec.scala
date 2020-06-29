package com.github.apien.tictactoe.integration

import cats.effect.{Blocker, IO}
import com.github.apien.tictactoe.integration.GameSqlRepository.GameQueries
import com.github.apien.tictactoe.test.TtcSpec
import doobie.scalatest.IOChecker
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor

class GameQueriesSpec extends TtcSpec with IOChecker {
  //TODO use test-containers
  override def transactor: doobie.Transactor[IO] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    s"jdbc:postgresql://localhost:15432/ttc",
    "rest_api",
    "tajne",
    Blocker.liftExecutionContext(
      ExecutionContexts.synchronous
    )
  )

  "Game query to get all objects" should "be properly mapped" in {
    check(GameQueries.getAll)
  }

  "Game query to insert new record" should "be properly mapped" in {
    check(GameQueries.insert("g1", "p1"))
  }

  "Game query to find object by id" should "be properly mapped" in {
    check(GameQueries.findById("g1"))
  }

}
