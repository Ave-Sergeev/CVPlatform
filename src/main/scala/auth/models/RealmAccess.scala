package auth.models

import zio.json.{jsonMemberNames, DeriveJsonCodec, JsonCodec, SnakeCase}

@jsonMemberNames(SnakeCase)
case class RealmAccess(
    roles: List[String]
)

object RealmAccess {
  implicit val codec: JsonCodec[RealmAccess] = DeriveJsonCodec.gen[RealmAccess]
}
