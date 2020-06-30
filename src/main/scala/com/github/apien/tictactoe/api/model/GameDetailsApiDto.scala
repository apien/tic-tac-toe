package com.github.apien.tictactoe.api.model

import com.github.apien.tictactoe.domain.model.{Game, GameId, Move, PlayerId}
import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto._

case class GameDetailsApiDto(
    gameId: GameId,
    ownerId: PlayerId,
    guestId: Option[PlayerId],
    winnerId: Option[PlayerId],
    moves: List[MoveApiDto]
)

object GameDetailsApiDto {
  import com.github.apien.tictactoe.api.gameIdEDecoder
  import com.github.apien.tictactoe.api.gameIdEncoder
  import com.github.apien.tictactoe.api.playerIdDecoder
  import com.github.apien.tictactoe.api.playerIdEncoder

  implicit val encoder: Encoder.AsObject[GameDetailsApiDto] = deriveEncoder[GameDetailsApiDto]
  implicit val decoder: Decoder[GameDetailsApiDto] = deriveDecoder[GameDetailsApiDto]

  def fromDomain(game: Game, moves: List[Move]): GameDetailsApiDto =
    GameDetailsApiDto(
      game.id,
      game.owner,
      game.guest,
      game.winner,
      moves.map(MoveApiDto.fromDomain)
    )
}
