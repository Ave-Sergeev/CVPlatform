package auth.models

import zio.json._

@jsonMemberNames(SnakeCase)
case class UserInfo(@jsonField("preferred_username") username: String)

object UserInfo {
  implicit val codec: JsonCodec[UserInfo] = DeriveJsonCodec.gen[UserInfo]
}
