package com.github.apien.tictactoe.test

import cats.effect.{Blocker, IO}
import com.dimafeng.testcontainers.PostgreSQLContainer
import doobie.util.ExecutionContexts
import doobie.util.transactor.Transactor
import org.flywaydb.core.Flyway

trait DbItSpec extends TtcSpec {

  implicit val container: PostgreSQLContainer = PostgreSQLContainer()

  def createTransactor(container: PostgreSQLContainer): Transactor[IO] = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver",
    container.jdbcUrl,
    container.username,
    container.password,
    Blocker.liftExecutionContext(
      ExecutionContexts.synchronous
    )
  )

  abstract class PostgresFixture(implicit val container: PostgreSQLContainer) {
    private def migrate(jdbc: String, username: String, password: String): Int = {
      Flyway
        .configure()
        .cleanDisabled(false)
        .baselineOnMigrate(true)
        .dataSource(
          jdbc,
          username,
          password
        )
        .load()
        .migrate
    }

    lazy val transactor: Transactor[IO] = createTransactor(container)

    migrate(container.jdbcUrl, container.username, container.password)
  }

}
