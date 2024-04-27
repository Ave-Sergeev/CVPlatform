package service

import zio.json.{DeriveJsonCodec, JsonCodec}

import java.util.UUID

object Model {
  case class Profile(id: UUID, name: String, link: String)

  object Profile {
    implicit val codec: JsonCodec[Profile] = DeriveJsonCodec.gen[Profile]
  }
}
