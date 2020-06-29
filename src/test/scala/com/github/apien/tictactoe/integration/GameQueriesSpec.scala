package com.github.apien.tictactoe.integration

import cats.effect.IO
import com.dimafeng.testcontainers.{Container, ForAllTestContainer, ForEachTestContainer, PostgreSQLContainer}
import com.github.apien.tictactoe.integration.GameSqlRepository.GameQueries
import com.github.apien.tictactoe.test.{DbItSpec, TtcSpec}
import doobie.scalatest.IOChecker

class GameQueriesSpec extends DbItSpec with IOChecker with ForAllTestContainer {

  override def transactor: doobie.Transactor[IO] = createTransactor(container)

  "Game query to get all objects" should "be properly mapped" in new PostgresFixture {
    check(GameQueries.getAll)
  }

  "Game query to insert new record" should "be properly mapped" in new PostgresFixture {
    check(GameQueries.insert("g1", "p1"))
  }

  "Game query to find object by id" should "be properly mapped" in new PostgresFixture {
    check(GameQueries.findById("g1"))
  }
}
