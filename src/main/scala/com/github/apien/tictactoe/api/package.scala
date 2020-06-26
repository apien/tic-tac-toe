package com.github.apien.tictactoe

import com.github.apien.tictactoe.domain.GameRepository.PlayerJoinError.{GameNoFreeSlot, GameNotExist}
import com.github.apien.tictactoe.domain.model.{GameId, PlayerId}
import io.circe.generic.extras.semiauto._
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import sttp.tapir.{Codec, DecodeResult}
import sttp.tapir.Codec.PlainCodec

package object api {

  implicit val gameIdEncoder = deriveUnwrappedEncoder[GameId]
  implicit val gameIdEDecoder = deriveUnwrappedDecoder[GameId]

  implicit val playerIdEncoder = deriveUnwrappedEncoder[PlayerId]
  implicit val playerIdDecoder = deriveUnwrappedDecoder[PlayerId]

  implicit val notFoundEncoider = deriveEncoder[GameNotExist]
  implicit val notFoundEDecoder = deriveDecoder[GameNotExist]

  implicit val gamesFreeSlotEncoider = deriveEncoder[GameNoFreeSlot]
  implicit val gamesFreeSlotDecoder = deriveDecoder[GameNoFreeSlot]

  implicit val gameIdCodec: PlainCodec[GameId] = {
    def decode(rawValue: String): DecodeResult[GameId] = DecodeResult.Value(GameId(rawValue))

    Codec.string.mapDecode(decode)(_.value)
  }
}
