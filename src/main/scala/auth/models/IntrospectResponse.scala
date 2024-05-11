package auth.models

import zio.json._
import zio.schema.{DeriveSchema, Schema}

sealed trait IntrospectResponse

case class IntrospectResponseFailure(active: Boolean) extends IntrospectResponse

object IntrospectResponseFailure {
  implicit val schema: Schema[IntrospectResponseFailure] = DeriveSchema.gen[IntrospectResponseFailure]
}

@jsonMemberNames(SnakeCase)
case class IntrospectResponseSuccess(
    exp: Long,
    username: String,
    realmAccess: RealmAccess,
    active: Boolean
) extends IntrospectResponse

object IntrospectResponseSuccess {
  implicit val codec: Schema[IntrospectResponseSuccess] = DeriveSchema.gen[IntrospectResponseSuccess]
}

object IntrospectResponse {
  implicit val schema: Schema[IntrospectResponse] = DeriveSchema.gen[IntrospectResponse]

  implicit val decoder: JsonDecoder[IntrospectResponse] =
    List[JsonDecoder[IntrospectResponse]](
      DeriveJsonDecoder.gen[IntrospectResponseSuccess].widen,
      DeriveJsonDecoder.gen[IntrospectResponseFailure].widen
    ).reduce(_ orElse _)
}
