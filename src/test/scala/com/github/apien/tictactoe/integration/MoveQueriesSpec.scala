package com.github.apien.tictactoe.integration

import cats.effect.{Blocker, IO}
import com.github.apien.tictactoe.domain.model.{Column, Coordinate, Move, Row}
import com.github.apien.tictactoe.integration.GameSqlRepository.GameQueries
import com.github.apien.tictactoe.integration.MoveSqlRepository.MoveQueries
import com.github.apien.tictactoe.test.TtcSpec
import doobie.scalatest.IOChecker
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor

class MoveQueriesSpec extends TtcSpec with IOChecker {
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

  "Move query to get all" should "be properly mapped" in {
    check(MoveQueries.getAllForGame("g1"))
  }

  "Move query to insert a new record" should "be properly mapped" in {
    check(MoveQueries.insert(Move("p1",Coordinate(Row(1),Column(1)),"2020-06-30T15:30:15.312")))
  }
}
