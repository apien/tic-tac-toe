package com.github.apien.tictactoe.integration

import com.github.apien.tictactoe.domain.model.{GameId, Move}
import com.github.apien.tictactoe.domain.{MoveRepository, model}
import com.github.apien.tictactoe.integration.MoveSqlRepository.MoveQueries
import doobie.free.connection.ConnectionIO
import doobie.implicits._
import doobie.implicits.javasql._
import doobie.implicits.javatime._
class MoveSqlRepository extends MoveRepository {

  override def getAllForGame(gameId: model.GameId): ConnectionIO[List[Move]] =
    MoveQueries
      .getAllForGame(gameId)
      .to[List]

  override def insert(move: Move): ConnectionIO[Move] =
    MoveQueries
      .insert(move)
      .withUniqueGeneratedKeys("player_id", "row", "col", "date_time")
}

object MoveSqlRepository {

  object MoveQueries {

    def getAllForGame(gameId: GameId) =
      sql"""
           |select player_id, row, col, date_time
           |from move m
           |LEFT JOIN game g ON g.owner_id = m.player_id OR g.guest_id = m.player_id
           |WHERE g.id = $gameId
           |ORDER BY date_time ASC
           |""".stripMargin
        .query[Move]

    def insert(move: Move) =
      sql"""
           | INSERT INTO move(row, col, player_id, date_time)
           | VALUES (${move.coordinate.row}, ${move.coordinate.col}, ${move.playerId}, ${move.dateTime})
       """.stripMargin.update

  }
}
