package service.models

import zio.json.{DeriveJsonCodec, JsonCodec}

import java.util.UUID

case class Profile(
    id: UUID,
    name: String,
    link: String
)

object Profile {
  implicit val codec: JsonCodec[Profile] = DeriveJsonCodec.gen[Profile]
}
