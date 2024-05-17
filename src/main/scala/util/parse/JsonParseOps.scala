package util.parse

import exception.Exceptions.BodyParsingException
import zio.{Task, ZIO}
import zio.json.{DecoderOps, JsonDecoder}

object JsonParseOps {
  def bodyParse[A: JsonDecoder](body: String): Task[A] = ZIO
    .fromEither(body.fromJson[A])
    .mapError(err => BodyParsingException(s"Cannot decode body: $err"))
}
