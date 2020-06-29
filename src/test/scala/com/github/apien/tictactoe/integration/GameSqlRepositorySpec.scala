package com.github.apien.tictactoe.integration

import com.dimafeng.testcontainers.ForEachTestContainer
import com.github.apien.tictactoe.domain.model.{Game, GameId, PlayerId}
import com.github.apien.tictactoe.test.DbItSpec
import doobie.implicits._
import cats.syntax.option._
class GameSqlRepositorySpec extends DbItSpec with ForEachTestContainer {

  private val gameRepository = new GameSqlRepository()

  "GameSqlRepository.create" should "insert object" in new PostgresFixture {
    gameRepository.create("g1", "p1").transact(transactor).unsafeRunSync()

    gameRepository.getAll
      .transact(transactor)
      .unsafeRunSync() shouldBe List(Game(GameId("g1"), PlayerId("p1"), None, None))
  }

  it should "return inserted object" in new PostgresFixture {
    gameRepository
      .create("g1", "p1")
      .transact(transactor)
      .unsafeRunSync() shouldBe Game(GameId("g1"), PlayerId("p1"), None, None)
  }

  "GameSqlRepository.findById" should "return found object" in new PostgresFixture {
    gameRepository.create("g1", "p1").transact(transactor).unsafeRunSync()
    gameRepository.create("g2", "p1").transact(transactor).unsafeRunSync()
    gameRepository.create("g3", "p1").transact(transactor).unsafeRunSync()

    gameRepository.findById("g1").transact(transactor).unsafeRunSync() shouldBe Game("g1", "p1", None, None).some
  }

  it should "return None when no such game" in new PostgresFixture {
    gameRepository.create("g1", "p1").transact(transactor).unsafeRunSync()

    gameRepository.findById("other").transact(transactor).unsafeRunSync() shouldBe None
  }

  "GameSqlRepository.joinPlayer" should "modify guest column" in new PostgresFixture {
    gameRepository.create("g1", "p1").transact(transactor).unsafeRunSync()
    gameRepository.create("g2", "p11").transact(transactor).unsafeRunSync()

    gameRepository.joinPlayer("g2", "my_guest_id").transact(transactor).unsafeRunSync()

    gameRepository.getAll.transact(transactor).unsafeRunSync() shouldBe List(
      Game("g1", "p1", None, None),
      Game("g2", "p11", PlayerId("my_guest_id").some, None)
    )
  }

  it should "return modified object" in new PostgresFixture {
    gameRepository.create("g2", "p1").transact(transactor).unsafeRunSync()

    gameRepository.joinPlayer("g2", "my_guest_id").transact(transactor).unsafeRunSync() shouldBe
      Game("g2", "p1", PlayerId("my_guest_id").some, None).some
  }

  it should "return None when no such game" in new PostgresFixture {
    gameRepository.joinPlayer("g2", "my_guest_id").transact(transactor).unsafeRunSync() shouldBe None
  }
}
