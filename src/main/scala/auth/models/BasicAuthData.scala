package auth.models

import zio.json.{DeriveJsonCodec, JsonCodec}

case class BasicAuthData(username: String, password: String)

object BasicAuthData {
  implicit val codec: JsonCodec[BasicAuthData] = DeriveJsonCodec.gen[BasicAuthData]
}
