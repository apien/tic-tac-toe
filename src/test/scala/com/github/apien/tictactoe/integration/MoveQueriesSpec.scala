package com.github.apien.tictactoe.integration

import cats.effect.IO
import com.dimafeng.testcontainers.ForAllTestContainer
import com.github.apien.tictactoe.domain.model.{Column, Coordinate, Move, Row}
import com.github.apien.tictactoe.integration.MoveSqlRepository.MoveQueries
import com.github.apien.tictactoe.test.DbItSpec
import doobie.scalatest.IOChecker

class MoveQueriesSpec extends DbItSpec with IOChecker with ForAllTestContainer {

  override def transactor: doobie.Transactor[IO] = createTransactor(container)

  "Move query to get all" should "be properly mapped" in new PostgresFixture {
    check(MoveQueries.getAllForGame("g1"))
  }

  "Move query to insert a new record" should "be properly mapped" in new PostgresFixture {
    check(MoveQueries.insert(Move("p1", Coordinate(Row(1), Column(1)), "2020-06-30T15:30:15.312")))
  }

}
