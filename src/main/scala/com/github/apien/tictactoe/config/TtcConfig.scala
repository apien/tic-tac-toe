package com.github.apien.tictactoe.config
import pureconfig._
import pureconfig.generic.auto._

case class TtcConfig(db: DatabaseConfig)

/**
  * Connection to our persistence.
  * @param host Host of the databse.
  * @param port Port of the database.
  * @param user User database name.
  * @param password User's password.
  * @param database Database name.
  */
case class DatabaseConfig(
    host: String,
    port: String,
    user: String,
    password: String,
    database: String
)

object TtcConfig {
  def load = ConfigSource.default.loadOrThrow[TtcConfig]

}
